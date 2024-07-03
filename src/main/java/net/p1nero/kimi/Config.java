package net.p1nero.kimi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Config {
    public static String API_LINK = "https://api.moonshot.cn/v1/chat/completions";
    public static String API_KEY = "your key";
    public static String MODEL = "moonshot-v1-8k";
    public static String TEMPERATURE = "0.3";
    public static int SIZE = 5;
    public static String SYSTEM = "你是一个超级无敌可爱的女孩子，请你注意说话的语气要可爱。当有多个问题时没有特殊说明请你以最后一个问题为准（注意分辨多轮对话！）。";
    public static String NAME = "Kimi酱";
    public static boolean BROADCAST = true;
    public static final String JSON = "MineKimi.json";
    static Locale defaultLocale = Locale.getDefault();
    static String language = defaultLocale.getLanguage();


    public static void onInitialize() {
        Kimi.LOGGER.info("Your system language is: " + language);
        loadConfig();
    }

    public static void loadConfig() {
        File configFolder = new File("config" + File.separator + Kimi.MOD_ID);
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        File configFile = new File(configFolder, JSON);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Kimi.LOGGER.info("Loading configuration file...");
                JsonObject config = new Gson().fromJson(reader, JsonObject.class);
                API_LINK = config.get("API_Link").getAsString();
                API_KEY = config.get("API_KEY").getAsString();
                MODEL = config.get("MODEL").getAsString();
                TEMPERATURE = config.get("TEMPERATURE").getAsString();
                SIZE = config.get("SIZE").getAsInt();
                SYSTEM = config.get("SYSTEM").getAsString();
                NAME = config.get("NAME").getAsString();
                BROADCAST = config.get("BROADCAST").getAsBoolean();
            } catch (IOException e) {
                Kimi.LOGGER.error("Failed to load configuration file!" + e);
            }
        } else {
            try {
                Kimi.LOGGER.info("Generating configuration file...");
                configFile.createNewFile();
                JsonObject config = new JsonObject();
                config.addProperty("API_Link", API_LINK);
                config.addProperty("API_KEY", API_KEY);
                config.addProperty("MODEL", MODEL);
                config.addProperty("TEMPERATURE", TEMPERATURE);
                config.addProperty("SIZE", SIZE);
                config.addProperty("SYSTEM", SYSTEM);
                config.addProperty("NAME", NAME);
                config.addProperty("BROADCAST", BROADCAST);
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
                }
            } catch (IOException e) {
                Kimi.LOGGER.info("Error generating configuration file!" + e);
            }
        }
    }
}