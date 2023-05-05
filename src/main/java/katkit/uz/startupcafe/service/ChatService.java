package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.entity.ChatEntity;
import katkit.uz.startupcafe.enums.ChatRole;
import katkit.uz.startupcafe.enums.ChatStatus;
import katkit.uz.startupcafe.repository.ChatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatsRepository chatsRepository;


    public ChatService(ChatsRepository chatsRepository) {
        this.chatsRepository = chatsRepository;
    }


    public void updateRole(Long chatId, ChatRole left) {
        chatsRepository.updateRole(chatId, left);
    }

    public boolean isExists(Long chatId) {
        return chatsRepository.existsByChatId(chatId);
    }

    public void addChat(ChatEntity chatsEntity) {
        chatsRepository.save(chatsEntity);
    }


    public List<ChatEntity> getChatList(Boolean visible, ChatStatus status, ChatRole role) {
        return chatsRepository.getChats(visible, status, role);
    }
}
