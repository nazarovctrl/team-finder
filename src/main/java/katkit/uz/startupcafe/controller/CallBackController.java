package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.service.ProfileChatService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.ProjectPagingService;
import katkit.uz.startupcafe.service.ProjectService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class CallBackController {

    private final ProfileService profileService;
    private final ProjectService projectService;
    private final ProjectPagingService projectPagingService;
    private final ProfileChatService profileChatService;

    public CallBackController(ProfileService profileService, ProjectService projectService, ProjectPagingService projectPagingService, ProfileChatService profileChatService) {
        this.profileService = profileService;
        this.projectService = projectService;
        this.projectPagingService = projectPagingService;
        this.profileChatService = profileChatService;
    }

    public void handle(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        User user = callbackQuery.getFrom();
        String data = callbackQuery.getData();
        String[] split = data.split("/");

        if (split[0].equals("lang")) {
            profileService.changeLanguage(user.getId(), split[1], message.getMessageId());
            return;
        }

        if (split[0].equals("subscribe")) {
            profileChatService.checkSubscription(user.getId(), message.getMessageId());
            return;
        }

        if (split[0].equals("delete")) {
            projectPagingService.sendDeleteConfirmAndCancel(data, callbackQuery);
            return;
        }

        if (split[0].equals("delete_confirm")) {
            projectPagingService.deleteConfirmProject(data, callbackQuery);
            return;
        }

        if (split[0].equals("delete_cancel")) {
            projectPagingService.deleteCancelProject(data, callbackQuery);
            return;
        }

        if (split[0].equals("back")) {
            String key = split[1];
            if (key.equals("my")) {
                projectPagingService.toMyPrevPage(data, callbackQuery);
            } else if (key.equals("join")) {
                projectPagingService.toJoinPrevPage(data, callbackQuery);
            } else if (key.equals("admin")) {
                projectPagingService.toAdminPrevPage(data, callbackQuery);
            } else {
                projectPagingService.toPrevPage(data, callbackQuery);
            }
            return;
        }

        if (split[0].equals("next")) {
            String key = split[1];
            if (key.equals("my")) {
                projectPagingService.toMyNextPage(data, callbackQuery);
            } else if (key.equals("join")) {
                projectPagingService.toJoinNextPage(data, callbackQuery);
            } else if (key.equals("admin")) {
                projectPagingService.toAdminNextPage(data, callbackQuery);
            } else {
                projectPagingService.toNextPage(data, callbackQuery);
            }
            return;
        }

        if (split[0].equals("join")) {
            projectPagingService.joinProject(data, callbackQuery);
            return;
        }

        if (split[0].equals("add")) {
            projectPagingService.addToProject(data, callbackQuery);
            return;
        }
        if (split[0].equals("reject")) {
            projectPagingService.rejectJoinProject(data, callbackQuery);
            return;
        }

        if (split[0].equals("connect")) {
            projectPagingService.connect(data, callbackQuery);
            return;
        }

        if (split[0].equals("back-for-connect")) {
            projectPagingService.backForConnect(data, callbackQuery);
            return;
        }

    }
}
