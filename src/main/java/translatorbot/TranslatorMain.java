package translatorbot;

import me.bush.translator.Language;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import translatorbot.FileHandlers.ConfigHandler;
import translatorbot.UtilityCommands.DeepL;
import translatorbot.UtilityCommands.Trnsl;

import java.awt.*;

public class TranslatorMain {
    static String token = ConfigHandler.config("Token");
    static String BotName = ConfigHandler.config("BotName");
    static String Prefix = ConfigHandler.config("Prefix");
    static boolean DeeplEnabled = Boolean.parseBoolean(ConfigHandler.config("DeepLEnabled"));
    static String DeepLEmoji = ConfigHandler.config("DeepLEmoji");
    static boolean UseGoogleAsFallbackForDeepL = Boolean.parseBoolean(ConfigHandler.config("UseGoogleAsFallbackForDeepL"));

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        System.out.println(BotName + " logged in.");
        String self = api.getYourself().getIdAsString();

        api.addReactionAddListener(ra -> {
            String emoji = ra.getEmoji().asUnicodeEmoji().orElse("");
            String allemoji = ra.requestMessage().join().getReactions().toString();

            String messageContent = ra.requestMessage().join().getContent();

            String deleteCandidate = ra.requestMessage().join().getAuthor().getIdAsString();

            Language targetLang;
            String dlLang = null;

            switch(emoji) {
                case "❌":
                    if (deleteCandidate.equals(self)) {
                        String del = ra.requestMessage().join().getIdAsString();
                        api.getMessageById(del, ra.getChannel()).join().delete();
                    }
                    return;
                case "\uD83C\uDDFA\uD83C\uDDF8": //USA English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-US";
                    break;
                case "\uD83C\uDDE8\uD83C\uDDE6": //Canada English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-US";
                    break;
                case "\uD83C\uDDE6\uD83C\uDDFA": //Australia English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-GB";
                    break;
                case "\uD83C\uDDEC\uD83C\uDDE7": //United Kingdom English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-GB";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF1": //Poland Polish
                    targetLang = Language.POLISH;
                    dlLang = "pl";
                    break;
                case "\uD83C\uDDE8\uD83C\uDDF3": //China Chinese Simplified
                    targetLang = Language.CHINESE_SIMPLIFIED;
                    dlLang = "zh";
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF3": //Hindi OR India idk
                    targetLang = Language.HINDI;
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFD": //Mexico Spanish
                    targetLang = Language.SPANISH;
                    dlLang = "es";
                    break;
                case "\uD83C\uDDEA\uD83C\uDDF8": //Spain Spanish
                    targetLang = Language.SPANISH;
                    dlLang = "es";
                    break;
                case "\uD83C\uDDEC\uD83C\uDDF9": //Guatemala Guatemala
                    targetLang = Language.SPANISH;
                    dlLang = "es";
                    break;
                case "\uD83C\uDDEB\uD83C\uDDF7": //France French
                    targetLang = Language.FRENCH;
                    dlLang = "fr";
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA": //Russia Russian
                    targetLang = Language.RUSSIAN;
                    dlLang = "ru";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF9": //Portugal Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-PT";
                    break;
                case "\uD83C\uDDE7\uD83C\uDDF7": //Brazil Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-BR";
                    break;
                case "\uD83C\uDDE9\uD83C\uDDEA": //Germany German
                    targetLang = Language.GERMAN;
                    dlLang = "de";
                    break;
                case "\uD83C\uDDEF\uD83C\uDDF5": //Japan Japanese
                    targetLang = Language.JAPANESE;
                    dlLang = "ja";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDED": //Philippines Filipino
                    targetLang = Language.FILIPINO;
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF7": //South Korea Korean
                    targetLang = Language.KOREAN;
                    dlLang = "ko";
                    break;
                case "\uD83C\uDDF0\uD83C\uDDF5": //North Korea Korean
                    targetLang = Language.KOREAN;
                    dlLang = "ko";
                    break;
                case "\uD83C\uDDFB\uD83C\uDDF3": //Vietnam Vietnamese
                    targetLang = Language.VIETNAMESE;
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF9": //Italy Italian
                    targetLang = Language.ITALIAN;
                    dlLang = "it";
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFE": //Malaysia Malay
                    targetLang = Language.MALAY;
                    break;
                default:
                    return;
            }
            if (allemoji.contains(DeepLEmoji)) {
                if (DeeplEnabled) { //Check for the DeepL emoji (the one that says to use DeepL if reacted)
                    if (dlLang == null) { //some languages aren't on DeepL. If the language isn't supported, it will be null because it wasn't set in the switch case
                        if (UseGoogleAsFallbackForDeepL) { //if DeepL isn't able to translate into the language, use Google as a fallback translator.
                            ra.getChannel().sendMessage(Trnsl.trnsl(messageContent, targetLang));
                        } else {
                            ra.getChannel().sendMessage("This language is not supported by DeepL.");
                        }
                    } else {
                        ra.getChannel().sendMessage(DeepL.deepl(messageContent, dlLang)); //Translate if successful
                    }
                } else {
                    ra.getChannel().sendMessage(Trnsl.trnsl(messageContent, targetLang)); //use Google if DeepL isnt enabled.
                }
            } else {
                ra.getChannel().sendMessage(Trnsl.trnsl(messageContent, targetLang)); //use Google if there isnt a DeepL emoji.
            }
            });

        api.addMessageCreateListener(mc -> {
            String m = mc.getMessageContent();

            if (m.toLowerCase().startsWith(Prefix + "translate")) {
                String textToTranslate = m.replace(Prefix + "translate ", "");
                mc.getMessage().reply(Trnsl.trnsl(textToTranslate, Language.ENGLISH));
            }

            if (DeeplEnabled) {
                if (m.toLowerCase().startsWith(Prefix + "deepl")) {
                    String textToTranslate = m.replace(Prefix + "deepl ", "");
                    Thread deeplThread = new Thread(() -> {
                        mc.getMessage().reply(DeepL.deepl(textToTranslate, "en-US"));
                    });
                    deeplThread.start();
                }
            }

            if (m.toLowerCase().startsWith(Prefix + "help")) {
                String deepLText = "";
                String deepLCommand = "";
                if (DeeplEnabled) {
                    deepLText = "To translate the message using DeepL instead of Google Translate, react with " + DeepLEmoji + " and the flag of the language.\n";
                    deepLCommand =  Prefix + "deepl - Translates the following string into English with DeepL Translate.\n\n";
                }

                EmbedBuilder e = new EmbedBuilder();
                e.setTitle(BotName + " Help");
                e.setColor(Color.GRAY);
                e.setDescription("Prefix - ``" + Prefix + "``\n\nTo translate a message, react to the message with the flag of the language you want.\n"
                        + deepLText + "By default, the bot uses Google Translate.");
                e.addField("Commands",Prefix +"translate - Translates the following string into English with Google Translate.\n\n"
                        + deepLCommand
                        + Prefix + "help - Shows help for the bot.");
                e.setImage("https://cdn.discordapp.com/attachments/1100888255483875428/1135339151139545158/example.png");

                mc.getMessage().reply(e);
            }
        });
    }
}
