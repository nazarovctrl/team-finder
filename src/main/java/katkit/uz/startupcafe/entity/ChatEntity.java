package katkit.uz.startupcafe.entity;

import jakarta.persistence.*;
import katkit.uz.startupcafe.enums.ChatRole;
import katkit.uz.startupcafe.enums.ChatStatus;
import katkit.uz.startupcafe.enums.ChatType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table(name = "chats")
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Long chatId;

    @Column
    private String username;

    @Enumerated(EnumType.STRING)
    @Column
    private ChatRole role;

    @Enumerated(EnumType.STRING)
    @Column
    private ChatType type;

    @Column
    private String title;

    @Column(name = "invite_link")
    private String inviteLink;

    @Enumerated(EnumType.STRING)
    @Column
    private ChatStatus status = ChatStatus.NOT_ACTIVE;

    @Column
    private Boolean visible = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();


}
