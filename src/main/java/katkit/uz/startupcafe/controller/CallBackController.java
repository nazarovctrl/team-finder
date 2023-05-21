package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.service.ProfileChatService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.ProjectPagingService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class CallBackController {
    private final ProfileService profileService;
    private final ProjectPagingService projectPagingService;
    private final ProfileChatService profileChatService;

    public CallBackController(ProfileService profileService, ProjectPagingService projectPagingService, ProfileChatService profileChatService) {
        this.profileService = profileService;
        this.projectPagingService = projectPagingService;
        this.profileChatService = profileChatService;
    }

    public void handle(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        User user = callbackQuery.getFrom();
        String data = callbackQuery.getData();
        String[] split = data.split("/");

        if (split[0].equals("back")) {
            String key = split[1];
            switch (key) {
                case "my" -> projectPagingService.toMyPrevPage(data, callbackQuery);
                case "join" -> projectPagingService.toJoinPrevPage(data, callbackQuery);
                case "admin" -> projectPagingService.toAdminPrevPage(data, callbackQuery);
                default -> projectPagingService.toPrevPage(data, callbackQuery);
            }
            return;
        }

        if (split[0].equals("next")) {
            String key = split[1];
            switch (key) {
                case "my" -> projectPagingService.toMyNextPage(data, callbackQuery);
                case "join" -> projectPagingService.toJoinNextPage(data, callbackQuery);
                case "admin" -> projectPagingService.toAdminNextPage(data, callbackQuery);
                default -> projectPagingService.toNextPage(data, callbackQuery);
            }
            return;
        }

        switch (split[0]) {
            case "lang" -> profileService.changeLanguage(user.getId(), split[1], message.getMessageId());
            case "subscribe" -> profileChatService.checkSubscription(user.getId(), message.getMessageId());
            case "delete" -> projectPagingService.sendDeleteConfirmAndCancel(data, callbackQuery);
            case "delete_confirm" -> projectPagingService.deleteConfirmProject(data, callbackQuery);
            case "delete_cancel" -> projectPagingService.deleteCancelProject(data, callbackQuery);
            case "join" -> projectPagingService.joinProject(data, callbackQuery);
            case "add" -> projectPagingService.addToProject(data, callbackQuery);
            case "reject" -> projectPagingService.rejectJoinProject(data, callbackQuery);
            case "connect" -> projectPagingService.connect(data, callbackQuery);
            case "back-for-connect" -> projectPagingService.backForConnect(data, callbackQuery);
        }

    }
}
