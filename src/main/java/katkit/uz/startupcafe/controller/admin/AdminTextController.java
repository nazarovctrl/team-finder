package katkit.uz.startupcafe.controller.admin;

import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.ProjectPagingService;
import katkit.uz.startupcafe.service.ProjectService;
import katkit.uz.startupcafe.service.SentenceService;
import katkit.uz.startupcafe.service.admin.AdminTextService;
import org.springframework.stereotype.Component;

@Component
public class AdminTextController {
    private final AdminTextService adminTextService;
    private final SentenceService sentenceService;
    private final ProjectPagingService projectPagingService;
    private final ProjectService projectService;
    private final ProfileService profileService;

    public AdminTextController(AdminTextService adminTextService, SentenceService sentenceService, ProjectPagingService projectPagingService, ProjectService projectService, ProfileService profileService) {
        this.adminTextService = adminTextService;
        this.sentenceService = sentenceService;
        this.projectPagingService = projectPagingService;
        this.projectService = projectService;
        this.profileService = profileService;
    }

    public void handle(String text, Long chatId) {
        ButtonKey buttonKey = sentenceService.getButtonKey(text);

        if (buttonKey != null && buttonKey.equals(ButtonKey.HOME)) {
            adminTextService.toHomePage(chatId);
            return;
        }

        if (buttonKey != null) {
            switch (buttonKey) {
                case EDIT_PROJECTS -> {
                    projectPagingService.getAdminProjectPage(chatId);
                    return;
                }
                case PROJECT_FIND -> {
                    projectService.findProject(chatId);
                    return;
                }
                case STATISTICS -> {
                    adminTextService.sendStatistic(chatId);
                    return;
                }
                case POST_CREATE -> {
                    adminTextService.requestPost(chatId);
                    return;
                }
            }
        }

        Step step = profileService.getStep(chatId);
        if (step != null) {
            if (step == Step.PROJECT_FIND) {
                projectService.search(chatId, text, true);
            }
        }


    }

    public void replyToBotCommand(String text, Long chatId) {
        switch (text) {
            case "/start" -> adminTextService.welcome(chatId);
            case "/language" -> adminTextService.changeLanguage(chatId);
        }

    }

}
