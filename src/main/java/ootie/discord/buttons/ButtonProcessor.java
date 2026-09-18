package ootie.discord.buttons;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import ootie.discord.listeners.context.ButtonContext;
import ootie.discord.routing.AnnotationHandler;
import ootie.discord.routing.ButtonHandler;
import ootie.discord.routing.HandlerRegistry;
import ootie.executors.ExecutionLockType;
import ootie.executors.ExecutorServiceManager;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.DateTimeHelper;
import ootie.helpers.TimedRunnable;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import ootie.logging.RollbarManager;
import ootie.message.MessageHelper;
import ootie.service.game.GameNameService;

@UtilityClass
public class ButtonProcessor {

    private static final HandlerRegistry<ButtonContext> registry =
            AnnotationHandler.buildHandlerRegistry(ButtonContext.class, ButtonHandler.class);
    private static final ButtonRuntimeWarningService runtimeWarningService = new ButtonRuntimeWarningService();

    public static void checkButtonHandlersSetup() {
        // if (registry.getSize() == 0) {
        //     throw new IllegalStateException("No button handlers were registered");
        // }
    }

    public static void queue(ButtonInteractionEvent event) {
        String gameName = GameNameService.getGameNameFromChannel(event);
        String componentId = event.getButton().getCustomId();
        ExecutionLockType lockType = registry.isSave(componentId) ? ExecutionLockType.WRITE : ExecutionLockType.READ;
        ExecutorServiceManager.runAsyncWithLock(
                eventToString(event, gameName), gameName, event.getMessageChannel(), () -> process(event), lockType);
    }

    private static String eventToString(ButtonInteractionEvent event, String gameName) {
        return "ButtonProcessor task for `" + event.getUser().getEffectiveName() + "`"
                + (gameName == null ? "" : " in `" + gameName + "`")
                + ": "
                + event.getButton().getCustomId();
    }

    private static void process(ButtonInteractionEvent event) {
        long processStartTime = System.currentTimeMillis();

        ButtonContext context = new ButtonContext(event);
        if (!context.isValid()) return;

        long beforeTime = System.currentTimeMillis();
        log(event);
        long logRuntime = System.currentTimeMillis() - beforeTime;

        long resolveRuntime = 0;
        long saveRuntime = 0;
        try {
            beforeTime = System.currentTimeMillis();
            resolveButtonInteractionEvent(context);
            resolveRuntime = System.currentTimeMillis() - beforeTime;

            beforeTime = System.currentTimeMillis();
            context.save();
            saveRuntime = System.currentTimeMillis() - beforeTime;

        } catch (Exception e) {
            BotLogger.error(new LogOrigin(event, context), "Something went wrong with button interaction", e);
        } finally {
            RollbarManager.clear();
        }

        long contextCreationRuntime = context.getCreationEndTime() - context.getCreationStartTime();
        runtimeWarningService.submitNewRuntime(
                event,
                processStartTime,
                System.currentTimeMillis(),
                contextCreationRuntime,
                logRuntime,
                resolveRuntime,
                saveRuntime);
    }

    private static void log(ButtonInteractionEvent event) {
        // TODO: These timings are temporary to track down any spikes...
        int warningThresholdSeconds = 1;
        new TimedRunnable("ButtonProcessor BotLogger log", warningThresholdSeconds, () -> BotLogger.logButton(event))
                .run();

        new TimedRunnable("ButtonProcessor Rollbar setup", warningThresholdSeconds, () -> {
                    RollbarManager.putInteractionMetadata("button", event);
                    RollbarManager.put("button_id", event.getButton().getCustomId());
                    RollbarManager.put("game_name", GameNameService.getGameNameFromChannel(event));
                })
                .run();

        new TimedRunnable("ButtonProcessor user settings save", warningThresholdSeconds, () -> {
                    User user = event.getUser();
                    int currentHourUTC = ZonedDateTime.now(ZoneId.of("UTC")).getHour();
                })
                .run();
    }

    private static void resolveButtonInteractionEvent(ButtonContext context) {
        // pull values from context for easier access
        ButtonInteractionEvent event = context.getEvent();
        Player player = context.getPlayer();
        String buttonID = context.getButtonID();
        Game game = context.getGame();
        MessageChannel privateChannel = context.getPrivateChannel();
        MessageChannel mainGameChannel = context.getMainGameChannel();

        // Check the list of ButtonHandlers first
        if (registry.handle(buttonID, context)) return;
        MessageHelper.sendMessageToEventChannel(
                event, "Button " + event.getButton().getCustomId() + " pressed. This button does not do anything.");
    }

    private static void trackButtonHandler(String handlerId) {
        RollbarManager.put("button_handler_id", handlerId);
    }

    public static String getButtonProcessingStatistics() {
        var decimalFormatter = new DecimalFormat("#.##");
        double thresholdMissPercent = runtimeWarningService.getThresholdMissPercent();
        return "Button Processor Statistics: " + DateTimeHelper.getCurrentTimestamp()
                + "\n> Total button presses: "
                + runtimeWarningService.getRuntimeSubmissionCount()
                + "\n> Threshold misses: "
                + decimalFormatter.format(thresholdMissPercent) + "% ("
                + runtimeWarningService.getRuntimeThresholdMissCount() + ")"
                + "\n> Average preprocessing time: "
                + decimalFormatter.format(runtimeWarningService.getAveragePreprocessingTime()) + "ms"
                + "\n> Average processing time: "
                + decimalFormatter.format(runtimeWarningService.getAverageProcessingTime()) + "ms";
    }
}
