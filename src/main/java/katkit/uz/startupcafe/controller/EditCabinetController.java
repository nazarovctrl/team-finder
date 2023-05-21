package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.CabinetService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.SentenceService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class EditCabinetController {
    private final SentenceService sentenceService;
    private final ProfileService profileService;
    private final CabinetService localCabinetService;

    public EditCabinetController(SentenceService sentenceService, ProfileService profileService, CabinetService localCabinetService) {
        this.sentenceService = sentenceService;
        this.profileService = profileService;
        this.localCabinetService = localCabinetService;
    }

    public void handle(Long chatId, String text) {
        ButtonKey buttonKey = sentenceService.getButtonKey(text);

        if (buttonKey != null && buttonKey.equals(ButtonKey.BACK)) {
            Step step = profileService.getStep(chatId);
            if (step != null && step.equals(Step.PROFILE_EDIT)) {
                localCabinetService.toCabinet(chatId);
                return;
            }
            localCabinetService.toEditCabinet(chatId);
            return;
        }

        if (buttonKey != null) {
            switch (buttonKey) {
                case CHANGE_NAME -> localCabinetService.requestName(chatId);
                case CHANGE_PHONE -> localCabinetService.requestPhone(chatId);
                case CHANGE_BIO -> localCabinetService.requestBIO(chatId);
                case CHANGE_PROFESSION -> localCabinetService.requestProfession(chatId);
            }
            return;
        }

        Step step = profileService.getStep(chatId);

        switch (step) {
            case PROFILE_EDIT_NAME -> localCabinetService.changeName(chatId, text);
            case PROFILE_EDIT_PHONE -> localCabinetService.changePhoneNumber(chatId, text);
            case PROFILE_EDIT_BIO -> localCabinetService.changeBIO(chatId, text);
            case PROFILE_EDIT_PROFESSION -> localCabinetService.changeProfession(chatId, text);
        }
    }

    public void changePhoneNumber(Message message) {
        Contact contact = message.getContact();
        String phoneNumber = contact.getPhoneNumber();
        localCabinetService.changePhoneNumber(message.getChatId(), phoneNumber);
    }
}
