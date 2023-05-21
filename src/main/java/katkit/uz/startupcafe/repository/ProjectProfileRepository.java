package katkit.uz.startupcafe.repository;

import katkit.uz.startupcafe.entity.ProjectProfileEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ProjectProfileRepository extends CrudRepository<ProjectProfileEntity, Integer>, PagingAndSortingRepository<ProjectProfileEntity, Integer> {
    @Modifying
    @Transactional
    int deleteByProjectIdAndProfileId(Integer projectId, Integer profileId);

    List<ProjectProfileEntity> findByProjectIdAndProfileUserId(Integer projectId, Long profileUserId);

}
