package ootie.helpers;

import static java.util.Map.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Constants {
    // Staff Pings

    // Server IDs
    public static final String ASYNCOOTIE_HUB_SERVER_ID = "943410040369479690";

    public static final Map<String, String> EMOJI_FARM_SERVERS = Map.ofEntries(
            entry("1155639926675746886", "Emoji Farm 1"),
            entry("1156671516784730314", "Emoji Farm 2"),
            entry("1156686770436591637", "Emoji Farm 3"),
            entry("1158956227829706762", "Emoji Farm 4"),
            entry("1158956387376828507", "Emoji Farm 5"),
            entry("1158956545019760750", "Emoji Farm 6"),
            entry("1158956865875615836", "Emoji Farm 7"),
            entry("1158956969290383360", "Emoji Farm 8"),
            entry("1164297443379249302", "Emoji Farm 9"),
            entry("1164298025603190864", "Emoji Farm 10"),
            entry("1171620536833560676", "Emoji Farm 11"),
            entry("1180152020582289478", "Emoji Farm 12"),
            entry("1180160763353124864", "Emoji Farm 13"),
            entry("1197344983531913267", "Emoji Farm 14"),
            entry("1220415501608681512", "Emoji Farm 15"),
            entry("1220415609725124660", "Emoji Farm 16"),
            entry("1220415693837832212", "Emoji Farm 17"),
            entry("1243245147551170582", "Emoji Farm 18"),
            entry("1303437221361352715", "Emoji Farm 19"));

    public static final String dicecordId = "572698679618568193";

    // Game Modes

    public static final String SHOW_GAME = "show_game";

    public static final String SAVE_GAMES = "save_games";
    public static final String SAVE_GAME = "save_game";

    /* From \data\ */

    // no /search colors yet, but there is /help sample_colors

    public static final String REALM = "realm";
    public static final String COLOR = "color";
    public static final String DUCATS = "ducats";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";

    // no /search combat_modifiers yet
    public static final String SEARCH_DECKS = "decks";
    public static final String SEARCH_EXPLORES = "explores";
    public static final String SEARCH_FACTIONS = "factions";
    // no /search franken_errata yet
    // no /search generic_cards yet
    public static final String SEARCH_LEADERS = "leaders";
    public static final String SEARCH_CHARACTERS = "characters";
    public static final String CHARACTER = "character";
    public static final String EVENTS = "events";
    public static final String IDEAS = "ideas";
    public static final String SHOW = "show";

    public static final String SEARCH_SOURCES = "sources";
    // no /search strategy_card_sets yet
    public static final String SEARCH_STRATEGY_CARDS = "strategy_cards";
    public static final String SEARCH_TECHS = "techs";
    public static final String SEARCH_EVENTS = "events";
    public static final String SEARCH_TOKENS = "tokens";
    public static final String SEARCH_UNITS = "units";
    /* From \resources\ */
    public static final String SEARCH_EMOJIS = "emojis";
    public static final String SEARCH_TILES = "tiles";
    public static final String SEARCH_PLANETS = "planets";
    /* From others */
    public static final String SEARCH_COMMANDS = "commands";
    public static final String SEARCH_GAMES = "games";
    public static final String SEARCH_MY_GAMES = "my_games";
    public static final String STATISTICS_FACTION_GAMES = "faction_games";
    public static final String STATISTICS_POLITICS_POSITION = "politics_position";
    public static final String SEARCH_FOR_GAME = "for_game";

    public static final String RELOAD_GAME = "reload_game";
    public static final String GAME_NAME = "game_name";
    public static final String CREATE_GAME = "create";
    public static final String DELETE_GAME = "delete_game";
    public static final String POSITION = "position";
    public static final String TILE_NAME_2 = "tile_name_2";
    public static final String TILE_NAME_3 = "tile_name_3";
    public static final String TILE_NAME_4 = "tile_name_4";
    public static final String TILE_NAME_5 = "tile_name_5";
    public static final String TILE_NAME_TO = "tile_name_to";
    public static final String EXTRA_RINGS = "extra_rings";

    public static final String HELP = "help";
    public static final String HELP_DOCUMENTATION = "documentation";
    public static final String SETUP_TEMPLATES = "setup_templates";

    public static final String CREATION_DATE_TIME = "creation_date_time";
    public static final String CREATION_DATE = "creation_date";
    public static final String LAST_MODIFIED_DATE = "last_modified_date";

    public static final String LABEL = "label";
    public static final String MESSAGE = "message";
    public static final String LAST_TIME_GAMES_CHECKED = "last_time_games_checked";
    public static final String AUTO_PING = "auto_ping";

    // /custom command
    public static final String CUSTOM = "custom";

    public static final String GAME = "game";
    public static final String MAP = "map";
    public static final String INFO = "info";

    public static final String UNDO = "undo";
    public static final String SET_ORDER = "set_order";
    public static final String PASSED = "passed";
    public static final String UNPASS = "unpass";
    public static final String PASS = "pass";
    public static final String TURN_END = "turn_end";
    public static final String TURN_START = "turn_start";
    public static final String ACTIVE_PLAYER = "active_player";
    public static final String ACTIVE_SYSTEM = "active_system";
    public static final String LAST_ACTIVE_PLAYER_PING = "last_active_player_ping";
    public static final String LAST_ACTIVE_PLAYER_CHANGE = "last_active_player_change";

    public static final String TABLE_TALK_CHANNEL = "table_talk_channel";
    public static final String MAIN_GAME_CHANNEL = "main_game_channel";
    public static final String SAVED_CHANNEL = "saved_channel";
    public static final String SAVED_MESSAGE = "saved_message";
    public static final String BOT_MAP_CHANNEL = "bot_map_channel";
    public static final String CHANNEL1 = "channel1";
    public static final String CHANNEL2 = "channel2";
    public static final String CHANNEL3 = "channel3";
    public static final String CHANNEL4 = "channel4";
    public static final String CHANNEL5 = "channel5";
    public static final String CHANNEL6 = "channel6";
    public static final String CHANNEL7 = "channel7";
    public static final String CHANNEL8 = "channel8";
    public static final String ROLE1 = "role1";
    public static final String ROLE2 = "role2";
    public static final String ROLE3 = "role3";
    public static final String ROLE4 = "role4";
    public static final String ROLE5 = "role5";
    public static final String ROLE6 = "role6";
    public static final String ROLE7 = "role7";
    public static final String ROLE8 = "role8";
    public static final String PLAYER1 = "player1";
    public static final String PLAYER2 = "player2";
    public static final String PLAYER3 = "player3";
    public static final String PLAYER4 = "player4";
    public static final String PLAYER5 = "player5";
    public static final String PLAYER6 = "player6";
    public static final String PLAYER7 = "player7";
    public static final String PLAYER8 = "player8";
    public static final String PLAYER = "player";
    public static final String PLAYER_FACTION = "player_or_faction";
    public static final String FACTION_COLOR = "faction_or_color";
    public static final String EXPEDITION = "expedition";
    public static final String TARGET_FACTION_OR_COLOR = "target_faction_or_color";
    public static final String TARGET_PLAYER = "target_player";
    public static final String TECH_TYPE = "tech_type";
    public static final String STATS = "stats";
    public static final String DRAW_ACTIONCARD = "draw_actioncard";
    public static final String ADD_PROVINCE = "add_province";
    public static final String SEND_TG = "send_tg";
    public static final String SEND_COMMODITIES = "send_commodities";
    public static final String SETUP = "setup";
    public static final String SETUP_STARTING_FLEET = "setup_starting_fleet";
    public static final String WEIRD_GAME_SETUP = "weird_game_setup";
    public static final String GALACTIC_EVENTS_SETUP = "galactic_events_setup";
    public static final String CUSTOMIZATION = "customization";
    public static final String CHANGE_COLOR = "change_color";
    public static final String SAMPLE_COLORS = "sample_colors";
    public static final String SAMPLE_DECALS = "sample_decals";
    public static final String CORRECT_FACTION = "correct_faction";
    public static final String GAME_CHANNEL_SETUP = "game_channel_setup";
    public static final String PLANETS = "planets";

    public static final String TURN_COUNT = "turn_count";

    public static final String NUMBER_OF_TURNS = "number_of_turns";
    public static final String TOTAL_TURN_TIME = "total_turn_time";

    public static final String TXT = ".txt";
    public static final String JSON = ".json";
    public static final String JPG = ".jpg";
    public static final String PNG = ".png";
    public static final String ADMIN = "admin";
    public static final String NAALU_PN = "gift";
    public static final String NAALU = "naalu";

    public static final String DRAW = "draw";
    public static final String BOT = "bot";
    public static final String CHANGED_FOCUS = "changed_focus";
    public static final String DIPLO_POWER = "diplo_power";
    public static final String ADMIN_POWER = "admin_power";
    public static final String MILITARY_POWER = "military_power";
    public static final String STABILITY = "stability";
    public static final String STATE_RELIGION = "state_religion";
    public static final String CLEANUP = "cleanup";

    public static final String TURN_ORDER = "turn_order";
    public static final String AVERAGE_TURN_STATS = "average_turn_stats";

    public static final String COUNT = "count";
    public static final String RESULT = "result";

    public static final String ACTION_CARD_ID = "action_card_id";
    public static final String DRAW_AC = "draw";
    public static final String DRAW_SPECIFIC_AC = "draw_specific_ac";
    public static final String MAKE_AC_COPIES = "make_ac_copies";
    public static final String SHUFFLE_AC_DECK = "shuffle_deck";
    public static final String SHOW_AC = "show";
    public static final String DISCARD_AC = "discard";
    public static final String PURGE_AC = "purge";
    public static final String DISCARD_AC_RANDOM = "discard_random";
    public static final String PLAY_AC = "play";
    public static final String PICK_AC_FROM_DISCARD = "pick_from_discard";
    public static final String PICK_AC_FROM_PURGED = "pick_from_purged";
    public static final String SHUFFLE_AC_BACK_INTO_DECK = "shuffle_back_into_deck";
    public static final String SHUFFLE_AC_FROM_HAND = "shuffle_from_hand";
    public static final String REVEAL_AND_PUT_AC_INTO_DISCARD = "reveal_and_put_into_discard";
    public static final String SEND_AC = "send";
    public static final String SEND_AC_RANDOM = "send_random";
    public static final String SHOW_AC_DISCARD_LIST = "show_discard_list";
    public static final String SHOW_AC_PURGED_LIST = "show_purged_list";
    public static final String SHOW_AC_REMAINING_CARD_COUNT = "show_remaining_card_count";
    public static final String SHOW_GARBOZIA_AC = "show_garbozia";

    public static final String PROMISSORY_NOTE_ID = "promissory_note_id";
    public static final String PROMISSORY_NOTES = "promissory_notes";
    public static final String PROMISSORY_NOTES_OWNED = "promissory_notes_owned";
    public static final String PROMISSORY_NOTES_PLAY_AREA = "promissory_notes_play_area";
    public static final String SHOW_PN = "show";
    public static final String PLAY_PN = "play";
    public static final String SEND_PN = "send";
    public static final String PURGE_PN = "purge";
    public static final String PURGED_PN = "purged_pn";

    public static final String TECH_ADD = "add";
    public static final String TECH_REMOVE = "remove";
    public static final String TECH_PURGE = "purge";
    public static final String TECH_UNPURGE = "unpurge";
    public static final String TECH_EXHAUST = "exhaust";
    public static final String TECH_REFRESH = "ready";

    public static final String PLANET_ADD = "add";
    public static final String PLANET_REMOVE = "remove";
    public static final String PLANET_EXHAUST = "exhaust";
    public static final String PLANET_REFRESH = "ready";
    public static final String PLANET_REFRESH_ALL = "ready_all";
    public static final String PLANET_EXHAUST_ALL = "exhaust_all";
    public static final String PLANET_EXHAUST_ABILITY = "legendary_exhaust_ability";
    public static final String PLANET_REFRESH_ABILITY = "legendary_ready_ability";
    public static final String PLANET_INFO = "info";
    public static final String PLANET_MOVE_TOKEN_PLANET = "move_token_planet";

    public static final String EXPLORE = "explore";
    public static final String CULTURAL = "cultural";
    public static final String INDUSTRIAL = "industrial";
    public static final String HAZARDOUS = "hazardous";
    public static final String EXPLORE_CARD_ID = "explore_card_id";
    public static final String SHUFFLE_BACK_INTO_DECK = "shuffle_back_into_deck";
    public static final String DISCARD_FROM_DECK = "discard_from_deck";
    public static final String SHUFFLE_INTO_DECK_FROM_HAND = "shuffle_into_deck";
    public static final String DRAW_AND_DISCARD = "draw_and_discard";
    public static final String TRAIT = "trait";
    public static final String RESET = "reset";
    public static final String INSTANT = "instant";
    public static final String ATTACH = "attach";
    public static final String FRAGMENT = "fragment";
    public static final String DISCARDED_EXPLORES = "discarded_explores";
    public static final String SEND_FRAGMENT = "send_fragment";
    public static final String USE = "use";
    public static final String PURGE_FRAGMENTS = "purge_fragments";
    public static final String SHOW_PURGED_FRAGMENTS = "show_purged_fragments";
    public static final String FRAGMENTS = "fragments";
    public static final String LIST_FRAGMENTS = "list_fragments";
    public static final String RELIC = "relic";
    public static final String RELIC_ALL = "relic_all";
    public static final String RELIC_DECK = "relic_deck";
    public static final String RELICS = "relics";
    public static final String EXHAUSTED_RELICS = "exhausted_relics";
    public static final String ENIGMATIC_DEVICE = "enigmaticdevice";

    public static final String WARFARE = "warfare";
    public static final String PROPULSION = "propulsion";
    public static final String CYBERNETIC = "cybernetic";
    public static final String BIOTIC = "biotic";
    public static final String WEAPON = "weapon";
    public static final String UNIT_UPGRADE = "unitupgrade";

    public static final String PRIVATE_CHANNEL = "-private";

    public static final String LEADER = "leader";
    public static final String AGENT = "agent";
    public static final String COMMANDER = "commander";
    public static final String HERO = "hero";
    public static final String ENVOY = "envoy";
    private static final List<String> leaderList = new ArrayList<>();

    static {
        leaderList.add(AGENT);
        leaderList.add(COMMANDER);
        leaderList.add(HERO);
        leaderList.add(ENVOY);
    }

    // Twilight's Fall
    public static final String TF_ABILITY = "tf_ability";
    public static final String TF_UNIT_UPGRADE = "tf_unit_upgrade";
    public static final String TF_GENOME = "tf_genome";
    public static final String TF_PARADIGM = "tf_paradigm";
    public static final String TF_EDICT = "tf_edict";

    public static final String HS_TILE_POSITION = "hs_tile_position";

    public static final String ATTACHMENT_TITANSPN_PNG = "attachment_titanspn.png";
    public static final String ATTACHMENT_IIHQ_1 = "attachment_custodiavigilia_1.png";
    public static final String ATTACHMENT_IIHQ_2 = "attachment_custodiavigilia_2.png";
    public static final String TERRAFORM = "terraform";
    public static final String CUSTODIAN_TOKEN = "custodian";
    public static final String CONSULATE_TOKEN = "consulate";
    public static final String CUSTODIAN_TOKEN_PNG = "token_custodian.png";
    public static final String SWAP_SC = "swap_sc";
    public static final String KELERES_HERO_MENTAK = "keleres_hero_mentak";
    public static final String BOTHELPER = "bothelper";
    public static final String SERVERPROMOTE = "server_promote";
    public static final String CREATE_GAME_CHANNELS = "create_game_channels";
    public static final String CREATE_GAME_BUTTON = "create_game_button";
    public static final String CREATE_FOW_GAME_BUTTON = "create_fow_game_button";
    public static final String CREATE_FOW_GAME_CHANNELS = "create_fow_game_channels";
    public static final String IMPORT_TTPG = "import_ttpg";
    public static final String TTPG_FILE_NAME = "ttpg_file_name";
    public static final String GAME_FUN_NAME = "game_fun_name";
    public static final String CATEGORY = "category";
    public static final String SEND_JSON_TO_S3 = "send_games_to_s3";
    public static final String SERVER_LIMIT_STATS = "server_limit_stats";
    public static final String END = "end";
    public static final String GAME_HAS_ENDED = "game_has_ended";
    public static final String ARCHIVE_OLD_THREADS = "archive_old_threads";
    public static final String CREATE_TILE = "create_tile";
    public static final String CREATE_PLANET = "create_planet";
    public static final String RE_EXPORT_TILES = "re_export_tiles";
    public static final String THREAD_COUNT = "thread_count";
    public static final String LIST_OLD_CHANNELS = "list_old_channels";
    public static final String LIST_DEAD_GAMES = "list_dead_games";
    public static final String LIST_SLASH_COMMANDS_USED = "list_slash_commands_used";
    public static final String LIST_TITLES_GIVEN = "list_titles_given";
    public static final String LIST_OLD_THREADS = "list_old_threads";
    public static final String ACTIONS_CHANNEL_SUFFIX = "-actions";
    public static final String CARDS_INFO_THREAD_PREFIX = "Cards Info-";
    public static final String BAG_INFO_THREAD_PREFIX = "Draft Bag-";
    public static final String BOT_CHANNEL_SUFFIX = "-bot-map-updates";
    public static final String PING = "ping";
    public static final String PING_ACTIVE_PLAYER = "ping_active_player";
    public static final String WHISPER = "whisper";
    public static final String MSG = "msg";
    public static final String OVERRIDE_FOW = "override_fow";
    public static final String OVERRIDE_EXPLORE_OWNERSHIP_REQ = "override_explore_ownership_req";
    public static final String ANON = "anon";
    public static final String ANNOUNCE = "announce";
    public static final String ENDED_GAMES = "ended_games";
    public static final String TEXT_SIZE = "text_size";
    public static final String FIX_CHANNEL_PERMISSIONS = "fix_channel_permissions";
    public static final String CATEGORY_CHANNEL_COUNT = "category_channel_count";
    public static final String BEGIN_VIDEO_GEN = "begin_video_gen";
    public static final String INCLUDE_SUBCOMMANDS = "include_subcommands";
    public static final String INCLUDE_OPTIONS = "include_options";
    public static final String CARDS_INFO = "cards_info";
    public static final String FRANKEN = "franken";
    public static final String FRANKEN2 = "franken2";
    public static final String FRANKENDRAZ = "frankendraz";
    public static final String SHOW_GAME_AS_PLAYER = "show_game_as";
    public static final String CHECK_PRIVATE_COMMUNICATIONS = "check_private_communications";

    // DRAFTS
    public static final String BAG_DRAFT = "bag_draft";
    public static final String MILTY_DRAFT_MANAGER = "milty_draft_manager";
    public static final String MILTY_DRAFT_SETTINGS = "milty_draft_settings";
    public static final String DRAFT_MANAGER = "draft_manager";
    public static final String DRAFT_SYSTEM_SETTINGS = "draft_system_settings";
    public static final String FRANKEN_DRAFT_SETTINGS = "franken_draft_settings";
    public static final String BASE_GAME_MINI_MILTY_SETTINGS = "base_game_mini_milty_settings";
    public static final String DRAFT = "draft";

    // groups
    public static final String DRAFT_MANAGE = "manage";
    public static final String DRAFT_FACTION = "faction";
    public static final String DRAFT_PUBLIC_SNAKE = "public_snake";
    public static final String DRAFT_SEAT = "seat";
    public static final String DRAFT_SLICE = "slice";
    public static final String DRAFT_SPEAKER_ORDER = "speaker_order";
    public static final String DRAFT_MANTIS_TILE = "mantis_tile";
    public static final String DRAFT_ANDCAT_REF_PACKAGES = "andcat_ref_packages";

    // subcommands
    public static final String DRAFT_MANAGE_DEBUG = "debug";
    public static final String DRAFT_MANAGE_VALIDATE = "validate";
    public static final String DRAFT_MANAGE_START = "start";
    public static final String DRAFT_MANAGE_CAN_END = "can_end";
    public static final String DRAFT_MANAGE_END = "end";
    public static final String DRAFT_MANAGE_POST_DRAFT_WORK = "post_draft_work";
    public static final String DRAFT_MANAGE_CAN_SETUP_PLAYERS = "can_setup_players";
    public static final String DRAFT_MANAGE_SETUP_PLAYERS = "setup_players";
    public static final String DRAFT_MANAGE_ADD_DRAFTABLE = "add_draftable";
    public static final String DRAFT_MANAGE_REMOVE_DRAFTABLE = "remove_draftable";
    public static final String DRAFT_MANAGE_SET_ORCHESTRATOR = "set_orchestrator";
    public static final String DRAFT_MANAGE_ADD_PLAYER = "add_player";
    public static final String DRAFT_MANAGE_REMOVE_PLAYER = "remove_player";
    public static final String DRAFT_MANAGE_LIST_PLAYERS = "list_players";
    public static final String DRAFT_MANAGE_CLEAR_MISSING_PLAYERS = "clear_missing_players";
    public static final String DRAFT_MANAGE_ADD_ALL_GAME_PLAYERS = "add_all_game_players";
    public static final String DRAFT_MANAGE_SWAP_PLAYERS = "swap_players";
    public static final String DRAFT_MANAGE_REPLACE_PLAYER = "replace_player";
    public static final String DRAFT_MANAGE_MAKE_PICK = "make_pick";
    public static final String DRAFT_MANAGE_UNPICK = "unpick";
    public static final String DRAFT_MANAGE_CUSTOM_COMMAND = "custom_draftable_command";
    public static final String DRAFT_MANAGE_CUSTOM_ORCHESTRATOR_COMMAND = "custom_orchestrator_command";
    public static final String DRAFT_MANAGE_SETUP_DRAFT = "setup_draft";
    public static final String DRAFT_MANAGE_SETUP_NUCLEUS = "setup_nucleus";
    public static final String DRAFT_FACTION_ADD = "add";
    public static final String DRAFT_FACTION_REMOVE = "remove";
    public static final String DRAFT_FACTION_SET_KELERES_FLAVOR = "set_keleres_flavor";
    public static final String DRAFT_PUBLIC_SNAKE_SET_ORDER = "set_order";
    public static final String DRAFT_PUBLIC_SNAKE_SET_CURRENT_PLAYER = "set_current_player";
    public static final String DRAFT_PUBLIC_SNAKE_SET_DRAFT_DIRECTION = "set_draft_direction";
    public static final String DRAFT_PUBLIC_SNAKE_SEND_BUTTONS = "send_buttons";
    public static final String DRAFT_SEAT_SET_SEAT_COUNT = "set_seat_count";
    public static final String DRAFT_SEAT_SET_FOR_MAP = "set_for_map";
    public static final String DRAFT_SLICE_MODIFY_SLICE = "modify_slice";
    public static final String DRAFT_SLICE_ADD_SLICE = "add_slice";
    public static final String DRAFT_SLICE_REMOVE_SLICE = "remove_slice";
    public static final String DRAFT_SPEAKER_ORDER_SET_PICK_COUNT = "set_pick_count";
    public static final String DRAFT_MANTIS_TILE_START_BUILDING = "start_building";
    public static final String DRAFT_MANTIS_TILE_CONFIGURE = "configure";
    public static final String DRAFT_MANTIS_TILE_SET_TILES = "set_tiles";
    public static final String DRAFT_ANDCAT_ADD_PACKAGE = "add_package";
    public static final String DRAFT_ANDCAT_REMOVE_PACKAGE = "remove_package";
    public static final String DRAFT_ANDCAT_MODIFY_PACKAGE = "modify_package";
    public static final String DRAFT_ANDCAT_SET_PLAYER_CHOICES = "set_player_choices";
    public static final String DRAFT_ANDCAT_CLEAR_PLAYER_CHOICES = "clear_player_choices";

    // options
    public static final String FORCE_OPTION = "force";
    public static final String ADD_DRAFTABLE_OPTION = "add_draftable_type";
    public static final String SET_ORCHESTRATOR_OPTION = "set_orchestrator_type";
    public static final String SAVE_DATA_OPTION = "save_data";
    public static final String AC_TYPE = "ac_type";
    public static final String PROVINCE = "province";
    public static final String SIZE = "size";

    public static final String CARDS_INFO_THREAD_CHANNEL_ID = "cards_info_thread_channel_id";
    public static final String DRAFT_BAG_INFO_THREAD_CHANNEL_ID = "draft_bag_info_thread_channel_id";
    public static final String LATEST_COMMAND = "latest_command";
    public static final String PHASE_OF_GAME = "phase_of_game";
    public static final String ALL_SLASH_COMMANDS = "all_slash_commands";

    // Installation constants
    public static final String INSTALLATION = "installation";
    public static final String ADD_SWEEP_TOKEN = "add_sweep_token";
    public static final String REMOVE_SWEEP_TOKEN = "remove_sweep_token";

    public static final String STATS_USERNAME = "userName";
    public static final String STATS_FACTION = "faction";
    public static final String STATS_COLOR = "color";
    public static final String STATS_VP = "vp";
    public static final String STATS_CC = "cc";
    public static final String STATS_SPEAKER = "speaker";
    public static final String STATS_SC = "sc";
    public static final String STATS_PASSED = "passed";
    public static final String STATS_SO = "so";
    public static final String SC3_ASSIGN_SPEAKER_BUTTON_ID_PREFIX = "sc_3_assign_speaker_to_";

    public static final String VERBOSITY = "verbosity";
    public static final String VERBOSITY_VERBOSE = "verbose";
    private static final String VERBOSITY_AVERAGE = "average";
    private static final String VERBOSITY_MINIMAL = "minimal";
    public static final List<String> VERBOSITY_OPTIONS =
            List.of(VERBOSITY_VERBOSE, VERBOSITY_AVERAGE, VERBOSITY_MINIMAL);

    public static final List<String> CALL_OF_THE_HAUNTED_LEADERS = List.of(
            "revenantverydithagent",
            "revenantmyrrcommander",
            "revenantthroneshero",
            "revenantarcanumagent",
            "revenantoblivioncommander",
            "revenantkairnhero",
            "revenantxytherisagent",
            "revenantponthouscommander",
            "revenantkryxoshero");

    public static final String BETA_TEST_MODE = "beta_test_mode";
    public static final String CC_LIMIT = "cc_limit";
    public static final String AGE_OF_EXPLORATION_MODE = "age_of_exploration_mode";
    public static final String FACILITIES_MODE = "facilities_mode";
    public static final String MINOR_FACTIONS_MODE = "minor_factions_mode";
    public static final String HIDDEN_AGENDA_MODE = "hidden_agenda_mode";
    public static final String AGE_OF_COMMERCE_MODE = "age_of_commerce_mode";
    public static final String TOTAL_WAR_MODE = "total_war_mode";
    public static final String DANGEROUS_WILDS_MODE = "dangerous_wilds_mode";
    public static final String CIVILIZED_SOCIETY_MODE = "civilized_society_mode";
    public static final String AGE_OF_FIGHTERS_MODE = "age_of_fighters_mode";
    public static final String MERCENARIES_FOR_HIRE_MODE = "mercenaries_for_hire_mode";
    public static final String ADVENT_OF_THE_WARSUN_MODE = "advent_of_the_warsun_mode";
    public static final String ZEALOUS_ORTHODOXY_MODE = "zealous_orthodoxy_mode";
    public static final String CULTURAL_EXCHANGE_PROGRAM_MODE = "cultural_exchange_program_mode";
    public static final String CONVENTIONS_OF_WAR_ABANDONED_MODE = "conventions_of_war_abandoned_mode";
    public static final String RAPID_MOBILIZATION_MODE = "rapid_mobilization_mode";
    public static final String MONUMENTS_TO_THE_AGES_MODE = "monuments_to_the_ages_mode";
    public static final String MONUMENTS_MODE = "monuments_mode";
    public static final String COSMIC_PHENOMENAE_MODE = "cosmic_phenomenae_mode";
    public static final String COSMIC_CONVERGENCE_MODE = "cosmic_convergence_mode";
    public static final String MUAAT_MANIA_MODE = "muaat_mania_mode";
    public static final String WEIRD_WORMHOLES_MODE = "weird_wormholes_mode";
    public static final String NO_FRACTURE = "no_fracture";
    public static final String CALL_OF_THE_VOID_MODE = "call_of_the_void_mode";
    public static final String WILD_WILD_GALAXY_MODE = "wild_wild_galaxy_mode";
    public static final String FEAST_OR_FAMINE_MODE = "feast_or_famine_mode";
    public static final String STELLAR_ATOMICS_MODE = "stellar_atomics_mode";
    public static final String NO_SWAP_MODE = "no_swap_mode";
    public static final String VEILED_HEART_MODE = "veiled_heart_mode";
    public static final String LORE_MODE = "lore_mode";
    public static final String LIMITED_WHISPERS_MODE = "limited_whispers_mode";
    public static final String WHISPERS_ENABLED = "whispers_enabled";
    public static final String WHISPERS_DISABLED = "whispers_disabled";
    public static final String ORDINIAN_C1_MODE = "ordinian_c1_mode";
    public static final String LIBERATION_C4_MODE = "liberation_c4_mode";
    public static final String ERWANS_GAMBIT_MODE = "erwans_gambit_mode";
    public static final String FAKE_COMMANDERS = "fake_commanders";
    public static final String UPDATE_THREAD_ARCHIVE_TIME = "update_thread_archive_time";
    public static final String THREAD_SEARCH_STRING = "thread_search_string";
    public static final String AUTO_ARCHIVE_DURATION = "auto_archive_duration";
    public static final String ALL_INFO = "all_info";

    public static final String BUTTON = "button";
    public static final String BUTTON_TEXT = "button_text";
    public static final String GENERIC_BUTTON_ID_PREFIX = "generic_button_id_";
    public static final String INCLUDE_ALIASES = "include_aliases";
    public static final String SEARCH = "search";
    public static final String FIND = "find";
    public static final String SEARCH_TYPE = "type";
    public static final String BENTOR_HAS_FOUND_CFRAG = "has_found_cfrag";
    public static final String BENTOR_HAS_FOUND_HFRAG = "has_found_hfrag";
    public static final String BENTOR_HAS_FOUND_IFRAG = "has_found_ifrag";
    public static final String BENTOR_HAS_FOUND_UFRAG = "has_found_ufrag";

    public static final String IMAGE_GEN_COUNT = "image_gen_count";
    public static final String RUN_DATA_MIGRATIONS = "run_data_migrations";
    public static final String ENDED_DATE = "ended_date";
    public static final String REPLACEMENT_MADE = "replacement_made";

    public static final String ADD_REMOVE = "add_remove";

    public static final String COMBAT = "combat";

    public static final String MIGRATION_NAME = "migration_name";
    public static final String RUN_MANUAL_DATA_MIGRATION = "run_manual_data_migration";
    public static final String CRON_NAME = "cron_name";
    public static final String RUN_CRON = "run_cron";
    public static final String UPLOAD_RECENT_GAME_STATS = "upload_recent_game_stats";

    public static final String HELIOS_ATTACHMENT_1 = "attachment_helios1.png";
    public static final String HELIOS_ATTACHMENT_2 = "attachment_helios2.png";
    public static final String PLOT_CARDS = "plot_cards";
    public static final String PLOT_FACTIONS = "plot_factions";

    public static final String CHECK_DISTANCE = "check_distance";
    public static final String CHECK_ALL_DISTANCE = "check_all_distance";
    public static final String MAX_DISTANCE = "max_distance";
    public static final String SHOW_DISTANCES = "show_distances";
    public static final String FACTION_TECH = "faction_tech";
    public static final String FACTION_TECH_ADD = "faction_tech_add";
    public static final String FACTION_TECH_REMOVE = "faction_tech_remove";
    public static final Object MOD_TEMP_ONE_ROUND = "ONE_ROUND";
    public static final String MOD_TEMP_ONE_TACTICAL_ACTION = "ONE_TACTICAL_ACTION";
    public static final String MOD_TEMP_ONE_COMBAT = "ONE_COMBAT";
    public static final String PLAYER_TEMP_MODS = "PLAYER_TEMP_MODS";
    public static final String PLAYER_NEW_TEMP_MODS = "PLAYER_NEW_TEMP_MODS";
    public static final String ARCHIVE_CHANNELS = "archive_channels";
    public static final String GET_CHANNEL_HTML = "get_channel_html";
    public static final String CHANNEL = "channel";
    public static final String MILTYMOD_MODE = "miltymod_mode";
    public static final String EVENT_PLAY = "play";
    public static final String INCLUDE_RAW_STRING = "include_raw_string";
    public static final String RESTORE_GAME = "restore_game";
    public static final String SAVE_FILE = "save_file";
    public static final String GAMES = "games";
    public static final String GAMES_WITH_FACTIONS = "games_with_factions";
    public static final String GAME_STATISTIC = "game_statistic";
    public static final String PLAYER_STATISTIC = "player_statistic";
    public static final String COMMUNITY = "community";
    public static final String UNDO_TO_COMMAND = "undo_to_command";
    public static final String SHOW_GAME_MODES = "show_game_modes";
    public static final String MAX_SO_COUNT = "max_so_count";
    public static final String REFRESH_RELIC_INFO = "refreshRelicInfo";
    public static final String REFRESH_LEADER_INFO = "refreshLeaderInfo";
    public static final String REFRESH_UNIT_INFO = "refreshUnitInfo";
    public static final String REFRESH_ALL_UNIT_INFO = "refreshUnitInfoAll";
    public static final String REFRESH_TECH_INFO = "refreshTechInfo";
    public static final String REFRESH_BREAKTHROUGH_INFO = "refreshBreakthroughInfo";
    public static final String REFRESH_PLANET_INFO = "refreshPlanetInfo";
    public static final String SERVER_GAME_STATS = "server_game_stats";
    public static final String START_COMBAT = "start_combat";
    public static final String COMBAT_TYPE = "combat_type";
    public static final String SHOW_FULL_COMPONENT_TEXT = "show_full_component_text";
    public static final String ELIMINATED = "eliminated";
    public static final String PLANET_FLAVOUR_TEXT = "planet_flavour_text";
    public static final String SOURCE = "source";
    public static final String INJECT_RULES_LINKS = "inject_rules_links";
    public static final String FACTION_DISPLAY_NAME = "faction_display_name";
    public static final String USER = "user";
    public static final String SHOW_MAP_SETUP = "show_map_setup";
    public static final String SHOW_HAND = "show_hand";
    public static final String BAG_DRAFT_STATUS_MESSAGE_ID = "bag_draft_status_message_id";
    public static final String SHOW_ALL_UNITS = "show_all_units";
    public static final String GAME_LAUNCH_THREAD_ID = "game_launch_thread_id";
    public static final String GAME_TAGS = "game_tags";
    public static final String SHOW_UNPLAYED_AC = "show_unplayed_ac";
    public static final String TIGL_RANK = "tigl_rank";
    public static final String TIGL = "tigl";
    public static final String TIGL_CHANGE_NICKNAME = "change_nickname";
    public static final String TIGL_NICKNAME = "nickname";
    public static final String TIGL_FRACTURED_TAG = "TIGL Fractured";
    public static final String LAZAX = "lazax";
    public static final String LAZAX_MY_POINTS = "my_points";
    public static final String LAZAX_TOP_100 = "top_100";
    public static final String LAZAX_DELEGATION_LEADERBOARD = "delegation_leaderboard";
    public static final String PUBLIC = "public";
    public static final String IS_FRACTURED = "is_fractured";
    public static final String SHOW_GAME_IDS = "show_game_ids";
    public static final String SEARCH_NAMES = "search_names";
    public static final String SEARCH_TAGS = "search_tags";
    public static final String SEARCH_USERS = "search_users";
    public static final String SEARCH_RULES = "rules";
    public static final String EXTRA_SECRET_MODE = "extra_secret_mode";
    public static final String VOTC_MODE = "votc_mode";
    public static final String OPTIONS = "options";
    public static final String SHOW_FULL_TEXT = "show_full_text";
    public static final String STARTED_DATE = "started_date";
    public static final String MODIFY_UNITS = "modify_units";
    public static final String SHOW_OWNED_PNS_IN_PLAYER_AREA = "show_owned_pns_in_player_area";
    public static final String SECONDS = "seconds";
    public static final String MOVE_TILE = "move_tile";
    public static final String TRANSACTION = "transaction";
    public static final String TRANSACTION_START = "transaction_start";
    public static final String ALL = "ALL";
    public static final String COEXIST = "coexist";
}
