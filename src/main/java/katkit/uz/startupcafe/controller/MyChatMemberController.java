package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.bot.SendingService;
import katkit.uz.startupcafe.entity.ChatEntity;
import katkit.uz.startupcafe.enums.ChatRole;
import katkit.uz.startupcafe.enums.ChatType;
import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.service.ChatService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.SentenceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

@Component
public class MyChatMemberController {

    @Value("${bot.name}")
    private String botUsername;


    private final ChatService chatService;

    private final SendingService sendingService;

    private final SentenceService sentenceService;
    private final ProfileService profileService;

    public MyChatMemberController(ChatService chatService, SendingService sendingService, SentenceService sentenceService, ProfileService profileService) {
        this.chatService = chatService;
        this.sendingService = sendingService;
        this.sentenceService = sentenceService;

        this.profileService = profileService;
    }

    public void handle(ChatMemberUpdated myChatMember) {
        User user = myChatMember.getNewChatMember().getUser();

        if (!user.getIsBot() || !user.getUserName().equals(botUsername)) {
            return;
        }

        Chat chat = myChatMember.getChat();

        if (chat.getType().equals(ChatType.PRIVATE.name().toLowerCase())) {
            userChatController(myChatMember);
            return;
        }

        ChatMember newChatMember = myChatMember.getNewChatMember();

        if (chatService.isExists(chat.getId())) {
            chatService.updateRole(chat.getId(), ChatRole.valueOf(newChatMember.getStatus().toUpperCase()));
            return;
        }

        ChatEntity chatsEntity = new ChatEntity();
        chatsEntity.setChatId(chat.getId());
        chatsEntity.setType(ChatType.valueOf(chat.getType().toUpperCase())); // channel  supergroup  group
        chatsEntity.setTitle(chat.getTitle());
        chatsEntity.setUsername(chat.getUserName());
        chatsEntity.setRole(ChatRole.valueOf(newChatMember.getStatus().toUpperCase())); // administrator member  left
        chatsEntity.setInviteLink(chat.getInviteLink());

        chatService.addChat(chatsEntity);
    }

    private void userChatController(ChatMemberUpdated myChatMember) {


        ChatRole role = ChatRole.valueOf(myChatMember.getNewChatMember().getStatus().toUpperCase());
        Long userId = myChatMember.getFrom().getId();

        if (role.equals(ChatRole.KICKED)) {
            profileService.changeVisibleByUserId(userId, false);
            return;
        }

        if (role.equals(ChatRole.MEMBER)) {
            String languageCode = profileService.getLanguageCode(userId);
            profileService.changeVisibleByUserId(userId, true);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(sentenceService.getSentence(SentenceKey.RESTART, languageCode));
            sendMessage.setChatId(userId);
            sendingService.sendMessage(sendMessage);
        }
    }


}