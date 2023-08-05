# TranslateBot
A very simple Javacord bot for translating messages. Designed to be self-hostable. Uses theRealBush's Google Translate Library.

### Usage
React to a message with a flag emoji to translate it into that language.
Use `$translate [text]` to translate a string into English.

To remove a translation message, react to the translation with the "X" emoji.

### Hosting
This bot was made with Java 11 in mind, so you will need to use Java 11 or newer.
1. Download the JAR and config.json file.
2. Put them in the same directory next to each other.
3. Put your bot token, bot name, and prefix into config.json.
4. Run the bot with `java -jar TranslateBot-1.2-all.jar`

If you want to use DeepL, change "DeepLEnabled" to "true", and set your DeepLKey in config.json. If you want to use Google Translate when DeepL doesnt have the language to translate to, change "UseGoogleAsFallbackForDeepL" to "true". If you want to change the DeepL emoji, change "DeepLEmoji" to one you wish to use.
### To Do

Add uptime command.

Add more languages & flags.

Any suggestions are greatly appreciated! Please open an issue if you have ideas.
