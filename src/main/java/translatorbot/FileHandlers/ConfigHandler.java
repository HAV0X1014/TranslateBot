package translatorbot.FileHandlers;
import org.json.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    public static String getString(String option) {
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
        //this insanity just reads the full contents of the file and puts it to the String "fullText"

        JSONObject obj = new JSONObject(fullText);
        value = obj.getJSONObject("Config").getString(option);

        return value;
    }
    
    public static String[] getArray(String option) {
        String[] values = null;

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
        //this insanity just reads the full contents of the file and puts it to the String "fullText"

        JSONObject obj = new JSONObject(fullText);
        JSONArray jsonArray = obj.getJSONObject("Config").getJSONArray(option);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        values = list.toArray(new String[list.size()]);

        return values;
    }

    public static boolean getBoolean(String option) {
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
        //this insanity just reads the full contents of the file and puts it to the String "fullText"

        JSONObject obj = new JSONObject(fullText);
        return obj.getJSONObject("Config").getBoolean(option);
    }
}
