package ootie.image;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import ootie.ResourceHelper;
import ootie.json.JsonMapperManager;
import ootie.logging.BotLogger;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public class Mapper {

    private static final Properties decals = new Properties();
    private static final Properties general = new Properties();
    private static final Properties hyperlaneAdjacencies = new Properties();
    private static final Properties specialCase = new Properties();
    private static final Properties tokensFromProperties = new Properties();

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
        // must be first for validating later models

    }

    private static void readData(String propertyFileName, Properties properties) throws IOException {
        properties.clear();
        String propFile = ResourceHelper.getInstance().getDataFile(propertyFileName);
        if (propFile != null) {
            try (InputStream input = new FileInputStream(propFile)) {
                properties.load(input);
            } catch (IOException e) {
                BotLogger.error("Could not read .property file: " + propertyFileName, e);
                throw e;
            }
        }
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
