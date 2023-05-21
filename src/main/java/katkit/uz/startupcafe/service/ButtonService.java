package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.entity.ChatEntity;
import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.util.ButtonUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class ButtonService {
    private final ProfileService profileService;
    private final SentenceService sentenceService;

    public ButtonService(@Lazy ProfileService profileService, SentenceService sentenceService) {
        this.profileService = profileService;
        this.sentenceService = sentenceService;
    }

    public ReplyKeyboardMarkup getMenu(String languageCode) {
        List<KeyboardRow> rowList = ButtonUtil.rowList(
                ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROJECTS, languageCode)),
                        ButtonUtil.button(sentenceService.getButtonText(ButtonKey.OFFICE, languageCode))),
                ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROJECT_FIND, languageCode)))
        );

        KeyboardRow row = ButtonUtil.row();

        row.add(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROFILE, languageCode)));
        rowList.add(row);

        return ButtonUtil.markup(rowList);
    }

    public ReplyKeyboardMarkup getMenu(Long chatId, String languageCode) {
        List<KeyboardRow> rowList = ButtonUtil.rowList(
                ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROJECTS, languageCode)),
                        ButtonUtil.button(sentenceService.getButtonText(ButtonKey.OFFICE, languageCode))),
                ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROJECT_FIND, languageCode)))
        );

        KeyboardRow row = ButtonUtil.row();
        if (profileService.isRegistered(chatId)) {
            row.add(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROFILE, languageCode)));
        } else {
            row.add(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.SIGN_UP, languageCode)));
        }
        rowList.add(row);

        return ButtonUtil.markup(rowList);
    }

    public ReplyKeyboardMarkup getAdminMenu(Long chatId, String languageCode) {
        List<KeyboardRow> rowList = ButtonUtil.rowList(
                ButtonUtil.row(
                        ButtonUtil.button(sentenceService.getButtonText(ButtonKey.EDIT_PROJECTS, languageCode)),
                        ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROJECT_FIND, languageCode))),
                ButtonUtil.row(
                        ButtonUtil.button(sentenceService.getButtonText(ButtonKey.STATISTICS, languageCode)),
                        ButtonUtil.button(sentenceService.getButtonText(ButtonKey.POST_CREATE, languageCode))

                ));

        return ButtonUtil.markup(rowList);
    }

    public InlineKeyboardMarkup getSubscribeButton(List<ChatEntity> chatList, String text) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        for (ChatEntity chatEntity : chatList) {

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(chatEntity.getTitle());
            button.setCallbackData("/" + chatEntity.getTitle());
            if (chatEntity.getUsername() == null) {
                button.setUrl(chatEntity.getInviteLink());
            } else {
                button.setUrl("https://t.me/" + chatEntity.getUsername());
            }
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton checkButton = new InlineKeyboardButton();
        checkButton.setCallbackData("subscribe");
        checkButton.setText(text);
        row2.add(checkButton);
        keyboard.add(row2);

        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup getLanguagesButton(String languageCode) {
        InlineKeyboardButton buttonUz = new InlineKeyboardButton();
        buttonUz.setText("Uz \uD83C\uDDFA\uD83C\uDDFF ");
        buttonUz.setCallbackData("lang/uz");

        InlineKeyboardButton buttonRu = new InlineKeyboardButton();
        buttonRu.setText("Ru \uD83C\uDDF7\uD83C\uDDFA ");
        buttonRu.setCallbackData("lang/ru");

        InlineKeyboardButton buttonEN = new InlineKeyboardButton();
        buttonEN.setText("En \uD83C\uDDEC\uD83C\uDDE7");
        buttonEN.setCallbackData("lang/en");

        List<InlineKeyboardButton> row = new ArrayList<>();

        if (!languageCode.equals("uz")) {
            row.add(buttonUz);
        }
        if (!languageCode.equals("ru")) {
            row.add(buttonRu);
        }
        if (!languageCode.equals("en")) {
            row.add(buttonEN);
        }

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    public ReplyKeyboardMarkup getBackAndHomeMarkup(String languageCode) {
        KeyboardRow backAndHomeRow = getBackAndHomeRow(languageCode);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(backAndHomeRow);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(keyboard);
        markup.setResizeKeyboard(true);
        return markup;
    }

    private KeyboardRow getBackAndHomeRow(String languageCode) {
        KeyboardButton back = new KeyboardButton();
        back.setText(sentenceService.getButtonText(ButtonKey.BACK, languageCode));

        KeyboardButton home = new KeyboardButton();
        home.setText(sentenceService.getButtonText(ButtonKey.HOME, languageCode));

        KeyboardRow row = new KeyboardRow();
        row.add(back);
        row.add(home);

        return row;
    }

    public ReplyKeyboardMarkup getRequestContactButton(String languageCode) {
        KeyboardButton contactButton = new KeyboardButton(sentenceService.getButtonText(ButtonKey.CONTACT, languageCode));
        contactButton.setRequestContact(true);
        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        keyboard.add(getBackAndHomeRow(languageCode));
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public ReplyKeyboardMarkup getConfirmMarkup(String languageCode) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        keyboard.add(ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.CONFIRM, languageCode)),
                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.CANCEL, languageCode))));

        KeyboardRow backAndHomeRow = getBackAndHomeRow(languageCode);
        keyboard.add(backAndHomeRow);
        return ButtonUtil.markup(keyboard);

    }

    public ReplyKeyboardMarkup getCabinetMarkup(String languageCode) {
        return ButtonUtil.markup(ButtonUtil.rowList(
                        ButtonUtil.row(
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROFILE_INFORMATION, languageCode)),
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROFILE_EDIT, languageCode))
                        ),
                        ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.HOME, languageCode)))
                )
        );
    }

    public ReplyKeyboardMarkup getEditCabinetMarkup(String languageCode) {
        return ButtonUtil.markup(ButtonUtil.rowList(
                        ButtonUtil.row(
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.CHANGE_NAME, languageCode)),
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.CHANGE_BIO, languageCode))
                        ),
                        ButtonUtil.row(
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.CHANGE_PHONE, languageCode)),
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.CHANGE_PROFESSION, languageCode))
                        ),
                        getBackAndHomeRow(languageCode)
                )
        );
    }

    public ReplyKeyboardMarkup getHomeMarkup(String languageCode) {
        return ButtonUtil.markup(
                ButtonUtil.rowList(
                        ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.HOME, languageCode))))
        );
    }


    public ReplyKeyboardMarkup getBackHomeAndSkipMarkup(String languageCode) {
        return ButtonUtil.markup(ButtonUtil.rowList(
                ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.SKIP, languageCode))),
                getBackAndHomeRow(languageCode))
        );
    }

    public ReplyKeyboardMarkup getOfficeMarkup(String languageCode) {
        return ButtonUtil.markup(
                ButtonUtil.rowList(
                        ButtonUtil.row(
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.MY_PROJECT, languageCode)),
                                ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROJECT_CREATE, languageCode))
                        ),
                        ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.PROJECT_JOINED, languageCode))),
                        getHomeRow(languageCode)
                )
        );
    }

    public KeyboardRow getHomeRow(String languageCode) {
        return ButtonUtil.row(ButtonUtil.button(sentenceService.getButtonText(ButtonKey.HOME, languageCode)));
    }

}
