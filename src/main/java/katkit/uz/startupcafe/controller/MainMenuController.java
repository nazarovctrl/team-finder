package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.service.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MainMenuController {
    private final SentenceService sentenceService;
    private final ProjectService projectService;
    private final ProjectPagingService projectPagingService;
    private final OfficeService officeService;
    private final SignUpService signUpService;
    private final CabinetService cabinetService;

    public MainMenuController(SentenceService sentenceService, ProjectService projectService, ProjectPagingService projectPagingService, OfficeService officeService, SignUpService signUpService, CabinetService cabinetService) {
        this.sentenceService = sentenceService;
        this.projectService = projectService;
        this.projectPagingService = projectPagingService;
        this.officeService = officeService;
        this.signUpService = signUpService;
        this.cabinetService = cabinetService;
    }


    public void handle(String text, Message message) {
        ButtonKey buttonKey = sentenceService.getButtonKey(text);
        Long chatId = message.getChatId();

        switch (buttonKey) {
            case PROJECTS -> projectPagingService.getProjectPage(chatId);
            case OFFICE -> officeService.toOffice(chatId);
            case PROJECT_FIND -> projectService.findProject(chatId);
            case SIGN_UP -> signUpService.signUp(chatId);
            case PROFILE -> cabinetService.toCabinet(chatId);
        }
    }
}
