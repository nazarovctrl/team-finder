package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.bot.SendingService;
import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.enums.Step;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static katkit.uz.startupcafe.enums.Step.SIGN_UP_NAME;

@Service
public class SignUpService {
    private final ProfileService profileService;
    private final SentenceService sentenceService;
    private final ButtonService buttonService;
    private final SendingService sendingService;

    public SignUpService(ProfileService profileService, SentenceService sentenceService, ButtonService buttonService, SendingService sendingService) {
        this.profileService = profileService;
        this.sentenceService = sentenceService;
        this.buttonService = buttonService;
        this.sendingService = sendingService;
    }

    public void signUp(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_NAME, languageCode));
        sendMessage.setReplyMarkup(buttonService.getHomeMarkup(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, SIGN_UP_NAME);
    }

    public void requestContact(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_CONTACT, languageCode));
        sendMessage.setReplyMarkup(buttonService.getRequestContactButton(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.SIGN_UP_CONTACT);
    }

    public void requestProfession(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_PROFESSION, languageCode));
        sendMessage.setReplyMarkup(buttonService.getBackAndHomeMarkup(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.SIGN_UP_PROFESSION);
    }

    public void requestBIO(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_BIO, languageCode));
        sendMessage.setReplyMarkup(buttonService.getBackAndHomeMarkup(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.SIGN_UP_BIO);
    }

    public void signUpConfirm(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);

        String informationByUserId = profileService.getInformationByUserId(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setText(informationByUserId);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(buttonService.getConfirmMarkup(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.SIGN_UP_CONFIRM);
    }


    public void saveRegistration(Long chatId) {
        profileService.saveRegistration(chatId);
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(buttonService.getMenu(languageCode));
        sendMessage.setText(sentenceService.getSentence(SentenceKey.SIGN_UP_CONFIRM, languageCode));

        profileService.changeStep(chatId, Step.MAIN);
        sendingService.sendMessage(sendMessage);
    }

    public void cancelRegistration(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(buttonService.getMenu(languageCode));
        sendMessage.setText(sentenceService.getSentence(SentenceKey.SIGN_UP_CANCEL, languageCode));

        profileService.changeStep(chatId, Step.MAIN);
        sendingService.sendMessage(sendMessage);
    }
}
