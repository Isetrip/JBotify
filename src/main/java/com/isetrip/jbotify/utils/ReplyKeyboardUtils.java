package com.isetrip.jbotify.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class ReplyKeyboardUtils {

    public static ReplyKeyboardMarkup getWithButtons(String... strings) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();

        int stringsLength = strings.length;

        if (stringsLength < 3) {
            for (String str : strings)
                keyboardButtons.add(str);

            keyboard.add(keyboardButtons);
        } else if (stringsLength == 4) {
            keyboardButtons.add(strings[0]);
            keyboardButtons.add(strings[1]);
            keyboard.add(keyboardButtons);

            keyboardButtons = new KeyboardRow();
            keyboardButtons.add(strings[2]);
            keyboardButtons.add(strings[3]);
            keyboard.add(keyboardButtons);
        } else {
            int rowCount = stringsLength / 4;
            int remainingButtons = stringsLength % 4;

            for (int i = 0; i < rowCount; i++) {
                keyboardButtons = new KeyboardRow();

                for (int j = 0; j < 4; j++) {
                    int index = i * 4 + j;
                    keyboardButtons.add(strings[index]);
                }

                keyboard.add(keyboardButtons);
            }

            if (remainingButtons > 0) {
                keyboardButtons = new KeyboardRow();

                for (int i = 0; i < remainingButtons; i++) {
                    int index = rowCount * 4 + i;
                    keyboardButtons.add(strings[index]);
                }

                keyboard.add(keyboardButtons);
            }
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

}
