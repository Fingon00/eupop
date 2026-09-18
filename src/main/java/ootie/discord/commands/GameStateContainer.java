package ootie.discord.commands;

import ootie.game.Game;
import ootie.game.Player;

public interface GameStateContainer {

    boolean isSaveGame();

    Game getGame();

    Player getPlayer();
}
