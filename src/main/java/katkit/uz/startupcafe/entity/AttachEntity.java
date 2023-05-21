package katkit.uz.startupcafe.entity;

import jakarta.persistence.*;
import katkit.uz.startupcafe.enums.AttachType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attach")
public class AttachEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String fileId;

    @Enumerated(EnumType.STRING)
    @Column
    private AttachType type;

    @Column(name = "project_id")
    private Integer projectId;
    @OneToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private ProjectEntity project;
}