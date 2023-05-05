package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.entity.ProjectEntity;
import katkit.uz.startupcafe.enums.AttachType;
import katkit.uz.startupcafe.repository.AttachRepository;
import katkit.uz.startupcafe.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.util.List;

@Service
public class AttachService {
    private final AttachRepository attachRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    public AttachService(AttachRepository attachRepository, ProjectService projectService, ProjectRepository projectRepository) {
        this.attachRepository = attachRepository;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    public void saveVideo(Message message) {
        Video video = message.getVideo();


        ProjectEntity project = projectRepository.findFirstByProfileUserIdOrderByCreatedDateDesc(message.getChatId());
        attachRepository.setFields(project.getId(), video.getFileId(), AttachType.VIDEO);
        projectService.confirmProject(message.getChatId());
    }

    public void savePhoto(Message message) {
        List<PhotoSize> photo = message.getPhoto();
        ProjectEntity project = projectRepository.findFirstByProfileUserIdOrderByCreatedDateDesc(message.getChatId());
        attachRepository.setFields(project.getId(), photo.get(photo.size()-1).getFileId(), AttachType.PHOTO);
        projectService.confirmProject(message.getChatId());

    }
}
