package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.entity.ProfileEntity;
import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.SentenceKey;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InlineButtonService {
    private final SentenceService sentenceService;
    public InlineButtonService(SentenceService sentenceService) {
        this.sentenceService = sentenceService;
    }
    public InlineKeyboardButton getBack(String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("◀️◀️");
        button.setCallbackData("back" + callback);
        return button;
    }

    public InlineKeyboardButton getCurrent(Integer currentPage, Integer allPage) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(currentPage + "/" + allPage);
        button.setCallbackData("current");
        return button;
    }

    public InlineKeyboardButton getNext(String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("➡️➡️️");
        button.setCallbackData("next" + callback);
        return button;
    }

    public InlineKeyboardButton getDelete(String callback, String languageCode) {
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText(sentenceService.getSentence(SentenceKey.DELETE, languageCode));
        delete.setCallbackData("delete" + callback);
        return delete;
    }

    public InlineKeyboardButton getJoin(String callback, String languageCode) {
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText(sentenceService.getSentence(SentenceKey.JOIN, languageCode));
        delete.setCallbackData("join" + callback);
        return delete;
    }

    public InlineKeyboardButton getJoinForChannel(String callback, String languageCode) {
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setUrl("https://t.me/startup_uzbot?start&");
        delete.setText(sentenceService.getSentence(SentenceKey.JOIN, languageCode));
        delete.setCallbackData("join" + callback);
        return delete;
    }

    public InlineKeyboardButton getDeleteConfirm(String callback, String languageCode) {
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText(sentenceService.getButtonText(ButtonKey.CONFIRM, languageCode));
        delete.setCallbackData("delete_confirm" + callback);
        return delete;
    }

    public InlineKeyboardButton getDeleteCancel(String callback, String languageCode) {
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText(sentenceService.getButtonText(ButtonKey.CANCEL, languageCode));
        delete.setCallbackData("delete_cancel" + callback);
        return delete;
    }

    public InlineKeyboardButton getAdd(String callback, String languageCode) {
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText(sentenceService.getSentence(SentenceKey.ADD, languageCode));
        delete.setCallbackData("add" + callback);
        return delete;
    }

    public InlineKeyboardButton getReject(String callback, String languageCode) {
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText(sentenceService.getSentence(SentenceKey.REJECT, languageCode));
        delete.setCallbackData("reject" + callback);
        return delete;
    }

    public List<InlineKeyboardButton> getJoinRow(String callback, String languageCode) {
        List<InlineKeyboardButton> joinRow = new ArrayList<>();
        joinRow.add(getJoin(callback, languageCode));
        return joinRow;
    }


    public List<InlineKeyboardButton> getBackAndNextRow(String callback, Integer currentPage, Integer allPage) {
        InlineKeyboardButton back = getBack(callback);
        InlineKeyboardButton next = getNext(callback);
        InlineKeyboardButton current = getCurrent(currentPage, allPage);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(back);
        row.add(current);
        row.add(next);
        return row;
    }

    public List<InlineKeyboardButton> getNextRow(String callback, Integer currentPage, Integer allPage) {
        InlineKeyboardButton next = getNext(callback);
        InlineKeyboardButton current = getCurrent(currentPage, allPage);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(current);
        row.add(next);
        return row;
    }

    public List<InlineKeyboardButton> getBackRow(String callback, Integer currentPage, Integer allPage) {
        InlineKeyboardButton back = getBack(callback);
        InlineKeyboardButton current = getCurrent(currentPage, allPage);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(back);
        row.add(current);
        return row;
    }

    public List<InlineKeyboardButton> getCurrentRow(Integer currentPage, Integer allPage) {
        InlineKeyboardButton current = getCurrent(currentPage, allPage);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(current);
        return row;
    }

    public List<InlineKeyboardButton> getDeleteRow(String callback, String languageCode) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(getDelete(callback, languageCode));
        return row;
    }

    public List<InlineKeyboardButton> getDeleteConfirmAndCancelRow(String callback, String languageCode) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(getDeleteCancel(callback, languageCode));
        row.add(getDeleteConfirm(callback, languageCode));
        return row;
    }

    public InlineKeyboardMarkup getBackMarkup(String callback, Integer currantPage, Integer allPage) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        keyboard.add(getBackRow(callback, currantPage, allPage));


        return markup;
    }

    public InlineKeyboardMarkup getNextMarkup(String callback, Integer currantPage, Integer allPage) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        keyboard.add(getNextRow(callback, currantPage, allPage));


        return markup;
    }

    public InlineKeyboardMarkup getCurrentMarkup(Integer currantPage, Integer allPage) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        keyboard.add(getCurrentRow(currantPage, allPage));
        return markup;
    }


    public InlineKeyboardMarkup getDeleteBackAndNextMarkup(String callback, Integer currantPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getBackAndNextRow(callback, currantPage, allPage));

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getConnectDeleteBackAndNextMarkup(String callback, Integer currantPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getConnectRow(callback, languageCode));
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getBackAndNextRow(callback, currantPage, allPage));

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getDeleteAndBackMarkup(String callback, Integer currantPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getBackRow(callback, currantPage, allPage));

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getConnectDeleteAndBackMarkup(String callback, Integer currantPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getConnectRow(callback, languageCode));
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getBackRow(callback, currantPage, allPage));

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getDeleteAndNextMarkup(String callback, Integer currantPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getNextRow(callback, currantPage, allPage));
        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getConnectDeleteAndNextMarkup(String callback, Integer currantPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getConnectRow(callback, languageCode));
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getNextRow(callback, currantPage, allPage));
        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getDeleteMarkup(String callback, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getDeleteRow(callback, languageCode));
        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getJoinMarkup(String callback, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getJoinRow(callback, languageCode));
        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getJoinMarkupForChannel(String callback, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> joinRow = new ArrayList<>();
        joinRow.add(getJoinForChannel(callback, languageCode));
        keyboard.add(joinRow);
        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getDeleteAndCurrentMarkup(String callback, Integer currentPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getCurrentRow(currentPage, allPage));

        return markup;
    }

    public InlineKeyboardMarkup getConnectDeleteAndCurrentMarkup(String callback, Integer currentPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        keyboard.add(getConnectRow(callback, languageCode));
        keyboard.add(getDeleteRow(callback, languageCode));
        keyboard.add(getCurrentRow(currentPage, allPage));

        return markup;
    }

    public InlineKeyboardMarkup getConnectAndDeleteMarkup(String callback, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        keyboard.add(getConnectRow(callback, languageCode));
        keyboard.add(getDeleteRow(callback, languageCode));

        return markup;
    }

    private List<InlineKeyboardButton> getConnectRow(String callback, String languageCode) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(sentenceService.getSentence(SentenceKey.CONNECT, languageCode));
        button.setCallbackData("connect" + callback);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }

    public InlineKeyboardMarkup getConfirmAndCancelMarkup(String callback, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getDeleteConfirmAndCancelRow(callback, languageCode));
        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getJoinAndCurrentMarkup(String callback, Integer currentPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getJoinRow(callback, languageCode));
        keyboard.add(getCurrentRow(currentPage, allPage));
        return new InlineKeyboardMarkup(keyboard);
    }



    public InlineKeyboardMarkup getJoinAndNextMarkup(String callback, Integer currentPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getJoinRow(callback, languageCode));
        keyboard.add(getNextRow(callback, currentPage, allPage));
        return new InlineKeyboardMarkup(keyboard);
    }


    public InlineKeyboardMarkup getJoinBackAndNextMarkup(String callback, Integer currentPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getJoinRow(callback, languageCode));
        keyboard.add(getBackAndNextRow(callback, currentPage, allPage));
        return new InlineKeyboardMarkup(keyboard);
    }


    public InlineKeyboardMarkup getBackAndNextMarkup(String callback, Integer currentPage, Integer allPage) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getBackAndNextRow(callback, currentPage, allPage));
        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getJoinAndBackMarkup(String callback, Integer currentPage, Integer allPage, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(getJoinRow(callback, languageCode));
        keyboard.add(getBackRow(callback, currentPage, allPage));
        return new InlineKeyboardMarkup(keyboard);
    }


    public InlineKeyboardMarkup getProfileAndBackMarkup(List<ProfileEntity> profileList, String callback, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        int k = 0;

        for (int j = 0; j < profileList.size(); j++) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            for (int i = 0; i < 2 && profileList.size() > i; i++, j++) {
                ProfileEntity profile = profileList.get(j);

                InlineKeyboardButton button = new InlineKeyboardButton();

                String text = "+" + profile.getPhone();
                button.setUrl("https://t.me/" + text);
                if (k == 0) {
                    k++;
                    button.setText(profile.getName() + sentenceService.getSentence(SentenceKey.CREATOR, languageCode) + "\n");
                } else {
                    button.setText(profile.getName() + "\n");
                }
                button.setCallbackData("profile");
                row.add(button);
            }
            keyboard.add(row);

        }
        keyboard.add(getBackRowForConnect(callback, languageCode));

        return new InlineKeyboardMarkup(keyboard);
    }

    private List<InlineKeyboardButton> getBackRowForConnect(String callback, String languageCode) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(sentenceService.getSentence(SentenceKey.BACK, languageCode));
        button.setCallbackData("back-for-connect" + callback);

        return new ArrayList<>(Collections.singleton(button));
    }

    public ReplyKeyboard getAddAndCancelMarkup(String callback, String languageCode) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(getAdd(callback, languageCode));
        row.add(getReject(callback, languageCode));
        keyboard.add(row);
        return new InlineKeyboardMarkup(keyboard);
    }


}
