package katkit.uz.startupcafe.repository;


import jakarta.transaction.Transactional;
import katkit.uz.startupcafe.entity.ProfileEntity;
import katkit.uz.startupcafe.entity.ProjectEntity;
import katkit.uz.startupcafe.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends CrudRepository<ProjectEntity, Integer>, PagingAndSortingRepository<ProjectEntity, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE ProjectEntity p " +
            "SET p.description=?2 " +
            "WHERE  p.id =?1")
    void setDescription(Integer id, String description);

    ProjectEntity findFirstByProfileUserIdOrderByCreatedDateDesc(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE ProjectEntity p " +
            "SET p.status=?2 " +
            "WHERE  p.id =?1")
    void changeStatus(Integer id, ProjectStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE ProjectEntity p " +
            "SET p.technologies=?2 " +
            "WHERE  p.id =?1")
    void setTechnologies(Integer id, String technologies);

    Page<ProjectEntity> findByProfileUserIdAndStatusAndVisible(Long userid, ProjectStatus status, Pageable pageable, Boolean visible);

    @Query("select p from ProjectEntity as p " +
            "where p.profile.userId<>?1 and  ?2 not in elements( p.partnerList)  and p.status=?3  and p.visible=?4")
    Page<ProjectEntity> findByProfileUserIdAndStatusAndVisible(Long userId, ProfileEntity profile, ProjectStatus status, Boolean visible, Pageable pageable);

    Page<ProjectEntity> findByStatusAndVisible(ProjectStatus status, Boolean visible, Pageable pageable);

    Optional<ProjectEntity> findByIdAndVisible(int id, Boolean visible);

    @Modifying
    @Transactional
    @Query("UPDATE ProjectEntity p " +
            "SET p.visible=?2 " +
            "WHERE  p.id =?1")
    void changeVisible(Integer pId, boolean b);

    @Query("select p from ProjectEntity as p " +
            "where p.id=?2 and ?1 in elements( p.partnerList) ")
    ProjectEntity checkPartnerIsExists(ProfileEntity profile, Integer id);

    @Query("select p from ProjectEntity as p" +
            " where p.status=?2 and ?1 in  elements(p.partnerList) and p.visible=?3 ")
    Page<ProjectEntity> getJoinedProjectPage(ProfileEntity profile, ProjectStatus published, boolean pageable, Pageable b);

    @Query("select  p.profile from ProjectEntity as p " +
            "where p.id=?1  ")
    ProfileEntity getProfileByProjectId(Integer pId);

    @Query("select p.partnerList from ProjectEntity as p " +
            "where p.id=?1")
    List<ProfileEntity> getPartnerList(Integer pId);

    @Query("select count(p) from ProjectEntity  as p " +
            "where p.status=?1 and p.visible=?2")
    int getProjectCount(ProjectStatus published, boolean b);

    @Query("select count(p) from ProjectEntity  as p " +
            "where p.status=?1")
    int getProjectCount(ProjectStatus published);
}
