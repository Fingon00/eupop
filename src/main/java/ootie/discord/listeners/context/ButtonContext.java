package ootie.discord.listeners.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Getter
public class ButtonContext extends ListenerContext {

    private String messageID;

    @JsonIgnore
    public String getButtonID() {
        return componentID;
    }

    public ButtonInteractionEvent getEvent() {
        if (event instanceof ButtonInteractionEvent button) return button;
        return null;
    }

    public String getContextType() {
        return "button";
    }

    public ButtonContext(ButtonInteractionEvent event) {
        super(event, event.getButton().getCustomId());
        if (!isValid()) {
            return;
        }

        // Proceed with additional button things
        messageID = event.getMessageId();
    }

    @Override
    public void save() {
        if (!shouldSave) {
            return;
        }
        super.save();
    }
}
