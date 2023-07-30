package translatorbot.UtilityCommands;

import me.bush.translator.Language;
import me.bush.translator.Translation;
import me.bush.translator.Translator;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class Trnsl {
    public static EmbedBuilder trnsl(String textToTranslate, Language targetLang) {
        String translatedText = null;

        Translator translator = new Translator();

        Translation tr = translator.translateBlocking(textToTranslate, targetLang, Language.AUTO);
        translatedText = tr.getTranslatedText();

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Translated Text - Google Translate");
        e.setColor(new Color(46,125,50));
        e.setFooter(tr.getSourceLanguage() + " -> " + tr.getTargetLanguage());
        e.setDescription(translatedText);

        return e;
    }
}

