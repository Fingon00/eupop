package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotColonizeActionService {
    public static boolean colonizeAction(Player bot, Game game){
        boolean resolved = false;

        boolean colonialClaimConnectedToBotCapital = false; //TODO: Figure this out
        if(bot.getColonists() > 1 && colonialClaimConnectedToBotCapital){
            //Explore
        }else{
            resolved = true;
            if(bot.getColonists() > 2){
                bot.setColonists(bot.getColonists()-3);
                bot.setBotPower(bot.getBotPower()+1);
            }else{
                bot.setColonists(0);
            }
            //TODO: replace colonial claim with small town
        }

        return resolved;

    }

}
