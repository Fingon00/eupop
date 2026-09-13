package ootie.discord.listeners;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.JDA;

@UtilityClass
public class ListenerManager {

    public static void registerListeners(JDA jda) {
        jda.addEventListener(
                // Priority Listeners First
                new SlashCommandListener(),
                ButtonListener.getInstance(),
                new AutoCompleteListener());
    }
}
