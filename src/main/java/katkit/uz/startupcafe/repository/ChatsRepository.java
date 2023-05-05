package katkit.uz.startupcafe.repository;

import jakarta.transaction.Transactional;
import katkit.uz.startupcafe.entity.ChatEntity;
import katkit.uz.startupcafe.enums.ChatRole;
import katkit.uz.startupcafe.enums.ChatStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatsRepository extends CrudRepository<ChatEntity, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE ChatEntity SET role=?2 WHERE chatId=?1")
    void updateRole(Long chatId, ChatRole left);


    boolean existsByChatId(Long chatId);


    @Query("from ChatEntity " +
            "where visible=?1 and status=?2 and role=?3")
    List<ChatEntity> getChats(Boolean visible, ChatStatus status, ChatRole role);
}
