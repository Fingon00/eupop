package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotNavalActionService {
    public static boolean navalAction(Player bot, Game game){
        boolean resolved = false;
        
        boolean adjacentHostileSeazones = false;//TODO: Check this
        boolean placedShip = false;
        if(adjacentHostileSeazones){
            //TODO: Place ship in such a sea zone
            //TODO Naval battle
            resolved = true;
        }else{
            boolean adjacentEmptySeazones = false;
            if(adjacentEmptySeazones){
                placeShipNode(bot, game, ""); //TODO: Find the sea zone
            }else{
                if(bot.getLightShips().size() > 4){
                    return pirateNode(bot, game, placedShip);
                }else{
                    boolean adjacentSZWOShips = false;
                    if(adjacentSZWOShips){
                        return placeShipNode(bot, game, "");//TODO: Find the sea zone;
                    }else{
                        return tradeCardNode(bot, game, placedShip);
                    }
                }
            }
        }


        return resolved;

    }

    public static boolean placeShipNode(Player bot, Game game, String seazoneID){
        //TODO: Place ship in such a sea zone
        return pirateNode(bot, game, true);
    }

    public static boolean pirateNode(Player bot, Game game, boolean placedShip){
        boolean hasShipAdjacentToPirate = false;
        if(hasShipAdjacentToPirate){
            //TODO: remove pirate
            return true;
        }else{
            return tradeCardNode(bot, game, placedShip);
        }
    }

    public static boolean tradeCardNode(Player bot, Game game, boolean placedShip){
        
        //TODO: Draw a trade card and resolve
        //TODO: Moved merchants of bots that gained botpower to topmost valid node on the card
        if(!placedShip){
            //TODO: check if under 3 colonial claims and a ship in or adjacent to sea zone, return explore if so
            return true;
        }
        return true;
        
    }
}
