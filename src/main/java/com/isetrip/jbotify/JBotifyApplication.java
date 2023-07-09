package com.isetrip.jbotify;

import com.isetrip.jbotify.analytics.AnalyticsManager;
import com.isetrip.jbotify.annotations.*;
import com.isetrip.jbotify.commands.CommandBase;
import com.isetrip.jbotify.commands.CommandRegister;
import com.isetrip.jbotify.data.JBotifyConfiguration;
import com.isetrip.jbotify.events.EventsRegister;
import com.isetrip.jbotify.events.elements.LanguagesRegistryEvent;
import com.isetrip.jbotify.managers.ConfigurationManager;
import com.isetrip.jbotify.managers.CustomUpdateActionManager;
import com.isetrip.jbotify.managers.LangManager;
import com.isetrip.jbotify.modules.ModulesLoader;
import com.isetrip.jbotify.utils.ClassScanner;
import com.isetrip.jbotify.utils.DefaultUtils;
import com.pengrad.telegrambot.TelegramBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

    public static void run(Class<?> mainClazz, String... args) {
        try {
            log.info("Initializing JBotify...");
            initializeModulesLoader();

            JBotifyConfiguration botifyConfiguration = loadBotData();

            initializeEventHandlers(mainClazz);

            initializeLanguages();

            initializeCommands(mainClazz);

            initializeObjects(mainClazz);

            initializeConfigs(mainClazz);

            initializeCustomUpdateActionManager();

            initializeAnalytics(botifyConfiguration);

            initializeTelegramBot(botifyConfiguration);

            commandRegister.registerCommandsMenu();

            printLogo();
            log.info("JBotify is up and running and ready to go!");

            addShutdownHook();
        } catch (Exception e) {
            log.error("Error occurred during JBotify initialization: " + e.getMessage());
        }
    }

    private static void initializeModulesLoader() {
        try {
            log.info("Initializing ModulesLoader...");
            modulesLoader = new ModulesLoader();
            modulesLoader.load();
        } catch (Exception e) {
            log.error("Error occurred during ModulesLoader initialization: " + e.getMessage());
        }
    }

    private static JBotifyConfiguration loadBotData() throws IOException {
        log.info("Loading bot data from jbotify.cfg.properties file");
        try (InputStream inputStream = JBotifyApplication.class.getResourceAsStream("/jbotify.cfg.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            return JBotifyConfiguration.builder()
                    .botToken(properties.getProperty("bot.token"))
                    .analyticsAddress(properties.getProperty("analytics.address"))
                    .analyticsPort(Integer.parseInt(properties.getProperty("analytics.port")))
                    .build();
        } catch (IOException e) {
            log.error("Error occurred while loading bot data: " + e.getMessage());
            throw e;
        }
    }

    private static void initializeEventHandlers(Class<?> mainClazz) {
        try {
            log.info("Initializing Event Handlers...");
            eventsRegister = new EventsRegister();
            List<Class<?>> classes = ClassScanner.findAnnotatedClasses(BotEventHandler.class, DefaultUtils.concat(new String[]{mainClazz.getPackage().getName()}, modulesLoader.getModulesPackages().toArray(new String[0])));

            for (Class<?> clazz : classes) {
                eventsRegister.register(clazz);
            }
        } catch (Exception e) {
            log.error("Error occurred during Event Handlers initialization: " + e.getMessage());
        }
    }

    private static void initializeLanguages() {
        try {
            log.info("Initializing Languages...");
            LangManager langManager = new LangManager();
            langManager.loadLanguageProperties("en");
            eventsRegister.publish(new LanguagesRegistryEvent(langManager));
        } catch (Exception e) {
            log.error("Error occurred during Languages initialization: " + e.getMessage());
        }
    }

    private static void initializeCommands(Class<?> mainClazz) {
        try {
            log.info("Initializing Commands...");
            commandRegister = new CommandRegister();
            List<Class<?>> classes = ClassScanner.findAnnotatedClasses(RegisterCommand.class, DefaultUtils.concat(new String[]{mainClazz.getPackage().getName()}, modulesLoader.getModulesPackages().toArray(new String[0])));

            for (Class<?> clazz : classes) {
                if (CommandBase.class.isAssignableFrom(clazz)) {
                    commandRegister.register(((Class<? extends CommandBase>) clazz).newInstance());
                }
            }
        } catch (Exception e) {
            log.error("Error occurred during Commands initialization: " + e.getMessage());
        }
    }

    private static void initializeObjects(Class<?> mainClazz) {
        try {
            log.info("Initializing Objects...");
            List<Class<?>> classes = ClassScanner.findAnnotatedClasses(BotObject.class, DefaultUtils.concat(new String[]{mainClazz.getPackage().getName()}, modulesLoader.getModulesPackages().toArray(new String[0])));

            for (Class<?> clazz : classes) {
                Field[] fields = clazz.getDeclaredFields();

                for (Field field : fields) {
                    if (field.isAnnotationPresent(Instance.class)) {
                        instantiateField(field, clazz);
                    } else if (field.isAnnotationPresent(Autowired.class)) {
                        checkAndInstantiateField(classes, field);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred during Objects initialization: " + e.getMessage());
        }
    }

    private static void instantiateField(Field field, Class<?> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        field.setAccessible(true);
        Constructor<?> constructor = clazz.getDeclaredConstructor();

        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        Object instance = constructor.newInstance();
        field.set(clazz, instance);
    }

    private static void checkAndInstantiateField(List<Class<?>> classList, Field field) {
        Class<?> fieldType = field.getType();

        for (Class<?> clazz : classList) {
            if (clazz.equals(fieldType)) {
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    field.setAccessible(true);
                    field.set(fieldType, instance);
                    return;
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    log.error("Error occurred while instantiating field: " + e.getMessage());
                    return;
                }
            }
        }

        log.debug("Class with type " + fieldType.getSimpleName() + " not found in the list.");
    }

    private static void initializeConfigs(Class<?> mainClazz) {
        try {
            log.info("Initializing Configs...");
            configurationManager = new ConfigurationManager(ClassScanner.findAnnotatedClasses(Configuration.class, DefaultUtils.concat(new String[]{mainClazz.getPackage().getName()}, modulesLoader.getModulesPackages().toArray(new String[0]))));
        } catch (Exception e) {
            log.error("Error occurred during Configs initialization: " + e.getMessage());
        }
    }

    private static void initializeCustomUpdateActionManager() {
        try {
            log.info("Initializing CustomUpdateActionManager...");
            new CustomUpdateActionManager();
        } catch (Exception e) {
            log.error("Error occurred during CustomUpdateActionManager initialization: " + e.getMessage());
        }
    }

    private static void initializeAnalytics(JBotifyConfiguration botData) {
        try {
            log.info("Initializing Analytics...");
            analyticsManager = new AnalyticsManager(botData);
        } catch (Exception e) {
            log.error("Error occurred during Analytics initialization: " + e.getMessage());
        }
    }

    private static void initializeTelegramBot(JBotifyConfiguration botData) {
        try {
            log.info("Initializing TelegramBot...");
            bot = new TelegramBot(botData.getBotToken());
            bot.setUpdatesListener(handler = new JBotifyHandler(), e -> {
                if (e.response() != null) {
                    log.error(e.response().description() + ": " + e.response().errorCode());
                } else {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            log.error("Error occurred during TelegramBot initialization: " + e.getMessage());
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down JBotify...");
            if (bot != null) {
                bot.removeGetUpdatesListener();
            }
            if (analyticsManager != null) {
                analyticsManager.shutdown();
            }
        }));
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
        System.out.println("-: JBotify :-: v. 2.2.16 :-");
    }

}
