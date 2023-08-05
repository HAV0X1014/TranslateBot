package translatorbot.UtilityCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import translatorbot.FileHandlers.ConfigHandler;

import java.awt.*;

public class Help {
    public static EmbedBuilder help() {
        String deepLCommand = "";
        String prefix = ConfigHandler.getString("Prefix");
        if (ConfigHandler.getBoolean("DeepLEnabled")) {
            deepLCommand =  prefix + "deepl - Translates the following string into English with DeepL Translate.\n\n";
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle(ConfigHandler.getString("BotName") + " - Help");
        e.setColor(Color.GRAY);
        e.setDescription("#Flag Translation - \n\nTo translate a message, react to it with a flag emoji." +
                " The most spoken language of that country is the language it will be translated to. " +
                "For example, :flag_us: will be translated into English. A :flag_jp: will be translated into Japanese. " +
                "To use DeepL instead of Google Translate, react with " + ConfigHandler.getString("DeepLEmoji")
                + "and the flag you want. DeepL must be enabled by the bot owner for this to work.");

        e.addField("Commands",prefix +"translate - Translates the following string into English with Google Translate.\n\n"
                + deepLCommand
                + prefix + "help - Shows help for the bot."
                + prefix + "invite - Get an invite link for the bot.");

        e.setImage("https://cdn.discordapp.com/attachments/1100888255483875428/1135339151139545158/example.png");
        return e;
    }
}
