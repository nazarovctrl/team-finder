package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.OfficeService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.ProjectService;
import katkit.uz.startupcafe.service.SentenceService;
import org.springframework.stereotype.Component;

@Component
public class ProjectController {
    private final ProjectService projectService;
    private final OfficeService officeService;
    private final ProfileService profileService;
    private final SentenceService sentenceService;

    public ProjectController(ProjectService projectService, OfficeService officeService, ProfileService profileService, SentenceService sentenceService) {
        this.projectService = projectService;
        this.officeService = officeService;
        this.profileService = profileService;
        this.sentenceService = sentenceService;

    }

    public void handle(Long chatId, String text) {
        ButtonKey buttonKey = sentenceService.getButtonKey(text);

        if (buttonKey != null && buttonKey.equals(ButtonKey.BACK)) {
            Step step = profileService.getStep(chatId);
            switch (step) {
                case PROJECT_CREATE -> {
                    officeService.toOffice(chatId);
                    return;
                }
                case PROJECT_CREATE_DESCRIPTION -> {
                    projectService.toCreateProject(chatId);
                    return;
                }
                case PROJECT_CREATE_TECHNOLOGIES -> {
                    projectService.requestDescription(chatId);
                    return;
                }
                case PROJECT_CREATE_ATTACH -> {
                    projectService.requestTechnologies(chatId);
                    return;
                }
                case PROJECT_CREATE_CONFIRM -> {
                    projectService.requestPhoto(chatId);
                    return;
                }
            }
        }

        if (buttonKey != null) {
            switch (buttonKey) {
                case SKIP -> {
                    Step step = profileService.getStep(chatId);
                    if (step.equals(Step.PROJECT_CREATE_ATTACH)) {
                        projectService.confirmProject(chatId);
                    } else if (step.equals(Step.PROJECT_CREATE_TECHNOLOGIES)) {
                        projectService.requestPhoto(chatId);
                    }
                    return;
                }
                case CONFIRM -> {
                    projectService.publishProject(chatId);
                    return;
                }
                case CANCEL -> {
                    projectService.cancelProject(chatId);
                    return;
                }
            }

        }


        Step step = profileService.getStep(chatId);

        switch (step) {
            case PROJECT_CREATE -> {
                projectService.createProject(chatId, text);
                projectService.requestDescription(chatId);

            }
            case PROJECT_CREATE_DESCRIPTION -> {
                projectService.setDescription(chatId, text);
                projectService.requestTechnologies(chatId);
            }
            case PROJECT_CREATE_TECHNOLOGIES -> {
                projectService.setTechnologies(chatId, text);
                projectService.requestPhoto(chatId);
            }
            case PROJECT_FIND -> projectService.search(chatId, text, false);


        }
    }
}
