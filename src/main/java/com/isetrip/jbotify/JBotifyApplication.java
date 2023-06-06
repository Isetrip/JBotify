package com.isetrip.jbotify;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isetrip.jbotify.buttons.ButtonsRegistry;
import com.isetrip.jbotify.buttons.IKeyboardButton;
import com.isetrip.jbotify.commands.CommandBase;
import com.isetrip.jbotify.commands.CommandRegister;
import com.isetrip.jbotify.data.BotData;
import com.isetrip.jbotify.data.JBUser;
import com.isetrip.jbotify.database.HibernateSet;
import com.isetrip.jbotify.events.EventsRegister;
import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.lang.LangManager;
import com.isetrip.jbotify.lang.elements.English;
import com.isetrip.jbotify.managers.ConfigurationManager;
import com.isetrip.jbotify.root.annotations.*;
import com.isetrip.jbotify.utils.ClassScanner;
import com.isetrip.jbotify.logs.*;
import lombok.Getter;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class JBotifyApplication {

    @Getter
    private static EventsRegister eventsRegister;
    @Getter
    private static UpdatesHandler updatesHandler;
    @Getter
    private static CommandRegister commandRegister;
    @Getter
    private static LangManager langManager;
    @Getter
    private static ButtonsRegistry buttonsRegistry;
    @Getter
    private static HibernateSet<JBUser> jbUsersSet;
    @Getter
    private static ConfigurationManager configurationManager;

    public static void run(Class mainClazz, String... args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, TelegramApiException {
        Log.info("Initialising JBotify... ");

        Thread.setDefaultUncaughtExceptionHandler(((thread, throwable) -> {
            Log.error("Exception in tread " + thread.getName() + " :" + getStackTrace(throwable));
        }));

        Log.info("Loading bot data from botdat.properties file");
        InputStream inputStream = JBotifyApplication.class.getResourceAsStream(String.format("/%s", "botdat.properties"));
        Properties properties = new Properties();
        properties.load(inputStream);

        BotData botData = BotData.builder()
                .name(properties.getProperty("bot.name"))
                .token(properties.getProperty("bot.token"))
                .build();

        Log.info("Initialising TelegramBot... ");
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(updatesHandler = new UpdatesHandler(botData));

        Log.info("Initialising Events Handlers... ");
        eventsRegister = new EventsRegister();
        List<Class<?>> classes = ClassScanner.findAnnotatedClasses(mainClazz.getPackage().getName(), BotEventHandler.class);
        for (Class<?> clazz : classes) {
            eventsRegister.register(clazz);
        }

        Log.info("Initialising Commands... ");
        commandRegister = new CommandRegister();
        classes = ClassScanner.findAnnotatedClasses(mainClazz.getPackage().getName(), RegisterCommand.class);
        for (Class<?> clazz : classes) {
            if (CommandBase.class.isAssignableFrom(clazz)) {
                commandRegister.register(((Class<? extends CommandBase>) clazz).newInstance());
            }
        }

        Log.info("Initialising Languages... ");
        langManager = new LangManager();
        langManager.loadLanguageProperties(new English());
        classes = ClassScanner.findAnnotatedClasses(mainClazz.getPackage().getName(), RegisterLang.class);
        for (Class<?> clazz : classes) {
            if (Lang.class.isAssignableFrom(clazz)) {
                langManager.loadLanguageProperties((Lang) clazz.newInstance());
            }
        }

        Log.info("Initialising Buttons... ");
        buttonsRegistry = new ButtonsRegistry();
        classes = ClassScanner.findAnnotatedClasses(mainClazz.getPackage().getName(), RegisterButton.class);
        for (Class<?> clazz : classes) {
            if (IKeyboardButton.class.isAssignableFrom(clazz)) {
                buttonsRegistry.register(((Class<? extends IKeyboardButton>) clazz).newInstance());
            }
        }

        Log.info("Initialising Users Data... ");
        jbUsersSet = new HibernateSet<>(JBUser.class);

        Log.info("Initialising Configs... ");
        configurationManager = new ConfigurationManager(ClassScanner.findAnnotatedClasses(mainClazz.getPackage().getName(), Configuration.class));


        System.out.println("    ___  ________  ________  _________  ___  ________ ___    ___          \n" +
                "   |\\  \\|\\   __  \\|\\   __  \\|\\___   ___\\\\  \\|\\  _____\\\\  \\  /  /|___      \n" +
                "   \\ \\  \\ \\  \\|\\ /\\ \\  \\|\\  \\|___ \\  \\_\\ \\  \\ \\  \\__/\\ \\  \\/  / /\\__\\     \n" +
                " __ \\ \\  \\ \\   __  \\ \\  \\\\\\  \\   \\ \\  \\ \\ \\  \\ \\   __\\\\ \\    / /\\|__|     \n" +
                "|\\  \\\\_\\  \\ \\  \\|\\  \\ \\  \\\\\\  \\   \\ \\  \\ \\ \\  \\ \\  \\_| \\/  /  /     ___   \n" +
                "\\ \\________\\ \\_______\\ \\_______\\   \\ \\__\\ \\ \\__\\ \\__\\__/  / /      |\\  \\  \n" +
                " \\|________|\\|_______|\\|_______|    \\|__|  \\|__|\\|__|\\___/ /       \\ \\  \\ \n" +
                "                                                    \\|___|/        _\\/  /|\n" +
                "                                                                  |\\___/ /\n" +
                "                                                                  \\|___|/ ");
        System.out.println("-: JBotify :-: v.1.0.0 :-");

        Log.info("JBotify is up and running and ready to go!");

        Thread printingHook = new Thread(() -> {
            jbUsersSet.close();
        });
        Runtime.getRuntime().addShutdownHook(printingHook);
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
