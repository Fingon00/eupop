package ootie.game.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.helpers.Constants;
import ootie.helpers.Storage;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import ootie.service.game.GameUndoNameService;
import ootie.spring.websocket.WebSocketNotifier;

@UtilityClass
class GameUndoService {

    static int createUndoCopy(String gameName) {
        return GameFileLockManager.wrapWithReadLock(gameName, () -> {
            int latestIndex = cleanUpExcessUndoFilesAndReturnLatestIndex(gameName);
            if (latestIndex < 0) return -1;
            File gameFile = Storage.getGameFile(gameName + Constants.JSON);
            if (!gameFile.exists()) return -1;
            try {
                int createdUndoIndex = latestIndex + 1;
                Path mapUndoStorage = Storage.getGameUndo(gameName, getUndoFileName(gameName, createdUndoIndex));
                Files.copy(gameFile.toPath(), mapUndoStorage, StandardCopyOption.REPLACE_EXISTING);
                return createdUndoIndex;
            } catch (Exception e) {
                BotLogger.error("Error copying undo file for " + gameName, e);
                return -1;
            }
        });
    }

    private static int cleanUpExcessUndoFilesAndReturnLatestIndex(String gameName) {
        try {
            List<Integer> undoNumbers = GameUndoNameService.getSortedUndoNumbers(gameName);
            if (undoNumbers.isEmpty()) return 0;

            int maxUndoNumber = undoNumbers.getLast();
            ManagedGame managedGame = GameManager.getManagedGame(gameName);
            int maxUndoFilesPerGame = managedGame != null && managedGame.isHasEnded() ? 10 : 100;
            int oldestUndoNumberThatShouldExist = maxUndoNumber - maxUndoFilesPerGame;

            undoNumbers.stream()
                    .filter(undoIndex -> undoIndex < oldestUndoNumberThatShouldExist)
                    .map(undoIndex -> getUndoFileName(gameName, undoIndex))
                    .forEach(fileName -> deleteFile(Storage.getGameUndo(gameName, fileName)));

            return maxUndoNumber;
        } catch (Exception e) {
            BotLogger.error("Error trying clean up excess undo files for: " + gameName, e);
        }
        return -1;
    }

    @Nullable
    static Game undo(Game game) {
        int latestUndoIndex = cleanUpExcessUndoFilesAndReturnLatestIndex(game.getName());
        return lockAndUndo(game, latestUndoIndex - 1, latestUndoIndex);
    }

    @Nullable
    static Game undo(Game game, int undoIndex) {
        if (undoIndex <= 0) return null;
        int latestUndoIndex = cleanUpExcessUndoFilesAndReturnLatestIndex(game.getName());
        return lockAndUndo(game, undoIndex, latestUndoIndex);
    }

    private static Game lockAndUndo(Game gameToUndo, int undoIndex, int latestUndoIndex) {
        return GameFileLockManager.wrapWithWriteLock(
                gameToUndo.getName(), () -> undo(gameToUndo, undoIndex, latestUndoIndex));
    }

    private static Game undo(Game gameToUndo, int undoIndex, int latestUndoIndex) {
        if (latestUndoIndex <= 1) return null;
        String gameName = gameToUndo.getName();
        try {
            File currentGameFile = Storage.getGameFile(gameName + Constants.JSON);
            if (!currentGameFile.exists()) {
                BotLogger.error(new LogOrigin(gameToUndo), "Game file for " + gameName + " doesn't exist!");
                return null;
            }
            Game savedButtonsGame = null;
            if (undoIndex != latestUndoIndex - 1) {
                replaceGameFileWithUndo(gameName, undoIndex + 1, currentGameFile.toPath());
                savedButtonsGame = GameLoadService.load(gameName);
            }

            replaceGameFileWithUndo(gameName, undoIndex, currentGameFile.toPath());
            Game loadedGame = GameLoadService.load(gameName);
            if (loadedGame == null) { // rollback if we failed to load the undo
                replaceGameFileWithUndo(gameName, latestUndoIndex, currentGameFile.toPath());
                return null;
            }
            WebSocketNotifier.notifyGameStateChange(loadedGame);

            sendUndoConfirmationMessage(gameToUndo, undoIndex, latestUndoIndex);
            return loadedGame;
        } catch (Exception e) {
            BotLogger.error(new LogOrigin(gameToUndo), "Error trying to undo: " + gameName, e);
            return null;
        }
    }

    private static void replaceGameFileWithUndo(String gameName, int undoIndex, Path gameFilePath) throws IOException {
        Path undoFilePath = Storage.getGameUndo(gameName, getUndoFileName(gameName, undoIndex));
        Files.copy(undoFilePath, gameFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static String getUndoFileName(String gameName, int undoIndex) {
        return gameName + "_" + undoIndex + Constants.JSON;
    }

    private static void sendUndoConfirmationMessage(Game gameToUndo, int undoIndex, int latestUndoIndex) {

        Map<String, String> undoNamesToCommandText =
                GameUndoNameService.getUndoNamesToCommandText(gameToUndo, latestUndoIndex - undoIndex);
        List<String> undoCommands = new ArrayList<>();
        for (int i = latestUndoIndex; i > undoIndex; i--) {
            String fileName = getUndoFileName(gameToUndo.getName(), i);
            undoCommands.add(undoNamesToCommandText.get(fileName));
            Path currentUndo = Storage.getGameUndo(gameToUndo.getName(), fileName);
            if (!currentUndo.toFile().delete()) {
                BotLogger.error(new LogOrigin(gameToUndo), "Failed to delete undo file: " + currentUndo);
            }
        }

        sendUndoConfirmationMessage(gameToUndo, undoIndex, latestUndoIndex, undoCommands);
    }

    private static void sendUndoConfirmationMessage(
            Game game, int undoIndex, int latestUndoIndex, List<String> undoCommands) {
        StringBuilder sb = new StringBuilder("Rolled back to save `")
                .append(undoIndex)
                .append("` from `")
                .append(latestUndoIndex)
                .append("`:\n");

        String gameName = game.getName();
        for (int i = 0; i < undoCommands.size(); i++) {
            sb.append("> `")
                    .append(latestUndoIndex - i)
                    .append("` ")
                    .append(undoCommands.get(i))
                    .append('\n');
        }
    }

    private static void deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            BotLogger.error("Error trying to delete file: " + path, e);
        }
    }

    static Game loadUndoForMissingGame(String gameName) {
        List<Integer> sortedUndoNumbers = GameUndoNameService.getSortedUndoNumbers(gameName);
        if (sortedUndoNumbers.isEmpty()) {
            BotLogger.warning("Attempted to load undo for missing game, but one did not exist: " + gameName);
            return null;
        }
        int latestUndoIndex = sortedUndoNumbers.getLast();
        File currentGameFile = Storage.getGameFile(gameName + Constants.JSON);
        try {
            replaceGameFileWithUndo(gameName, latestUndoIndex, currentGameFile.toPath());
            Game loadedGame = GameLoadService.load(gameName);
            if (loadedGame != null) {
                WebSocketNotifier.notifyGameStateChange(loadedGame);
            }
            return loadedGame;
        } catch (IOException e) {
            BotLogger.error("Error trying to undo for missing game: " + gameName, e);
        }
        return null;
    }
}
