package katkit.uz.startupcafe.controller;


import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.SentenceService;
import katkit.uz.startupcafe.service.SignUpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SignUpController {
    private final SignUpService signUpService;
    private final ProfileService profileService;
    private final SentenceService sentenceService;

    public SignUpController(SignUpService signUpService, ProfileService profileService, SentenceService sentenceService) {
        this.signUpService = signUpService;
        this.profileService = profileService;
        this.sentenceService = sentenceService;
    }

    public void handle(Long chatId, String text) {
        ButtonKey buttonKey = sentenceService.getButtonKey(text);
        Step step = profileService.getStep(chatId);

        if (buttonKey != null) {
            switch (buttonKey) {
                case CONFIRM -> {
                    signUpService.saveRegistration(chatId);
                    return;
                }
                case CANCEL -> {
                    signUpService.cancelRegistration(chatId);
                    return;
                }
            }
        }


        if (buttonKey != null && buttonKey.equals(ButtonKey.BACK)) {
            switch (step) {
                case SIGN_UP_CONTACT -> signUpService.signUp(chatId);
                case SIGN_UP_PROFESSION -> signUpService.requestContact(chatId);
                case SIGN_UP_BIO -> signUpService.requestProfession(chatId);
                case SIGN_UP_CONFIRM -> signUpService.requestBIO(chatId);
            }
            return;
        }

        switch (step) {
            case SIGN_UP_NAME -> {
                profileService.changeName(chatId, text);
                signUpService.requestContact(chatId);
            }
            case SIGN_UP_PROFESSION -> {
                profileService.changeProfession(chatId, text);
                signUpService.requestBIO(chatId);
            }
            case SIGN_UP_BIO -> {
                profileService.changeBIO(chatId, text);
                signUpService.signUpConfirm(chatId);
            }
        }
    }

    public void handleContact(Message message) {
        Contact contact = message.getContact();
        String phoneNumber = contact.getPhoneNumber();
        profileService.changePhoneNumber(message.getChatId(), phoneNumber);
        signUpService.requestProfession(message.getChatId());
    }
}
