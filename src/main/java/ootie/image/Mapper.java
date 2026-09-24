package ootie.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import ootie.ResourceHelper;
import ootie.json.JsonMapperManager;
import ootie.logging.BotLogger;
import ootie.model.EventModel;
import ootie.model.ModelInterface;
import ootie.model.ProvinceModel;
import ootie.model.SourceModel;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public class Mapper {

    private static final Map<String, EventModel> events = new HashMap<>();
    private static final Map<String, SourceModel> sources = new HashMap<>();
    private static final Map<String, ProvinceModel> provinces = new HashMap<>();

    private static final JsonMapper jsonMapper =
            JsonMapperManager.basic().rebuild().build();

    public static void init() {
        try {
            loadData();
        } catch (Exception e) {
            BotLogger.error("Could not load data", e);
        }
    }

    static void loadData() throws Exception {
        importJsonObjectsFromFolder("events", events, EventModel.class);
        importJsonObjectsFromFolder("sources", sources, SourceModel.class);
        importJsonObjectsFromFolder("provinces", provinces, ProvinceModel.class);
    }

    public static boolean isValidEvent(String eventID) {
        return events.containsKey(eventID);
    }

    public static Map<String, EventModel> getEvents() {
        return new HashMap<>(events);
    }

    public static Map<String, ProvinceModel> getProvinces() {
        return new HashMap<>(provinces);
    }

    public static EventModel getEvent(String eventID) {
        return events.get(eventID);
    }

    private static <T extends ModelInterface> void importJsonObjects(
            String jsonFileName, Map<String, T> objectMap, Class<T> target) throws Exception {
        List<T> allObjects = new ArrayList<>();
        String filePath = ResourceHelper.getInstance().getDataFile(jsonFileName);
        JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, target);

        if (filePath != null) {
            try {
                InputStream input = new FileInputStream(filePath);
                allObjects = jsonMapper.readValue(input, type);
            } catch (Exception e) {
                BotLogger.error("Could not import JSON Objects from file: " + jsonFileName, e);
                throw e;
            }
        }

        List<String> badObjects = new ArrayList<>();
        for (T obj : allObjects) {
            if (objectMap.containsKey(obj.getAlias())) { // duplicate found
                BotLogger.warning("Duplicate **" + target.getSimpleName() + "** found: " + obj.getAlias());
            }
            objectMap.put(obj.getAlias(), obj);
            if (!obj.isValid()) {
                badObjects.add(obj.getAlias());
            }
        }
        if (!badObjects.isEmpty())
            BotLogger.warning("The following **" + target.getSimpleName() + "** are improperly formatted:\n> "
                    + String.join("\n> ", badObjects));
    }

    private static <T extends ModelInterface> void importJsonObjectsFromFolder(
            String jsonFolderName, Map<String, T> objectMap, Class<T> target) {
        String folderPath = ResourceHelper.getInstance().getDataFolder(jsonFolderName);
        // Added to prevent duplicates when running Mapper.init() over and over with ModelTest classes
        objectMap.clear();

        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (!file.isFile() || !file.getName().endsWith(".json")) {
                continue;
            }
            try {
                importJsonObjects(jsonFolderName + File.separator + file.getName(), objectMap, target);
            } catch (Exception e) {
                BotLogger.error("Could not import JSON Objects from file: " + jsonFolderName + "/" + file.getName(), e);
            }
        }
    }
}
