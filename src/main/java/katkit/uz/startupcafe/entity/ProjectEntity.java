package katkit.uz.startupcafe.entity;

import jakarta.persistence.*;
import katkit.uz.startupcafe.enums.ProjectStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString

@Entity
@Table(name = "project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String title;

    @Column
    private String technologies;
    @Column
    private String description = " ";

    @Column(name = "profile_id")
    private Integer profileId;
    @ManyToOne
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Enumerated(EnumType.STRING)
    @Column
    private ProjectStatus status = ProjectStatus.NOT_PUBLISHED;

    @Column
    private Boolean visible = true;


    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "partner_list")
    private List<ProfileEntity> partnerList = new ArrayList<>();

}
