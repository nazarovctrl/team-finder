package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.bot.SendingService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class OfficeService {

    private final SentenceService sentenceService;
    private final ButtonService buttonService;

    private final ProfileService profileService;
    private final SendingService sendingService;

    public OfficeService(SentenceService sentenceService, ButtonService buttonService, ProfileService profileService, SendingService sendingService) {
        this.sentenceService = sentenceService;
        this.buttonService = buttonService;
        this.profileService = profileService;
        this.sendingService = sendingService;
    }

    public void sendRegister(Long chatId) {

        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.REGISTER, languageCode));

        sendingService.sendMessage(sendMessage);
    }

    public void toOffice(Long chatId) {

        boolean registered = profileService.isRegistered(chatId);
        if (!registered) {
            sendRegister(chatId);
            return;
        }

        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.OFFICE, languageCode));
        sendMessage.setReplyMarkup(buttonService.getOfficeMarkup(languageCode));

        sendingService.sendMessage(sendMessage);

        profileService.changeStep(chatId, Step.OFFICE);
    }
}
