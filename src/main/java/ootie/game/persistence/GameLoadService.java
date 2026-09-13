package ootie.game.persistence;

import static ootie.game.persistence.GamePersistenceKeys.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.Storage;
import ootie.helpers.StringHelper;
import ootie.logging.BotLogger;
import org.jetbrains.annotations.NotNull;

@UtilityClass
class GameLoadService {

    private static final Pattern PEEKED_OBJECTIVE_PATTERN = Pattern.compile("(?>([a-z_]+):((?>\\d+,)+);)");

    private static final Pattern PATTERN = Pattern.compile("—");
    private static final String GAME_FILE_EXTENSION = Constants.TXT;

    static List<String> loadGameNames() {
        try (Stream<Path> pathStream = Files.list(Storage.getGamesDirectory().toPath())) {
            return pathStream
                    .filter(path -> path.toString().toLowerCase().endsWith(GAME_FILE_EXTENSION))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(GameLoadService::stripGameFileExtension)
                    .map(String::toLowerCase)
                    // newer games first
                    .sorted(Comparator.reverseOrder())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Game load(String gameName) {
        return GameFileLockManager.wrapWithReadLock(gameName, () -> {
            File gameFile = Storage.getGameFile(gameName + GAME_FILE_EXTENSION);
            return readGame(gameFile);
        });
    }

    private static String stripGameFileExtension(String fileName) {
        return fileName.substring(0, fileName.length() - GAME_FILE_EXTENSION.length());
    }

    @Nullable
    private static Game readGame(@NotNull File gameFile) {
        if (!gameFile.exists()) {
            BotLogger.critical("Could not load map, file does not exist: " + gameFile.getAbsolutePath());
            return null;
        }
        try {
            Game game = new Game();
            Iterator<String> gameFileLines = Files.readAllLines(gameFile.toPath(), Charset.defaultCharset())
                    .listIterator();

            game.setName(gameFileLines.next().toLowerCase());
            while (gameFileLines.hasNext()) {
                String data = gameFileLines.next();
                if (MAPINFO.equals(data)) {
                    continue;
                }
                if (ENDMAPINFO.equals(data)) {
                    break;
                }

                while (gameFileLines.hasNext()) {
                    data = gameFileLines.next();
                    if (GAMEINFO.equals(data)) {
                        continue;
                    }
                    if (ENDGAMEINFO.equals(data)) {
                        break;
                    }
                    readGameInfo(game, data);
                }

                while (gameFileLines.hasNext()) {
                    String tmpData = gameFileLines.next();
                    if (PLAYERINFO.equals(tmpData)) {
                        continue;
                    }
                    if (ENDPLAYERINFO.equals(tmpData)) {
                        break;
                    }
                    Player player = null;
                    while (gameFileLines.hasNext()) {
                        data = tmpData != null ? tmpData : gameFileLines.next();
                        tmpData = null;
                        if (PLAYER.equals(data)) {
                            // player = game.addPlayer(gameFileLines.next(), gameFileLines.next());
                            continue;
                        }
                        if (ENDPLAYER.equals(data)) {
                            break;
                        }
                        readPlayerInfo(player, data, game);
                    }
                }
            }
            return game;
        } catch (Exception e) {
            BotLogger.critical(
                    "Encountered fatal error loading game file: " + gameFile.getName() + ". Load aborted.", e);
            return null;
        }
    }

    private static void readGameInfo(Game game, String data) {
        String[] tokenizer = data.split(" ", 2);
        if (tokenizer.length == 2) {
            String identification = tokenizer[0];
            String info = tokenizer[1];
            switch (identification) {
            }
        }
    }

    private static List<String> getCardList(String tokenizer) {
        StringTokenizer cards = new StringTokenizer(tokenizer, ",");
        List<String> cardList = new ArrayList<>();
        while (cards.hasMoreTokens()) {
            cardList.add(cards.nextToken());
        }
        return cardList;
    }

    private static List<String> getParsedStrList(String tokenizer) {
        StringTokenizer data = new StringTokenizer(tokenizer, ",");
        List<String> output = new ArrayList<>();
        while (data.hasMoreTokens()) {
            String value = StringHelper.unescape(data.nextToken());
            output.add(value);
        }
        return output;
    }

    private static Map<String, String> getParsedStrStrMap(String tokenizer) {
        StringTokenizer mapdata = new StringTokenizer(tokenizer, ";");
        Map<String, String> data = new LinkedHashMap<>();
        while (mapdata.hasMoreTokens()) {
            StringTokenizer entry = new StringTokenizer(mapdata.nextToken(), ",");
            String id = entry.nextToken();
            String val = entry.nextToken();
            data.put(id, val);
        }
        return data;
    }

    private static Map<String, Boolean> getParsedStrBoolMap(String tokenizer) {
        StringTokenizer mapdata = new StringTokenizer(tokenizer, ";");
        Map<String, Boolean> cards = new LinkedHashMap<>();
        while (mapdata.hasMoreTokens()) {
            StringTokenizer entry = new StringTokenizer(mapdata.nextToken(), ",");
            String id = entry.nextToken();
            Boolean val = Boolean.parseBoolean(entry.nextToken());
            cards.put(id, val);
        }
        return cards;
    }

    private static Map<String, Integer> getParsedCards(String tokenizer) {
        StringTokenizer actionCardToken = new StringTokenizer(tokenizer, ";");
        Map<String, Integer> cards = new LinkedHashMap<>();
        while (actionCardToken.hasMoreTokens()) {
            StringTokenizer cardInfo = new StringTokenizer(actionCardToken.nextToken(), ",");
            String id = cardInfo.nextToken();
            Integer index = Integer.parseInt(cardInfo.nextToken());
            cards.put(id, index);
        }
        return cards;
    }

    private static void readPlayerInfo(Player player, String data, Game game) {
        StringTokenizer tokenizer = new StringTokenizer(data, " ");
        if (tokenizer.countTokens() == 2) {
            data = tokenizer.nextToken();
            switch (data) {
            }
        }
    }
}
