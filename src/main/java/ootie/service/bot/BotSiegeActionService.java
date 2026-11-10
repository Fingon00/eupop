package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotSiegeActionService {
    public static void siegeAction(Player bot, Game game){
        //TODO figure out how to siege, calculate 3 tax value
        boolean hasTownsWithUnrestOrRebelsInArea = false; //TODO: Figure this out
        if(bot.getAtWarWith().isEmpty() && !hasTownsWithUnrestOrRebelsInArea){
            bot.getArmy1().setLocation(null);
        }
        //end turn

    }
}
