package translatorbot;

import me.bush.translator.Language;
import me.bush.translator.Translator;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.interaction.*;
import translatorbot.FileHandlers.ConfigHandler;
import translatorbot.UtilityCommands.DeepL;
import translatorbot.UtilityCommands.Help;
import translatorbot.UtilityCommands.Trnsl;

import java.util.Arrays;

public class TranslatorMain {
    static String token = ConfigHandler.getString("Token");
    static String botName = ConfigHandler.getString("BotName");
    static String prefix = ConfigHandler.getString("Prefix");
    static boolean deeplEnabled = ConfigHandler.getBoolean("DeepLEnabled");
    static String deepLEmoji = ConfigHandler.getString("DeepLEmoji");
    static boolean useGoogleAsFallbackForDeepL = ConfigHandler.getBoolean("UseGoogleAsFallbackForDeepL");
    static String key = ConfigHandler.getString("DeepLKey");
    static String[] ignoredChannels = ConfigHandler.getArray("IgnoredChannels");
    static String statusText = ConfigHandler.getString("StatusText");
    static boolean registerSlashCommands = ConfigHandler.getBoolean("RegisterSlashCommands");
    static boolean registerApps = ConfigHandler.getBoolean("RegisterApps");

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        System.out.println(botName + " logged in.");
        String self = api.getYourself().getIdAsString();
        api.updateActivity(ActivityType.PLAYING, statusText);
        Translator translator = new Translator(); //google translate object
        if (key == null) key = "0";
        com.deepl.api.Translator deepLTranslator = new com.deepl.api.Translator(key); //deepL translator object

        if (registerSlashCommands) {
            System.out.println("Registering slash commands. This may take a while...");
            SlashCommand.with("help", "Shows help for the bot.").createGlobal(api).join();
            SlashCommand.with("invite","Makes an invite for the bot with all needed permissions set.").createGlobal(api).join();
            SlashCommand.with("translate","Translate text with Google Translate into the desired language.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "source", "The text you want to translate.", true), SlashCommandOption.create(SlashCommandOptionType.STRING, "target", "The language you want the text in. (Currently not implemented)", false))).createGlobal(api).join();
            SlashCommand.with("deepl","Translate text with DeepL Translator into the desired language.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "source", "The text you want to translate.", true), SlashCommandOption.create(SlashCommandOptionType.STRING, "target", "The language you want the text in. (Currently not implemented)", false))).createGlobal(api).join();
            System.out.println("**Slash commands registered!** Set \"RegisterSlashCommands\" to false in config.json!");
        }

        if (registerApps) {
            System.out.println("Registering Apps. (The things that show up when you right click a message) This may take a while...");
            MessageContextMenu.with("Translate - Google Translate").createGlobal(api).join();
            MessageContextMenu.with("Translate - DeepL").createGlobal(api).join();
            System.out.println("**Apps Registered!** Set \"RegisterApps\" to false in config.json!");
        }

        api.addMessageContextMenuCommandListener(event -> {
            MessageContextMenuInteraction interaction = event.getMessageContextMenuInteraction();
            String command = interaction.getCommandName();

            switch(command) {
                case "Translate - DeepL":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        if (deeplEnabled) {
                            interactionOriginalResponseUpdater.setContent("").addEmbed(DeepL.deepl(deepLTranslator, interaction.getTarget().getContent(), "en-US")).update();
                        } else {
                            interactionOriginalResponseUpdater.setContent("DeepL translation is disabled.").update();
                        }
                    });
                    break;
                case "Translate - Google Translate":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(Trnsl.trnsl(translator,interaction.getTarget().getContent(), Language.ENGLISH)).update();
                    });
                    break;
            }
        });

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            String command = interaction.getCommandName();
            switch(command) {
                case "help":
                    interaction.createImmediateResponder().setContent("").addEmbed(Help.help()).respond();
                    break;
                case "invite":
                    interaction.createImmediateResponder().setContent(api.createBotInvite(new PermissionsBuilder().setAllowed(PermissionType.ADMINISTRATOR).build())).respond();
                    break;
                case "deepl":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        if (deeplEnabled) {
                            interactionOriginalResponseUpdater.setContent("").addEmbed(DeepL.deepl(deepLTranslator, interaction.getArgumentStringValueByName("source").get(), "en-US")).update();
                        } else {
                            interactionOriginalResponseUpdater.setContent("DeepL translation is disabled.").update();
                        }
                    });
                    break;
                case "translate":
                    interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("").addEmbed(Trnsl.trnsl(translator,interaction.getArgumentStringValueByName("source").get(), Language.ENGLISH)).update();
                    });
                    break;
            }
        });

        api.addReactionAddListener(ra -> {
            String emoji = ra.getEmoji().asUnicodeEmoji().orElse("");
            String allemoji = ra.requestMessage().join().getReactions().toString();

            String messageContent = ra.requestMessage().join().getContent();

            String deleteCandidate = ra.requestMessage().join().getAuthor().getIdAsString();

            Language targetLang;
            String dlLang = null;

            switch(emoji) {
                case "❌":
                    if (deleteCandidate.equals(self)) { //self delete when X emoji is seen
                        if (ra.requestMessage().join().getEmbeds().get(0).getTitle().toString().startsWith("Translated Text")) { //only delete if it is a translation message
                            String del = ra.requestMessage().join().getIdAsString();
                            api.getMessageById(del, ra.getChannel()).join().delete();
                        }
                    }
                    return;

                case "\uD83C\uDDFA\uD83C\uDDF8": //USA English
                case "\uD83C\uDDE8\uD83C\uDDE6": //Canada English
                case "\uD83C\uDDFB\uD83C\uDDEE": //US Virgin Islands English
                case "\uD83C\uDDEC\uD83C\uDDFA": //Guam English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-US";
                    break;
                case "\uD83C\uDDE6\uD83C\uDDFA": //Australia English
                case "\uD83C\uDDEC\uD83C\uDDE7": //United Kingdom English
                case "\uD83C\uDDF3\uD83C\uDDFF": //New Zealand English
                case "\uD83C\uDDFB\uD83C\uDDEC": //British Virgin Islands English
                case "\uD83C\uDDEC\uD83C\uDDEE": //Gibraltar English
                case "\uD83C\uDDEE\uD83C\uDDF2": //Isle of Man English
                    targetLang = Language.ENGLISH;
                    dlLang = "en-GB";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF1": //Poland Polish
                    targetLang = Language.POLISH;
                    dlLang = "pl";
                    break;
                case "\uD83C\uDDE8\uD83C\uDDF3": //China Chinese Simplified
                case "\uD83C\uDDF8\uD83C\uDDEC": //Singapore Chinese Simplified
                    targetLang = Language.CHINESE_SIMPLIFIED;
                    dlLang = "zh";
                    break;
                case "\uD83C\uDDED\uD83C\uDDF0": //Hong Kong Chinese Traditional
                case "\uD83C\uDDF9\uD83C\uDDFC": //Taiwan Chinese Traditional
                    targetLang = Language.CHINESE_TRADITIONAL;
                    dlLang = "zh";
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF3": //Hindi OR India idk
                case "\uD83C\uDDF5\uD83C\uDDF0": //Pakistan Hindi
                    targetLang = Language.HINDI;
                    break;
                case "\uD83C\uDDF2\uD83C\uDDFD": //Mexico Spanish
                case "\uD83C\uDDE8\uD83C\uDDF4": //Colombia Spanish
                case "\uD83C\uDDEA\uD83C\uDDF8": //Spain Spanish
                case "\uD83C\uDDE6\uD83C\uDDF7": //Argentina Spanish
                case "\uD83C\uDDF5\uD83C\uDDEA": //Peru Spanish
                case "\uD83C\uDDFB\uD83C\uDDEA": //Venezuela Spanish
                case "\uD83C\uDDE8\uD83C\uDDF1": //Chile Spanish
                case "\uD83C\uDDEC\uD83C\uDDF9": //Guatemala Guatemala
                case "\uD83C\uDDF5\uD83C\uDDF7": //Puerto Rico Spanish
                    targetLang = Language.SPANISH;
                    dlLang = "es";
                    break;
                case "\uD83C\uDDEB\uD83C\uDDF7": //France French
                case "\uD83C\uDDF2\uD83C\uDDEC": //Madagascar French
                case "\uD83C\uDDE8\uD83C\uDDF2": //Cameroon French
                case "\uD83C\uDDE8\uD83C\uDDEE": //Cote d' Ivoire French
                case "\uD83C\uDDF3\uD83C\uDDEA": //Niger French
                case "\uD83C\uDDE7\uD83C\uDDEF": //Benin French
                    targetLang = Language.FRENCH;
                    dlLang = "fr";
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA": //Russia Russian
                case "\uD83C\uDDE7\uD83C\uDDFE": //Belarus Russian
                    targetLang = Language.RUSSIAN;
                    dlLang = "ru";
                    break;
                case "\uD83C\uDDF5\uD83C\uDDF9": //Portugal Portuguese
                case "\uD83C\uDDF2\uD83C\uDDFF": //Mozambique Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-PT";
                    break;
                case "\uD83C\uDDE7\uD83C\uDDF7": //Brazil Portuguese
                    targetLang = Language.PORTUGUESE;
                    dlLang = "pt-BR";
                    break;
                case "\uD83C\uDDE9\uD83C\uDDEA": //Germany German
                case "\uD83C\uDDF1\uD83C\uDDEE": //Liechtenstein German
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
                case "\uD83C\uDDE7\uD83C\uDDF3": //Brunei Malay
                    targetLang = Language.MALAY;
                    break;
                default:
                    return;
            }
            if (!Arrays.toString(ignoredChannels).contains(ra.requestMessage().join().getChannel().getIdAsString())) { //if IgnoredChannels does NOT include the reaction channel, continue
                if (allemoji.contains(deepLEmoji)) {
                    if (deeplEnabled) { //Check for the DeepL emoji (the one that says to use DeepL if reacted)
                        if (dlLang == null) { //some languages aren't on DeepL. If the language isn't supported, it will be null because it wasn't set in the switch case
                            if (useGoogleAsFallbackForDeepL) { //if DeepL isn't able to translate into the language, use Google as a fallback translator.
                                ra.getChannel().sendMessage(Trnsl.trnsl(translator, messageContent, targetLang));
                            } else {
                                ra.getChannel().sendMessage("This language is not supported by DeepL.");
                            }
                        } else {
                            ra.getChannel().sendMessage(DeepL.deepl(deepLTranslator, messageContent, dlLang)); //Translate if successful
                        }
                    } else {
                        ra.getChannel().sendMessage(Trnsl.trnsl(translator, messageContent, targetLang)); //use Google if DeepL isnt enabled.
                    }
                } else {
                    ra.getChannel().sendMessage(Trnsl.trnsl(translator, messageContent, targetLang)); //use Google if there isnt a DeepL emoji.
                }
            }
        });

        api.addMessageCreateListener(mc -> {
            String m = mc.getMessageContent();

            if (m.toLowerCase().startsWith(prefix + "translate")) {
                String textToTranslate = m.replace(prefix + "translate ", "");
                mc.getMessage().reply(Trnsl.trnsl(translator, textToTranslate, Language.ENGLISH));
            }

            if (deeplEnabled) {
                if (m.toLowerCase().startsWith(prefix + "deepl")) {
                    String textToTranslate = m.replace(prefix + "deepl ", "");
                    Thread deeplThread = new Thread(() -> mc.getMessage().reply(DeepL.deepl(deepLTranslator, textToTranslate, "en-US")));
                    deeplThread.start();
                }
            }

            if (m.toLowerCase().startsWith(prefix + "help")) {
                mc.getMessage().reply(Help.help());
            }
            if (m.toLowerCase().startsWith(prefix + "invite")) {
                mc.getMessage().reply(api.createBotInvite(new PermissionsBuilder().setAllowed(PermissionType.ADMINISTRATOR).build()));
            }
        });
    }
}
