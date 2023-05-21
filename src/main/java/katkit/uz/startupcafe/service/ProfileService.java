package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.bot.SendingService;
import katkit.uz.startupcafe.entity.ProfileEntity;
import katkit.uz.startupcafe.enums.ProfileRole;
import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final SentenceService sentenceService;
    private final SendingService sendingService;
    private final ButtonService buttonService;

    public ProfileService(ProfileRepository profileRepository, SentenceService sentenceService, SendingService sendingService, ButtonService buttonService) {
        this.profileRepository = profileRepository;
        this.sentenceService = sentenceService;
        this.sendingService = sendingService;
        this.buttonService = buttonService;
    }


    public void addUser(User user) {
        if (profileRepository.existsByUserId(user.getId())) {
            return;
        }
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setUserId(user.getId());
        profileEntity.setUsername(user.getUserName());

        if (user.getLanguageCode() == null) {
            profileEntity.setLanguageCode("ru");
        } else {
            profileEntity.setLanguageCode(user.getLanguageCode());
        }

        profileRepository.save(profileEntity);
    }

    public String getLanguageCode(Long userId) {
        return profileRepository.getLanguageCode(userId);
    }

    public void changeLanguage(Long userId, String languageCode, Integer messageId) {
        profileRepository.changeLanguageByUserId(userId, languageCode);


        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(userId);
        deleteMessage.setMessageId(messageId);
        sendingService.sendMessage(deleteMessage);


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.LANGUAGE_CHANGED, languageCode));

        Step step = profileRepository.getStepByUserId(userId);

        if (step == null) {
            step = Step.MAIN;
        }

        if (step.name().startsWith(Step.PROFILE.name())) {
            sendMessage.setReplyMarkup(buttonService.getEditCabinetMarkup(languageCode));
        }

        switch (step) {
            case MAIN -> sendMessage.setReplyMarkup(buttonService.getMenu(userId, languageCode));
            case SIGN_UP_BIO, SIGN_UP_PROFESSION, PROJECT_CREATE_DESCRIPTION, PROJECT_CREATE ->
                    sendMessage.setReplyMarkup(buttonService.getBackAndHomeMarkup(languageCode));
            case SIGN_UP_CONTACT, PROFILE_EDIT_PHONE ->
                    sendMessage.setReplyMarkup(buttonService.getRequestContactButton(languageCode));
            case SIGN_UP_CONFIRM, PROJECT_CREATE_CONFIRM ->
                    sendMessage.setReplyMarkup(buttonService.getConfirmMarkup(languageCode));
            case PROFILE -> sendMessage.setReplyMarkup(buttonService.getCabinetMarkup(languageCode));
            case SIGN_UP_NAME, PROJECT_FIND -> sendMessage.setReplyMarkup(buttonService.getHomeMarkup(languageCode));
            case PROJECT_CREATE_ATTACH, PROJECT_CREATE_TECHNOLOGIES ->
                    sendMessage.setReplyMarkup(buttonService.getBackHomeAndSkipMarkup(languageCode));
            case OFFICE -> sendMessage.setReplyMarkup(buttonService.getOfficeMarkup(languageCode));
        }


        sendingService.sendMessage(sendMessage);

    }

    public boolean isRegistered(Long chatId) {
        return profileRepository.isRegistered(chatId);
    }


    public void changeStep(Long userId, Step step) {
        profileRepository.changeStep(userId, step);
    }

    public void changeVisibleByUserId(Long userId, boolean visible) {
        profileRepository.changeVisibleByUserId(userId, visible);
    }

    public Step getStep(Long userId) {
        return profileRepository.getStepByUserId(userId);
    }

    public void changeName(Long userId, String name) {
        profileRepository.changeNameByChatId(userId, name);
    }

    public void changePhoneNumber(Long userId, String phoneNumber) {
        profileRepository.changePhoneNumber(userId, phoneNumber);
    }

    public void changeBIO(Long userId, String text) {
        profileRepository.changeBIOByUserId(userId, text);
    }

    public String getInformationByUserId(Long userId) {

        ProfileEntity profile = profileRepository.findByUserId(userId);
        String languageCode = profile.getLanguageCode();

        String information = "*%s:* _" + profile.getName() + "_ \n" +
                "*%s:* _" + profile.getPhone() + "_ \n" +
                "*%s:* \n_" + profile.getBio() + "_ \n";

        return String.format(information,
                sentenceService.getSentence(SentenceKey.NAME, languageCode),
                sentenceService.getSentence(SentenceKey.PHONE_NUMBER, languageCode),
                sentenceService.getSentence(SentenceKey.INFORMATION, languageCode)
        );
    }

    public String getInformation(ProfileEntity profile) {
        String languageCode = profile.getLanguageCode();
        String username = profile.getUsername() != null ? profile.getUsername() : " ";
        String information = "*%s:* _" + profile.getName() + "_ \n" +
                "*%s:* _" + profile.getPhone() + "_ \n" +
                "*Username:* _" + username + "_ \n" +
                "*%s:* \n_" + profile.getBio() + "_ \n";

        return String.format(information,
                sentenceService.getSentence(SentenceKey.NAME, languageCode),
                sentenceService.getSentence(SentenceKey.PHONE_NUMBER, languageCode),
                sentenceService.getSentence(SentenceKey.INFORMATION, languageCode)
        );
    }

    public void saveRegistration(Long chatId) {
        profileRepository.changeRegisteredField(chatId, true);
    }

    public void changeProfession(Long chatId, String text) {
        profileRepository.changeProfessionByUserId(chatId, text);
    }

    public Integer getId(Long userId) {
        return profileRepository.getIdByUserId(userId);
    }

    public ProfileEntity getByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    public void changeRole(Long userId, ProfileRole admin) {
        profileRepository.changeRole(userId, admin);
    }

    public List<ProfileEntity> getUserList() {
        return profileRepository.findByVisible(true);
    }
}
