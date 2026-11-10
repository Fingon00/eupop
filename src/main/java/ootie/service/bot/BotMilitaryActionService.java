package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotMilitaryActionService {
    public static boolean militaryAction(Player bot, Game game){
        boolean resolved = false;

        if(bot.getAtWarWith().isEmpty()){
            if(bot.getClaims().isEmpty()){
                boolean adjacentBattleGrounds = false;
                String opponent = "";
                if(adjacentBattleGrounds){
                    //Place claim
                    opponent = ""; //select opponent based on claim
                }else{
                    boolean botHasDSinAdjArea = false;
                    if(botHasDSinAdjArea){
                        opponent = ""; //select opponent based on DS
                    }else{
                        opponent = ""; // select opponent based on MTC. If NPR, place claim
                        if(game.getPlayerByRealmOrColor(opponent) != null){
                            boolean hasCB = false;
                            if(hasCB){
                                //eligible target node
                            }else{
                                
                            }
                        }
                    }
                }
            }
        }else{
            
        }

        return resolved;

    }
}
