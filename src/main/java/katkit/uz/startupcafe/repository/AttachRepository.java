package katkit.uz.startupcafe.repository;

import jakarta.transaction.Transactional;
import katkit.uz.startupcafe.entity.AttachEntity;
import katkit.uz.startupcafe.enums.AttachType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AttachRepository extends CrudRepository<AttachEntity, Integer> {

    AttachEntity findByProjectId(Integer projectId);

    @Modifying
    @Transactional
    @Query("UPDATE AttachEntity " +
            "SET fileId=?2 , type=?3 " +
            "WHERE projectId=?1")
    void setFields(Integer id, String fileId, AttachType type);
}
