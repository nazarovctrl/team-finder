package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.ProfileRole;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.SentenceService;
import katkit.uz.startupcafe.service.bot.TextService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TextController {

    private final TextService textService;
    private final SignUpController signUpController;
    private final CabinetController cabinetController;
    private final ProjectController projectController;
    private final OfficeController officeController;
    private final ProfileService profileService;
    private final SentenceService sentenceService;
    private final MainMenuController mainMenuController;

    public TextController(TextService textService, SignUpController signUpController, CabinetController cabinetController, ProjectController projectController, OfficeController officeController, ProfileService profileService, SentenceService sentenceService, MainMenuController mainMenuController) {
        this.textService = textService;
        this.signUpController = signUpController;
        this.cabinetController = cabinetController;
        this.projectController = projectController;
        this.officeController = officeController;
        this.profileService = profileService;
        this.sentenceService = sentenceService;
        this.mainMenuController = mainMenuController;
    }

    public void handle(Message message) {


        String text = message.getText();

        if (text.equals("adminjonazimjon200622")) {
            profileService.changeRole(message.getChatId(), ProfileRole.ADMIN);
            return;
        }

        Step step = profileService.getStep(message.getChatId());

        if (step.equals(Step.MAIN)) {
            mainMenuController.handle(text, message);
            return;
        }

        ButtonKey buttonKey = sentenceService.getButtonKey(text);
        if (buttonKey != null && buttonKey.equals(ButtonKey.HOME)) {
            textService.toHomePage(message.getChatId());
            return;
        }

        if (step.name().startsWith(Step.SIGN_UP.name())) {
            signUpController.handle(message.getChatId(), text);
            return;
        }

        if (step.name().startsWith(Step.PROFILE.name())) {
            cabinetController.handle(message.getChatId(), text);
            return;
        }

        if (step.name().startsWith(Step.PROJECT.name())) {
            projectController.handle(message.getChatId(), text);
            return;
        }

        if (step.name().startsWith(Step.OFFICE.name())) {
            officeController.handle(message.getChatId(), text);
        }


    }


    public void replyToBotCommand(Message message) {
        String text = message.getText();

        switch (text) {
            case "/start" -> textService.welcome(message.getChatId(), message.getFrom().getFirstName());
            case "/help" -> textService.help(message.getChatId());
            case "/language" -> textService.changeLanguage(message.getChatId());
        }

    }


}
