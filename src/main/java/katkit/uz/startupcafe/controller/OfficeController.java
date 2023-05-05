package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.service.ProjectPagingService;
import katkit.uz.startupcafe.service.ProjectService;
import katkit.uz.startupcafe.service.SentenceService;
import org.springframework.stereotype.Component;

@Component
public class OfficeController {


    private final ProjectService projectService;
    private final SentenceService sentenceService;
    private final ProjectPagingService projectPagingService;


    public OfficeController(ProjectService projectService, SentenceService sentenceService, ProjectPagingService projectPagingService) {
        this.projectService = projectService;
        this.sentenceService = sentenceService;
        this.projectPagingService = projectPagingService;
    }


    public void handle(Long chatId, String text) {
        ButtonKey buttonKey = sentenceService.getButtonKey(text);

        if (buttonKey != null) {
            switch (buttonKey) {
                case MY_PROJECT -> projectPagingService.getMyProjectPage(chatId);

                case PROJECT_CREATE -> projectService.toCreateProject(chatId);

                case PROJECT_JOINED -> projectPagingService.getJoinedProjectPage(chatId);

            }
        }

    }

}

