package translatorbot.UtilityCommands;


import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import translatorbot.FileHandlers.ConfigHandler;

import java.awt.*;


public class DeepL {
    public static EmbedBuilder deepl(String textToTranslate, String targetLang) {
        String key = ConfigHandler.config("DeepLKey");

        Translator translator = new Translator(key);

        TextResult translatedText = null;
        try {
            translatedText = translator.translateText(textToTranslate, null, targetLang);
        } catch (DeepLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Translated Text - DeepL");
        e.setColor(Color.CYAN);
        e.setFooter(translatedText.getDetectedSourceLanguage() + " -> " + targetLang);
        e.setDescription(translatedText.getText());

        return e;
    }
}
