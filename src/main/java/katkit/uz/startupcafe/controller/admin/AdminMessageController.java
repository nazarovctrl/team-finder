package katkit.uz.startupcafe.controller.admin;

import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.ProfileService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class AdminMessageController {
    private final AdminTextController adminTextController;
    private final ProfileService profileService;
    private final PostController postController;

    public AdminMessageController(AdminTextController adminTextController, ProfileService profileService, PostController postController) {
        this.adminTextController = adminTextController;
        this.profileService = profileService;
        this.postController = postController;
    }

    public void handle(Message message) {

        if (message.hasText() && (message.getText().equals("/start") || message.getText().equals("/help"))) {
            adminTextController.replyToBotCommand(message.getText(), message.getChatId());
            return;
        }


        Step step = profileService.getStep(message.getChatId());
        if (step != null && step.equals(Step.POST_SEND)) {
            postController.handle(message);
            return;
        }

        if (message.hasText()) {
            adminTextController.handle(message.getText(), message.getChatId());
            return;
        }

    }
}
