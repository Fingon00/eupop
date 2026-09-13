package ootie.spring.context;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.logging.RollbarManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class RequestContext {

    // TODO: Debate combining this with the Command/Button processing context
    private static final ThreadLocal<Game> game = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> saveGame = ThreadLocal.withInitial(() -> Boolean.TRUE);

    @NotNull
    public static String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    static void setGame(Game game) {
        RequestContext.game.set(game);
        if (game != null) {
            RollbarManager.put("game_name", game.getName());
        }
    }

    public static Game getGame() {
        return game.get();
    }

    public static Player getPlayer() {
        return getGame().getPlayer(getUserId());
    }

    static boolean shouldSaveGame() {
        return getGame() != null && saveGame.get();
    }

    static void setSaveGame(boolean saveGame) {
        RequestContext.saveGame.set(saveGame);
    }

    static void clearContext() {
        game.remove();
        saveGame.remove();
    }
}
