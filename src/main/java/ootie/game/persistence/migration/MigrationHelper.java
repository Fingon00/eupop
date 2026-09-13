package ootie.game.persistence.migration;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.logging.BotLogger;

@UtilityClass
class MigrationHelper {


    private static <K, V> boolean replaceKey(Map<K, V> map, K toReplace, K replacement) {
        if (map.containsKey(toReplace)) {
            V value = map.get(toReplace);
            map.put(replacement, value);
            map.remove(toReplace);
            return true;
        }
        return false;
    }

    private static <K> boolean replace(Set<K> set, K toReplace, K replacement) {
        if (set.remove(toReplace)) {
            set.add(replacement);
            return true;
        }
        return false;
    }

    private static <K> boolean replace(List<K> list, K toReplace, K replacement) {
        boolean replaced = false;
        int index = list.indexOf(toReplace);
        while (index > -1) {
            list.set(index, replacement);
            replaced = true;
            index = list.indexOf(toReplace);
        }
        return replaced;
    }

}
