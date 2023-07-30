package translatorbot;

import me.bush.translator.Language;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import translatorbot.FileHandlers.ConfigHandler;
import translatorbot.UtilityCommands.Trnsl;

import java.util.NoSuchElementException;


public class TranslatorMain {
    static String token = ConfigHandler.config("Token");
    static String BotName = ConfigHandler.config("BotName");
    static String Prefix = ConfigHandler.config("Prefix");

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        System.out.println(BotName + " logged in.");
        String self = api.getYourself().getIdAsString();

        api.addReactionAddListener(ra -> {
            //general idea for how to select target language to translate to
            // read flag/target lang -> set as target lang -> get content of message -> send translation
            // the long part is the flags. gonna suck making all of these flags go to a lang
            // 7/29/2023
            String emoji = ra.getEmoji().asUnicodeEmoji().orElse("");

            Language targetLang = Language.ENGLISH;
            String messageContent = ra.getMessageContent().orElse("NoContent").toString();

            String deleteCandidate = ra.requestMessage().join().getAuthor().getIdAsString();

            switch(emoji) {
                case "❌":
                    if (deleteCandidate.equals(self)) {
                        String del = ra.requestMessage().join().getIdAsString();
                        api.getMessageById(del, ra.getChannel()).join().delete();
                    }
                    return;
                case "\uD83C\uDDFA\uD83C\uDDF8": //USA English
                    targetLang = Language.ENGLISH;
                    break;
                case "\uD83C\uDDEC\uD83C\uDDE7": //United Kingdom English
                    targetLang = Language.ENGLISH;
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF1": //Poland Polish
                    targetLang = Language.POLISH;
                    break;
                case "\uD83C\uDDE8\uD83C\uDDF3": //China Chinese Simplified
                    targetLang = Language.CHINESE_SIMPLIFIED;
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF3": //Hindi OR India idk
                    targetLang = Language.HINDI;
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFD": //Mexico Spanish
                    targetLang = Language.SPANISH;
                    break;
                case "\uD83C\uDDEA\uD83C\uDDF8": //Spain Spanish
                    targetLang = Language.SPANISH;
                    break;
                case "\uD83C\uDDEB\uD83C\uDDF7": //France French
                    targetLang = Language.FRENCH;
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA": //Russia Russian
                    targetLang = Language.RUSSIAN;
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF9": //Portugal Portuguese
                    targetLang = Language.PORTUGUESE;
                    break;
                case "\uD83C\uDDE7\uD83C\uDDF7": //Brazil Portuguese
                    targetLang = Language.PORTUGUESE;
                    break;
                case "\uD83C\uDDE9\uD83C\uDDEA": //Germany German
                    targetLang = Language.GERMAN;
                    break;
                case "\uD83C\uDDEF\uD83C\uDDF5": //Japan Japanese
                    targetLang = Language.JAPANESE;
                    break;
                case "\uD83C\uDDF5\uD83C\uDDED": //Philippines Filipino
                    targetLang = Language.FILIPINO;
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF7": //South Korea Korean
                    targetLang = Language.KOREAN;
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF5": //North Korea Korean
                    targetLang = Language.KOREAN;
                    break;
                case "\uD83C\uDDFB\uD83C\uDDF3": //Vietnam Vietnamese
                    targetLang = Language.VIETNAMESE;
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF9": //Italy Italian
                    targetLang = Language.ITALIAN;
                    break;
                default:
                    return;
            }
            ra.getChannel().sendMessage(Trnsl.trnsl(messageContent, targetLang));
        });

        api.addMessageCreateListener(mc -> {
            String m = mc.getMessageContent();

            if (m.startsWith(Prefix + "translate")) {
                String textToTranslate = m.replace(Prefix +"translate ", "");
                mc.getMessage().reply(Trnsl.trnsl(textToTranslate, Language.ENGLISH));
            }
        });
    }
}
