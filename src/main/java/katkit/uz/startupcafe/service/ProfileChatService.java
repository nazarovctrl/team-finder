package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.bot.SendingService;
import katkit.uz.startupcafe.entity.ChatEntity;
import katkit.uz.startupcafe.enums.ChatRole;
import katkit.uz.startupcafe.enums.ChatStatus;
import katkit.uz.startupcafe.enums.SentenceKey;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

@Service
public class ProfileChatService {

    private final ChatService chatService;
    private final SendingService sendingService;
    private final ProfileService profileService;
    private final SentenceService sentenceService;
    private final ButtonService buttonService;

    public ProfileChatService(ChatService chatService, SendingService sendingService, ProfileService profileService, SentenceService sentenceService, ButtonService buttonService) {
        this.chatService = chatService;
        this.sendingService = sendingService;
        this.profileService = profileService;
        this.sentenceService = sentenceService;
        this.buttonService = buttonService;

    }

    public boolean checkSubscription(Long userId) {
        List<ChatEntity> unSubscribedChatList = getUnSubscribedChatList(userId);

        if (unSubscribedChatList.isEmpty()) {
            return true;
        }

        String languageCode = profileService.getLanguageCode(userId);

        InlineKeyboardMarkup markup = buttonService.getSubscribeButton(unSubscribedChatList, sentenceService.getSentence(SentenceKey.CHECK, languageCode));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.SUBSCRIBE, languageCode));
        sendMessage.setReplyMarkup(markup);
        sendingService.sendMessage(sendMessage);

        return false;
    }


    private List<ChatEntity> getUnSubscribedChatList(Long userId) {

        List<ChatEntity> unSubscribedChatList = chatService.getChatList(true, ChatStatus.ACTIVE, ChatRole.ADMINISTRATOR);

        Predicate<ChatEntity> predicate = (chatEntity) -> {
            GetChatMember getChatMember = new GetChatMember(String.valueOf(chatEntity.getChatId()), userId);
            ChatMember chatMember = sendingService.sendGetChatMember(getChatMember);
            ChatRole role = ChatRole.valueOf(chatMember.getStatus().toUpperCase());
            return role != ChatRole.MEMBER && role != ChatRole.ADMINISTRATOR && role != ChatRole.CREATOR;
        };


        unSubscribedChatList = unSubscribedChatList.stream().filter(predicate).toList();
        return unSubscribedChatList;
    }

    public void checkSubscription(Long userId, Integer messageId) {

        List<ChatEntity> unSubscribedChatList = getUnSubscribedChatList(userId);
        if (unSubscribedChatList.isEmpty()) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(userId);
            deleteMessage.setMessageId(messageId);
            sendingService.sendMessage(deleteMessage);
            return;
        }

        String languageCode = profileService.getLanguageCode(userId);

        InlineKeyboardMarkup markup = buttonService.getSubscribeButton(unSubscribedChatList, sentenceService.getSentence(SentenceKey.CHECK, languageCode));


        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(sentenceService.getSentence(SentenceKey.SUBSCRIBE, languageCode) + "\n"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

        editMessageText.setReplyMarkup(markup);
        editMessageText.setChatId(userId);
        editMessageText.setMessageId(messageId);
        sendingService.sendMessage(editMessageText);

    }


}
