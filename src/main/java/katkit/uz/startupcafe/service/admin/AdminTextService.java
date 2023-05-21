package katkit.uz.startupcafe.service.admin;

import katkit.uz.startupcafe.bot.SendingService;
import katkit.uz.startupcafe.enums.ProjectStatus;
import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.repository.ProfileRepository;
import katkit.uz.startupcafe.repository.ProjectRepository;
import katkit.uz.startupcafe.service.ButtonService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.SentenceService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class AdminTextService {
    private final ProfileService profileService;
    private final SentenceService sentenceService;
    private final ButtonService buttonService;
    private final SendingService sendingService;
    private final ProfileRepository profileRepository;
    private final ProjectRepository projectRepository;

    public AdminTextService(ProfileService profileService, SentenceService sentenceService, ButtonService buttonService, SendingService sendingService,
                            ProfileRepository profileRepository,
                            ProjectRepository projectRepository) {
        this.profileService = profileService;
        this.sentenceService = sentenceService;
        this.buttonService = buttonService;
        this.sendingService = sendingService;
        this.profileRepository = profileRepository;
        this.projectRepository = projectRepository;
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

    public void welcome(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        String languageCode = profileService.getLanguageCode(chatId);

        profileService.changeStep(chatId, Step.MAIN);

        sendMessage.setReplyMarkup(buttonService.getAdminMenu(chatId, languageCode));
        String sentence = sentenceService.getSentence(SentenceKey.START_ADMIN, languageCode);
        sendMessage.setText(sentence);
        sendingService.sendMessage(sendMessage);

    }


    public void toHomePage(Long chatId) {

        profileService.changeStep(chatId, Step.MAIN);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        String languageCode = profileService.getLanguageCode(chatId);

        sendMessage.setReplyMarkup(buttonService.getAdminMenu(chatId, languageCode));
        String sentence = sentenceService.getSentence(SentenceKey.HOME, languageCode);

        sendMessage.setText(sentence);
        sendingService.sendMessage(sendMessage);

    }

    public void sendStatistic(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        int userCount = profileRepository.getUserCount();
        int projectCount = projectRepository.getProjectCount(ProjectStatus.PUBLISHED);
        int availableProjectCount = projectRepository.getProjectCount(ProjectStatus.PUBLISHED, true);

        sendMessage.setText(String.format(sentenceService.getSentence(SentenceKey.STATISTIC, languageCode), userCount, projectCount, availableProjectCount));

        sendingService.sendMessage(sendMessage);
    }

    public void requestPost(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.POST_REQUEST, languageCode));
        sendMessage.setReplyMarkup(buttonService.getHomeMarkup(languageCode));

        profileService.changeStep(chatId, Step.POST_SEND);
        sendingService.sendMessage(sendMessage);
    }
}
