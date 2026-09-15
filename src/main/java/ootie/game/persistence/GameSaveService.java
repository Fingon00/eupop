package ootie.game.persistence;

import static ootie.game.persistence.GamePersistenceKeys.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.Storage;
import ootie.helpers.StringHelper;
import ootie.json.JsonMapperManager;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
class GameSaveService {

    private static final JsonMapper mapper = JsonMapperManager.basic().rebuild().build();

    static boolean save(Game game, String reason) {
        return GameFileLockManager.wrapWithWriteLock(game.getName(), () -> {
            return save(game);
        });
    }

    private static boolean save(Game game) {

        Path gameSavePath = Storage.getGamePath(game.getName() + Constants.TXT);
        Path gameSaveDirectory = gameSavePath.getParent();
        Path temporarySavePath = null;
        try {
            temporarySavePath = Files.createTempFile(gameSaveDirectory, game.getName(), ".tmp");

            saveGame(game, temporarySavePath);

            PosixFileSystemUtility.setPermissionsIfPosix(temporarySavePath);

            Files.move(temporarySavePath, gameSavePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            BotLogger.critical(new LogOrigin(game), "Could not save map: " + game.getName(), e);
            return false;
        } finally {
            deleteTemporaryFileIfNeeded(temporarySavePath);
        }

        int undoIndex = GameUndoService.createUndoCopy(game.getName());
        return true;
    }

    private static void deleteTemporaryFileIfNeeded(Path temporarySavePath) {
        if (temporarySavePath == null) return;
        try {
            Files.deleteIfExists(temporarySavePath);
        } catch (IOException e) {
            BotLogger.error("Failed to delete temporary file: " + temporarySavePath, e);
        }
    }

    private static void saveGame(Game game, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

            writer.write(game.getName().toLowerCase());
            writer.write(System.lineSeparator());
            saveGameInfo(writer, game);
        }
    }

    private static void saveGameInfo(Writer writer, Game game) throws IOException {
        writer.write(MAPINFO);
        writer.write(System.lineSeparator());

        writer.write(GAMEINFO);
        writer.write(System.lineSeparator());
        // game information

        // Player information
        writer.write(PLAYERINFO);
        writer.write(System.lineSeparator());
        Map<String, Player> players = game.getPlayers();
        for (Map.Entry<String, Player> playerEntry : players.entrySet()) {
            writer.write(PLAYER);
            writer.write(System.lineSeparator());

            Player player = playerEntry.getValue();
            writer.write(player.getUserID());
            writer.write(System.lineSeparator());
            writer.write(player.getUserName());
            writer.write(System.lineSeparator());
            writer.write(ENDPLAYER);
            writer.write(System.lineSeparator());
        }

        writer.write(ENDPLAYERINFO);
        writer.write(System.lineSeparator());

        writer.write(ENDMAPINFO);
        writer.write(System.lineSeparator());
    }

    private static void writeCards(Map<String, Integer> cardList, Writer writer, String saveID) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : cardList.entrySet()) {
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append(";");
        }
        writer.write(saveID + " " + sb);
        writer.write(System.lineSeparator());
    }

    private static void writeStrList(Writer writer, String field, List<String> vals) throws IOException {
        List<String> escaped = vals.stream()
                .map(v -> v == null ? "" : v)
                .map(StringHelper::escape)
                .toList();
        writer.write(field + " " + String.join(",", escaped));
        writer.write(System.lineSeparator());
    }

    private static void writeStrLine(Writer writer, String field, String str) throws IOException {
        writer.write(field + " " + str);
        writer.write(System.lineSeparator());
    }

    private static void writeIntLine(Writer writer, String field, int val) throws IOException {
        writer.write(field + " " + val);
        writer.write(System.lineSeparator());
    }

    private static void writeBoolLine(Writer writer, String field, boolean bool) throws IOException {
        String output = Boolean.toString(bool);
        writer.write(field + " " + output);
        writer.write(System.lineSeparator());
    }

    /** Assumes the map is already properly escaped */
    private static void writeStrStrMap(Writer writer, String field, Map<String, String> map) throws IOException {
        List<String> entries = map.entrySet().stream()
                .map(e -> e.getKey() + "," + e.getValue())
                .toList();
        writer.write(field + " " + String.join(";", entries));
        writer.write(System.lineSeparator());
    }

    private static void writeStrIntMap(Writer writer, String field, Map<String, Integer> map) throws IOException {
        List<String> entries = map.entrySet().stream()
                .map(e -> e.getKey() + "," + e.getValue().toString())
                .toList();
        writer.write(field + " " + String.join(";", entries));
        writer.write(System.lineSeparator());
    }

    private static void writeStrBoolMap(Writer writer, String field, Map<String, Boolean> map) throws IOException {
        List<String> entries = map.entrySet().stream()
                .map(e -> e.getKey() + "," + (e.getValue().toString()))
                .toList();
        writer.write(field + " " + String.join(";", entries));
        writer.write(System.lineSeparator());
    }

    private static void writeCardsStringList(Map<String, List<String>> cardList, Writer writer, String saveID)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : cardList.entrySet()) {
            sb.append(entry.getKey())
                    .append(",")
                    .append(String.join(",", entry.getValue()))
                    .append(";");
        }
        writer.write(saveID + " " + sb);
        writer.write(System.lineSeparator());
    }

    private static void writeCardsStrings(Map<String, String> cardList, Writer writer, String saveID)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : cardList.entrySet()) {
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append(";");
        }
        writer.write(saveID + " " + sb);
        writer.write(System.lineSeparator());
    }

    private static String getStringRepresentationOfMap(Map<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    static boolean delete(String gameName) {
        return GameFileLockManager.wrapWithWriteLock(gameName, () -> {
            File mapStorage = Storage.getGameFile(gameName + Constants.TXT);
            if (!mapStorage.exists()) {
                return false;
            }
            File deletedMapStorage =
                    Storage.getDeletedGame(gameName + "_" + System.currentTimeMillis() + Constants.TXT);
            return mapStorage.renameTo(deletedMapStorage);
        });
    }
}
