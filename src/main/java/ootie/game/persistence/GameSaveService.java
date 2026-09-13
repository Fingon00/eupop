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

        writer.write(Constants.SO + " " + String.join(",", game.getSecretObjectives()));
        writer.write(System.lineSeparator());

        writer.write(Constants.AC + " " + String.join(",", game.getActionCards()));
        writer.write(System.lineSeparator());

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

            writer.write(Constants.FACTION + " " + player.getFaction());
            writer.write(System.lineSeparator());
            writer.write(Constants.FACTION_EMOJI + " " + player.getFactionEmojiRaw());
            writer.write(System.lineSeparator());
            String displayName =
                    player.getDisplayName() != null ? player.getDisplayName().replace(" ", "_") : "null";
            writer.write(Constants.FACTION_DISPLAY_NAME + " " + displayName);
            writer.write(System.lineSeparator());
            // TODO Remove when no longer relevant
            String playerColor = player.getColor();
            if (player.getFaction() == null || "null".equals(player.getFaction())) {
                playerColor = "null";
            }
            writer.write(Constants.COLOR + " " + playerColor);
            writer.write(System.lineSeparator());

            writer.write(Constants.DECAL_SET + " " + player.getDecalSet());
            writer.write(System.lineSeparator());

            writer.write(Constants.STATS_ANCHOR_LOCATION + " " + player.getPlayerStatsAnchorPosition());
            writer.write(System.lineSeparator());

            writer.write(Constants.STATS_TRACKED_USER_ID + " " + player.getStatsTrackedUserID());
            writer.write(System.lineSeparator());

            writer.write(Constants.STATS_TRACKED_USER_NAME + " "
                    + player.getStatsTrackedUserName().replace(" ", "----"));
            writer.write(System.lineSeparator());

            writer.write(Constants.HS_TILE_POSITION + " " + player.getHomeSystemPosition());
            writer.write(System.lineSeparator());

            writer.write(Constants.ALLIANCE_MEMBERS + " " + player.getAllianceMembers());
            writer.write(System.lineSeparator());

            writer.write(Constants.ROLE_FOR_COMMUNITY + " " + player.getRoleIDForCommunity());
            writer.write(System.lineSeparator());

            writer.write(Constants.PLAYER_PRIVATE_CHANNEL + " " + player.getPrivateChannelID());
            writer.write(System.lineSeparator());

            String fogColor = player.getFogFilter() == null ? "" : player.getFogFilter();
            writer.write(Constants.FOG_FILTER + " " + fogColor);
            writer.write(System.lineSeparator());

            writer.write(Constants.PASSED + " " + player.isPassed());
            writer.write(System.lineSeparator());

            writer.write(Constants.READY_TO_PASS_BAG + " " + player.isReadyToPassBag());
            writer.write(System.lineSeparator());

            writer.write(Constants.AUTO_PASS_WHENS_N_AFTERS + " " + player.isAutoPassOnWhensAfters());
            writer.write(System.lineSeparator());

            writer.write(Constants.SEARCH_WARRANT + " " + player.isSearchWarrant());
            writer.write(System.lineSeparator());

            writer.write(Constants.DUMMY + " " + player.isDummy());
            writer.write(System.lineSeparator());

            writer.write(Constants.NPC + " " + player.isNpc());
            writer.write(System.lineSeparator());

            writer.write(Constants.ELIMINATED + " " + player.isEliminated());
            writer.write(System.lineSeparator());

            writer.write(Constants.NOTEPAD + " " + player.getNotes());
            writer.write(System.lineSeparator());

            // BENTOR Ancient Blueprints
            writer.write(Constants.BENTOR_HAS_FOUND_CFRAG + " " + player.isHasFoundCulFrag());
            writer.write(System.lineSeparator());
            writer.write(Constants.BENTOR_HAS_FOUND_HFRAG + " " + player.isHasFoundHazFrag());
            writer.write(System.lineSeparator());
            writer.write(Constants.BENTOR_HAS_FOUND_IFRAG + " " + player.isHasFoundIndFrag());
            writer.write(System.lineSeparator());
            writer.write(Constants.BENTOR_HAS_FOUND_UFRAG + " " + player.isHasFoundUnkFrag());
            writer.write(System.lineSeparator());

            // LANEFIR ATS Armaments count
            writer.write(Constants.LANEFIR_ATS_COUNT + " " + player.getAtsCount());
            writer.write(System.lineSeparator());

            writer.write(Constants.PILLAGE_COUNT + " " + player.getPillageCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.SARWEEN_COUNT + " " + player.getSarweenCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.GHOST_COMMANDER_COUNT + " " + player.getGhostCommanderCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.MAGEN_INFANTRY_COUNT + " " + player.getMagenInfantryCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.PATH_TOKEN_COUNT + " " + player.getPathTokenCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.HONOR_COUNT + " " + player.getHonorCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.DISHONOR_COUNT + " " + player.getDishonorCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.STEELBALANCE_COUNT + " " + player.getSteelbalanceCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.STARBALANCE_COUNT + " " + player.getStarbalanceCounter());
            writer.write(System.lineSeparator());

            writer.write(Constants.HARVEST_COUNT + " " + player.getHarvestCounter());
            writer.write(System.lineSeparator());

            writeCards(player.getActionCards(), writer, Constants.AC);
            writeCards(player.getEvents(), writer, Constants.EVENTS);
            writeCards(player.getPromissoryNotes(), writer, Constants.PROMISSORY_NOTES);

            writer.write(Constants.PROMISSORY_NOTES_OWNED + " " + String.join(",", player.getPromissoryNotesOwned()));
            writer.write(System.lineSeparator());

            writer.write(Constants.PROMISSORY_NOTES_PLAY_AREA + " "
                    + String.join(",", player.getPromissoryNotesInPlayArea()));
            writer.write(System.lineSeparator());

            writer.write(Constants.UNITS_OWNED + " " + String.join(",", player.getUnitsOwned()));
            writer.write(System.lineSeparator());

            writeCards(player.getTrapCards(), writer, Constants.LIZHO_TRAP_CARDS);
            writeCardsStrings(player.getTrapCardsPlanets(), writer, Constants.LIZHO_TRAP_PLANETS);

            writeCards(player.getPlotCardsRaw(), writer, Constants.PLOT_CARDS);
            writeCardsStringList(player.getPlotCardsFactionsRaw(), writer, Constants.PLOT_FACTIONS);

            writer.write(Constants.FRAGMENTS + " " + String.join(",", player.getFragments()));
            writer.write(System.lineSeparator());

            writer.write(Constants.RELICS + " " + String.join(",", player.getRelics()));
            writer.write(System.lineSeparator());

            writer.write(Constants.EXHAUSTED_RELICS + " " + String.join(",", player.getExhaustedRelics()));
            writer.write(System.lineSeparator());

            writer.write(Constants.MAHACT_CC + " " + String.join(",", player.getMahactCC()));
            writer.write(System.lineSeparator());

            writer.write(Constants.FACTION_TECH + " " + String.join(",", player.getFactionTechs()));
            writer.write(System.lineSeparator());
            writer.write(Constants.TECH + " " + String.join(",", player.getTechs()));
            writer.write(System.lineSeparator());
            writer.write(Constants.SPENT_THINGS + " " + String.join(",", player.getSpentThingsThisWindow()));
            writer.write(System.lineSeparator());
            writer.write(Constants.BOMBARD_UNITS + " " + String.join(",", player.getBombardUnits()));
            writer.write(System.lineSeparator());
            writer.write(Constants.TRANSACTION_ITEMS + " " + String.join(",", player.getTransactionItems()));
            writer.write(System.lineSeparator());
            writer.write(Constants.TEAMMATE_IDS + " " + String.join(",", player.getTeamMateIDs()));
            writer.write(System.lineSeparator());
            writer.write(Constants.TECH_EXHAUSTED + " " + String.join(",", player.getExhaustedTechs()));
            writer.write(System.lineSeparator());
            writer.write(Constants.TECH_PURGED + " " + String.join(",", player.getPurgedTechs()));
            writer.write(System.lineSeparator());

            writer.write(Constants.PLANETS + " " + String.join(",", player.getUniquePlanets()));
            writer.write(System.lineSeparator());
            writer.write(Constants.PLANETS_EXHAUSTED + " " + String.join(",", player.getExhaustedPlanets()));
            writer.write(System.lineSeparator());
            writer.write(Constants.PLANETS_ABILITY_EXHAUSTED + " "
                    + String.join(",", player.getExhaustedPlanetsAbilities()));
            writer.write(System.lineSeparator());

            writer.write(Constants.TACTICAL + " " + player.getTacticalCC());
            writer.write(System.lineSeparator());
            writer.write(Constants.FLEET + " " + player.getFleetCC());
            writer.write(System.lineSeparator());
            writer.write(Constants.STRATEGY + " " + player.getStrategicCC());
            writer.write(System.lineSeparator());

            writer.write(Constants.ABILITIES + " " + String.join(",", player.getAbilities()));
            writer.write(System.lineSeparator());
            writer.write(Constants.EXHAUSTED_ABILITIES + " " + String.join(",", player.getExhaustedAbilities()));
            writer.write(System.lineSeparator());

            writer.write(Constants.TG + " " + player.getTg());
            writer.write(System.lineSeparator());

            writer.write(Constants.EXPECTED_HITS_TIMES_10 + " " + player.getExpectedHitsTimes10());
            writer.write(System.lineSeparator());

            writer.write(Constants.TOTAL_EXPENSES + " " + player.getTotalExpenses());
            writer.write(System.lineSeparator());
            writeIntLine(writer, Constants.BONUS_SCORED_SECRETS, player.getBonusScoredSecrets());

            writer.write(Constants.COMMODITIES + " " + player.getCommodities());
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
