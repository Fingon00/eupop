package ootie.service.player;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class PlayerSetupService {

    public static void setupPlayer(
            String color, String realm, Player player, Game game, GenericInteractionCreateEvent event) {
        player.setRealm(realm);
        player.setColor(color);
    }
}
