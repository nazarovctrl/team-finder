package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.ButtonService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.SentenceService;
import katkit.uz.startupcafe.service.bot.SendingService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Service
public class TextService {

    private final SendingService sendingService;

    private final ProfileService profileService;
    private final SentenceService sentenceService;

    private final ButtonService buttonService;

    public TextService(SendingService sendingService, ProfileService profileService, SentenceService sentenceService, ButtonService buttonService) {
        this.sendingService = sendingService;
        this.profileService = profileService;
        this.sentenceService = sentenceService;
        this.buttonService = buttonService;
    }

    public void welcome(Long chatId, String name) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        String languageCode = profileService.getLanguageCode(chatId);

        profileService.changeStep(chatId, Step.MAIN);

        sendMessage.setReplyMarkup(buttonService.getMenu(chatId, languageCode));
        String sentence = sentenceService.getSentence(SentenceKey.START, languageCode);

        sendMessage.setText(String.format(sentence, name));
        sendingService.sendMessage(sendMessage);

    }

    public void toHomePage(Long chatId) {


        profileService.changeStep(chatId, Step.MAIN);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        String languageCode = profileService.getLanguageCode(chatId);

        sendMessage.setReplyMarkup(buttonService.getMenu(chatId, languageCode));
        String sentence = sentenceService.getSentence(SentenceKey.HOME, languageCode);

        sendMessage.setText(sentence);
        sendingService.sendMessage(sendMessage);

    }


    public void changeLanguage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        String languageCode = profileService.getLanguageCode(chatId);
        String sentence = sentenceService.getSentence(SentenceKey.LANGUAGE, languageCode);
        sendMessage.setText(String.format(sentence, languageCode));

        sendMessage.setReplyMarkup(buttonService.getLanguagesButton(languageCode));
        sendingService.sendMessage(sendMessage);
    }

    public void help(Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);


        InputFile inputFile = new InputFile();
        inputFile.setMedia("AgACAgIAAxkBAAIC_WQAATaX5NWNxD9nsePLI03rMS6yaAACRsIxG392-ErCzmiK73IKuQEAAwIAA3gAAy4E");
        sendPhoto.setPhoto(inputFile);

        String languageCode = profileService.getLanguageCode(chatId);

        String sentence = sentenceService.getSentence(SentenceKey.HELP, languageCode);
        sendPhoto.setCaption(sentence);

        sendingService.sendMessage(sendPhoto);
    }


}

