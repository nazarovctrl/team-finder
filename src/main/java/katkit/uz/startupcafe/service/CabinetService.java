package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.bot.SendingService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class CabinetService {

    private final ProfileService profileService;
    private final ButtonService buttonService;
    private final SentenceService sentenceService;

    private final SendingService sendingService;

    public CabinetService(ProfileService profileService, ButtonService buttonService, SentenceService sentenceService, SendingService sendingService) {
        this.profileService = profileService;
        this.buttonService = buttonService;
        this.sentenceService = sentenceService;
        this.sendingService = sendingService;
    }

    public void toCabinet(Long chatId) {
        profileService.changeStep(chatId, Step.PROFILE);
        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROFILE, languageCode));
        sendMessage.setReplyMarkup(buttonService.getCabinetMarkup(languageCode));

        sendingService.sendMessage(sendMessage);

    }

    public void toEditCabinet(Long chatId) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT);
        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROFILE_EDIT, languageCode));
        sendMessage.setReplyMarkup(buttonService.getEditCabinetMarkup(languageCode));

        sendingService.sendMessage(sendMessage);

    }

    public void requestName(Long chatId) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT_NAME);

        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_NAME, languageCode));
        sendingService.sendMessage(sendMessage);
    }

    public void requestPhone(Long chatId) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT_PHONE);

        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_CONTACT, languageCode));
        sendMessage.setReplyMarkup(buttonService.getRequestContactButton(languageCode));

        sendingService.sendMessage(sendMessage);
    }

    public void requestBIO(Long chatId) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT_BIO);

        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_BIO, languageCode));

        sendingService.sendMessage(sendMessage);

    }

    public void requestProfession(Long chatId) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT_PROFESSION);

        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REQUEST_PROFESSION, languageCode));

        sendingService.sendMessage(sendMessage);
    }

    public void changePhoneNumber(Long chatId, String phoneNumber) {
        String languageCode = profileService.getLanguageCode(chatId);
        profileService.changeStep(chatId, Step.PROFILE_EDIT);
        profileService.changePhoneNumber(chatId, phoneNumber);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(sentenceService.getSentence(SentenceKey.NUMBER_CHANGED, languageCode));
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(buttonService.getEditCabinetMarkup(languageCode));

        sendingService.sendMessage(sendMessage);

    }

    public void changeName(Long chatId, String text) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT);
        profileService.changeName(chatId, text);

        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.NAME_CHANGED, languageCode));
        sendingService.sendMessage(sendMessage);

    }

    public void changeBIO(Long chatId, String text) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT);
        profileService.changeBIO(chatId, text);
        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.BIO_CHANGED, languageCode));
        sendingService.sendMessage(sendMessage);
    }

    public void changeProfession(Long chatId, String text) {
        profileService.changeStep(chatId, Step.PROFILE_EDIT);
        profileService.changeProfession(chatId, text);

        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROFESSION_CHANGED, languageCode));
        sendingService.sendMessage(sendMessage);
    }

    public void sendInformation(Long chatId) {

        String informationByUserId = profileService.getInformationByUserId(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setText(informationByUserId);
        sendMessage.setChatId(chatId);
        sendingService.sendMessage(sendMessage);
    }
}
