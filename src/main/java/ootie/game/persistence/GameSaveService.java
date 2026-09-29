package ootie.game.persistence;

import static ootie.game.persistence.GamePersistenceKeys.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.GameProperties;
import ootie.game.Player;
import ootie.game.PlayerProperties;
import ootie.helpers.Constants;
import ootie.helpers.Storage;
import ootie.json.JsonMapperManager;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import org.springframework.beans.BeanUtils;
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

        Path gameSavePath = Storage.getGamePath(game.getName() + Constants.JSON);
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
        // try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

        //     writer.write(game.getName().toLowerCase());
        //     writer.write(System.lineSeparator());
        //     saveGameInfo(writer, game);
        // }
        Map<String, PlayerSaveData> players = game.getPlayers().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Player player = entry.getValue();
                    PlayerProperties properties = new PlayerProperties();
                    BeanUtils.copyProperties(player, properties);
                    return new PlayerSaveData(properties);
                }));

        GameProperties properties = new GameProperties();
        BeanUtils.copyProperties(game, properties);
        GameSaveData saveData = new GameSaveData(properties, players);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            mapper.writeValue(writer, saveData);
        }
    }

    static boolean delete(String gameName) {
        return GameFileLockManager.wrapWithWriteLock(gameName, () -> {
            File mapStorage = Storage.getGameFile(gameName + Constants.JSON);
            if (!mapStorage.exists()) {
                return false;
            }
            File deletedMapStorage =
                    Storage.getDeletedGame(gameName + "_" + System.currentTimeMillis() + Constants.JSON);
            return mapStorage.renameTo(deletedMapStorage);
        });
    }
}
