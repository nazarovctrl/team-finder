package katkit.uz.startupcafe.controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateController {
    private final MessageController messageController;
    private final CallBackController callBackController;
    private final MyChatMemberController myChatMemberController;

    public UpdateController(MessageController messageController, CallBackController callBackController, MyChatMemberController myChatMemberController) {
        this.messageController = messageController;
        this.callBackController = callBackController;
        this.myChatMemberController = myChatMemberController;
    }

    public void handle(Update update) {

        if (update.hasMessage()) {
            messageController.handle(update.getMessage());
            return;
        }

        if (update.hasCallbackQuery()) {
            callBackController.handle(update.getCallbackQuery());
            return;
        }

        if (update.hasMyChatMember()){
            myChatMemberController.handle(update.getMyChatMember());
        }
    }

}
