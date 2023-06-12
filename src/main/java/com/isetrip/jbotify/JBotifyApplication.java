package com.isetrip.jbotify;

import com.isetrip.jbotify.analytics.AnalyticsManager;
import com.isetrip.jbotify.annotations.*;
import com.isetrip.jbotify.commands.CommandBase;
import com.isetrip.jbotify.commands.CommandRegister;
import com.isetrip.jbotify.data.JBotifyConfiguration;
import com.isetrip.jbotify.events.EventsRegister;
import com.isetrip.jbotify.events.elements.LanguagesRegistryEvent;
import com.isetrip.jbotify.managers.ConfigurationManager;
import com.isetrip.jbotify.managers.LangManager;
import com.isetrip.jbotify.modules.ModulesLoader;
import com.isetrip.jbotify.utils.ClassScanner;
import com.isetrip.jbotify.utils.DefaultUtils;
import com.pengrad.telegrambot.TelegramBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

@Slf4j
public class JBotifyApplication {

    @Getter
    private static EventsRegister eventsRegister;
    @Getter
    private static JBotifyHandler handler;
    @Getter
    private static CommandRegister commandRegister;
    @Getter
    private static ConfigurationManager configurationManager;
    @Getter
    private static ModulesLoader modulesLoader;
    @Getter
    private static AnalyticsManager analyticsManager;
    @Getter
    private static TelegramBot bot;

    public static void run(Class mainClazz, String... args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        log.info("Initialising JBotify... ");

        log.info("Initialising ModulesLoader... ");
        modulesLoader = new ModulesLoader();
        modulesLoader.load();

        log.info("Loading bot data from jbotify.cfg.properties file");
        InputStream inputStream = JBotifyApplication.class.getResourceAsStream(String.format("/%s", "jbotify.cfg.properties"));
        Properties properties = new Properties();
        properties.load(inputStream);

        JBotifyConfiguration botData = JBotifyConfiguration.builder()
                .botToken(properties.getProperty("bot.token"))
                .analyticsAddress(properties.getProperty("analytics.address"))
                .analyticsPort(Integer.parseInt(properties.getProperty("analytics.port")))
                .build();

        log.info("Initialising Events Handlers... ");
        eventsRegister = new EventsRegister();
        List<Class<?>> classes = ClassScanner.findAnnotatedClasses(BotEventHandler.class, DefaultUtils.concat(new String[] { mainClazz.getPackage().getName() }, modulesLoader.getModulesPackages().toArray(new String[0])));
        for (Class<?> clazz : classes) {
            eventsRegister.register(clazz);
        }

        log.info("Initialising Languages... ");
        LangManager langManager = new LangManager();
        langManager.loadLanguageProperties("en", "en_UK");
        eventsRegister.publish(new LanguagesRegistryEvent(langManager));

        log.info("Initialising Commands... ");
        commandRegister = new CommandRegister();
        classes = ClassScanner.findAnnotatedClasses(RegisterCommand.class, DefaultUtils.concat(new String[] { mainClazz.getPackage().getName() }, modulesLoader.getModulesPackages().toArray(new String[0])));
        for (Class<?> clazz : classes) {
            if (CommandBase.class.isAssignableFrom(clazz)) {
                commandRegister.register(((Class<? extends CommandBase>) clazz).newInstance());
            }
        }

        log.info("Initialising Configs... ");
        configurationManager = new ConfigurationManager(ClassScanner.findAnnotatedClasses(Configuration.class, DefaultUtils.concat(new String[] { mainClazz.getPackage().getName() }, modulesLoader.getModulesPackages().toArray(new String[0]))));

        log.info("Initialising Analytics... ");
        analyticsManager = new AnalyticsManager(botData);

        log.info("Initialising TelegramBot... ");
        bot = new TelegramBot(botData.getBotToken());
        bot.setUpdatesListener(handler = new JBotifyHandler(), e -> {
            if (e.response() != null) {
                log.error(e.response().description() + ": " + e.response().errorCode());
            } else {
                e.printStackTrace();
            }
        });

        commandRegister.registerCommandsMenu();

        printLogo();

        log.info("JBotify is up and running and ready to go!");

        Thread printingHook = new Thread(() -> {

        });
        Runtime.getRuntime().addShutdownHook(printingHook);
    }

    public static void printLogo() {
        System.out.println("                       +##*.                                                                                                                          \n" +
                        "                      :####=                                                                                                                          \n" +
                        "                       .-=:                                                                                                                           \n" +
                        "                                                                                                                                                      \n" +
                        "                :-=+*#########*+=-.                                                                                                                   \n" +
                        "            :=*#####################*=.                                                                                                               \n" +
                        "         .=#############################=.                                                                                  ...                       \n" +
                        "       :+#################################+.                 .====.  :========-:.                              :+**=     =#####-                      \n" +
                        "     .+#####################################=                .####-  +############=                   :****    =####    ####*==.                      \n" +
                        "    :#########################################:              .####-  +####::::=####+        ...       -####     .:.    :####:                         \n" +
                        "   -#####%%@@%%%%################%%%%%%%#######:             .####-  +####     +###*    -+######*-  .########- -#### :#########-####:   =####.        \n" +
                        "  -####%@@@@@@@@@@%%%%#######%%%@@@@@@@@@%######:            .####-  +####----+###*.   *###*==*####. =+####==. -#### .=+####+== +###*  .####-         \n" +
                        " .####%@@@@%%@%%@@@@@@@%%%%@@@@@@@%%%%%%@@@%#####            .####-  +###########*-   =####    =###*  -####    -####   :####.    *###- +###+          \n" +
                        " +####@@@%#%@%#@%%%@@@@@@@@@@@@%%@#%@%%%@@@@%####=           .####-  +####    .+###*  *###+    -####  -####    -####   :####.    :####-####.          \n" +
                        " ####%@@@@@%%%@%%%%@@@@@@@@@@@%%%%#@@%@@@@@@%#####   =+++=   :####:  +####     -####: =###*    =###*  -####    -####   :####.     +#######:           \n" +
                        ":####@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@#####.  -####+-=####+   +####===++####*   *###*--+####:  :####=-: -####   :####.      *#####+            \n" +
                        ":###%@@@@@@@@@@@@@%#############%@@@@@@@@@@@@#####.   :+#######*-    +###########+-     -*#######=.    =#####= -####   :####.      :#####             \n" +
                        ":#####%@@@@@@@@@@#..............:@@@@@@@@@@%%####+       ..:..        .........            .::.          .::.   ....    ....        ####:             \n" +
                        " #######%%%%%@@@@%*+++++++++++++#@@%%%%%%%##+=:.                                                                                 =+*###=              \n" +
                        " =###################%%%%%%%##########*+-:                                                                                       *###*-               \n" +
                        "  :=*##########################*+=-:.                                                                                                                 \n" +
                        "      .:=+*###########**+=--:.                                                                                                                        \n" +
                        "              ...           ");
        System.out.println("-: JBotify :-: v. 2.2.9 :-");
    }

}
