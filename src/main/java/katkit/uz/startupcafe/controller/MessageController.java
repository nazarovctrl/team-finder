package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.controller.admin.AdminMessageController;
import katkit.uz.startupcafe.enums.ProfileRole;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.AttachService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.ProfileChatService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageController {

    private final ProfileChatService profileChatService;
    private final AttachService attachService;
    private final ProfileService profileService;
    private final TextController textController;
    private final SignUpController signUpController;
    private final EditCabinetController editCabinetController;
    private final AdminMessageController adminMessageController;

    public MessageController(ProfileChatService profileChatService, AttachService attachService, ProfileService profileService, TextController textController, SignUpController signUpController, EditCabinetController editCabinetController, AdminMessageController adminMessageController) {
        this.profileChatService = profileChatService;
        this.attachService = attachService;
        this.profileService = profileService;
        this.textController = textController;
        this.signUpController = signUpController;
        this.editCabinetController = editCabinetController;
        this.adminMessageController = adminMessageController;
    }

    public void handle(Message message) {

        profileService.addUser(message.getFrom());

        if (ProfileRole.ADMIN.equals(profileService.getByUserId(message.getChatId()).getRole())) {
            adminMessageController.handle(message);
            return;
        }

        if (message.hasText() && (message.getText().equals("/start") ||
                message.getText().equals("/help") ||
                message.getText().equals("/language"))) {

            textController.replyToBotCommand(message);
            return;
        }


        if (!profileChatService.checkSubscription(message.getFrom().getId())) {
            return;
        }

        if (message.hasText()) {
            textController.handle(message);
            return;
        }

        if (message.hasContact()) {
            Step step = profileService.getStep(message.getChatId());
            if (step.equals(Step.SIGN_UP_CONTACT)) {
                signUpController.handleContact(message);
                return;
            }

            if (step.equals(Step.PROFILE_EDIT_PHONE)) {
                editCabinetController.changePhoneNumber(message);
            }
        }


        if (message.hasPhoto()) {
            Step step = profileService.getStep(message.getChatId());
            if (step != null && step.equals(Step.PROJECT_CREATE_ATTACH)) {
                attachService.savePhoto(message);
                return;
            }
        }

        if (message.hasVideo()) {
            Step step = profileService.getStep(message.getChatId());
            if (step != null && step.equals(Step.PROJECT_CREATE_ATTACH)) {
                attachService.saveVideo(message);
            }
        }

    }


}
