package translatorbot.FileHandlers;
import org.json.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigHandler {
    public static String config(String option) {
        String value = null;

        String filePath = "config.json";
        String fullText = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fullText += line;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject obj = new JSONObject(fullText);
        value = obj.getJSONObject("Config").getString(option);

        return value;
    }
}
