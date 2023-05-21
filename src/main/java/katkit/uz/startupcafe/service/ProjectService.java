package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.bot.SendingService;
import katkit.uz.startupcafe.entity.*;
import katkit.uz.startupcafe.enums.*;
import katkit.uz.startupcafe.repository.AttachRepository;
import katkit.uz.startupcafe.repository.ProjectProfileRepository;
import katkit.uz.startupcafe.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Optional;

import static katkit.uz.startupcafe.enums.Step.PROJECT_FIND;

@Service
public class ProjectService {
    private final AttachRepository attachRepository;
    private final ChatService chatService;
    private final ProjectProfileRepository projectProfileRepository;
    private final ProjectRepository projectRepository;
    private final ProfileService profileService;
    private final SendingService sendingService;
    private final SentenceService sentenceService;
    private final ButtonService buttonService;
    private final InlineButtonService inlineButtonService;

    public ProjectService(AttachRepository attachRepository, ChatService chatService, ProjectProfileRepository projectProfileRepository, ProjectRepository projectRepository, ProfileService profileService, SendingService sendingService, SentenceService sentenceService, ButtonService buttonService, InlineButtonService inlineButtonService) {
        this.attachRepository = attachRepository;
        this.chatService = chatService;
        this.projectProfileRepository = projectProfileRepository;
        this.projectRepository = projectRepository;
        this.profileService = profileService;
        this.sendingService = sendingService;
        this.sentenceService = sentenceService;
        this.buttonService = buttonService;
        this.inlineButtonService = inlineButtonService;
    }

    public void toCreateProject(Long chatId) {

        profileService.changeStep(chatId, Step.PROJECT_CREATE);
        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_CREATE, languageCode));
        sendMessage.setReplyMarkup(buttonService.getBackAndHomeMarkup(languageCode));
        sendingService.sendMessage(sendMessage);
    }

    public void findProject(Long chatId) {

        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.FIND_PROJECT, languageCode));
        sendMessage.setReplyMarkup(buttonService.getHomeMarkup(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, PROJECT_FIND);

    }

    public void createProject(Long chatId, String title) {

        ProjectEntity project = new ProjectEntity();
        project.setTitle(title);
        project.setProfileId(profileService.getId(chatId));
        projectRepository.save(project);

        AttachEntity attach = new AttachEntity();
        attach.setProjectId(project.getId());
        attachRepository.save(attach);
    }

    public void requestDescription(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_DESCRIPTION, languageCode));
        sendMessage.setReplyMarkup(buttonService.getBackAndHomeMarkup(languageCode));

        sendingService.sendMessage(sendMessage);

        profileService.changeStep(chatId, Step.PROJECT_CREATE_DESCRIPTION);

    }

    public void setDescription(Long chatId, String description) {
        ProjectEntity project = projectRepository.findFirstByProfileUserIdOrderByCreatedDateDesc(chatId);
        projectRepository.setDescription(project.getId(), description);
    }

    public void requestPhoto(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_PHOTO, languageCode));
        sendMessage.setReplyMarkup(buttonService.getBackHomeAndSkipMarkup(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.PROJECT_CREATE_ATTACH);

    }

    public String getInformation(String languageCode, ProjectEntity project) {
        String technologies;
        if (project.getTechnologies() == null) {
            technologies = sentenceService.getSentence(SentenceKey.TECHNOLOGIES_NULL, languageCode);
        } else {
            technologies = project.getTechnologies();
        }

        String sentence = sentenceService.getSentence(SentenceKey.PROJECT_CREATE_INFORMATION, languageCode);

        return String.format(sentence, project.getId(), project.getTitle(), project.getDescription(), technologies);

    }

    public String getInformationForChannel(String languageCode, ProjectEntity project) {
        String technologies;
        if (project.getTechnologies() == null) {
            technologies = sentenceService.getSentence(SentenceKey.TECHNOLOGIES_NULL, languageCode);
        } else {
            technologies = project.getTechnologies();
        }

        String sentence = sentenceService.getSentence(SentenceKey.PROJECT_INFORMATION, languageCode);

        return String.format(sentence, project.getId(), project.getTitle(), project.getDescription(), technologies, project.getProfile().getUserId(), project.getProfile().getName());

    }

    private void sendInfo(Long chatId, String languageCode, ProjectEntity project, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setText(getInformation(languageCode, project));
        sendingService.sendMessage(sendMessage);
    }

    private void sendInfoAfterJoin(Long chatId, String languageCode, ProjectEntity project, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setText(getInformationForChannel(languageCode, project));
        sendingService.sendMessage(sendMessage);
    }

    private void sendInfo(String languageCode, ProjectEntity project) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setText(getInformationForChannel(languageCode, project));
        List<ChatEntity> chatList = chatService.getChatList(true, ChatStatus.ACTIVE, ChatRole.ADMINISTRATOR);
        chatList.forEach(chat -> {
            sendMessage.setChatId(chat.getChatId());
            sendingService.sendMessage(sendMessage);
        });

    }

    private void sendInfWithPhotoAfterJoin(Long chatId, String languageCode, ProjectEntity project, AttachEntity attach, InlineKeyboardMarkup markup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setReplyMarkup(markup);
        sendPhoto.setPhoto(new InputFile(attach.getFileId()));
        sendPhoto.setParseMode(ParseMode.HTML);
        sendPhoto.setCaption(getInformationForChannel(languageCode, project));
        sendingService.sendMessage(sendPhoto);
    }

    private void sendInfWithPhoto(Long chatId, String languageCode, ProjectEntity project, AttachEntity attach, InlineKeyboardMarkup markup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setReplyMarkup(markup);
        sendPhoto.setPhoto(new InputFile(attach.getFileId()));
        sendPhoto.setParseMode(ParseMode.MARKDOWN);
        sendPhoto.setCaption(getInformation(languageCode, project));
        sendingService.sendMessage(sendPhoto);
    }

    private void sendInfWithVideoAfterJoin(Long chatId, String languageCode, ProjectEntity project, AttachEntity attach, InlineKeyboardMarkup markup) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId);
        sendVideo.setReplyMarkup(markup);
        sendVideo.setVideo(new InputFile(attach.getFileId()));
        sendVideo.setParseMode(ParseMode.HTML);
        sendVideo.setCaption(getInformationForChannel(languageCode, project));
        sendingService.sendMessage(sendVideo);
    }

    private void sendInfWithVideo(Long chatId, String languageCode, ProjectEntity project, AttachEntity attach, InlineKeyboardMarkup markup) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId);
        sendVideo.setReplyMarkup(markup);
        sendVideo.setVideo(new InputFile(attach.getFileId()));
        sendVideo.setParseMode(ParseMode.MARKDOWN);
        sendVideo.setCaption(getInformation(languageCode, project));
        sendingService.sendMessage(sendVideo);
    }

    private void sendInfWithPhoto(String languageCode, ProjectEntity project, AttachEntity attach) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(attach.getFileId()));
        sendPhoto.setCaption(getInformationForChannel(languageCode, project));
        sendPhoto.setParseMode(ParseMode.HTML);

        List<ChatEntity> chatList = chatService.getChatList(true, ChatStatus.ACTIVE, ChatRole.ADMINISTRATOR);
        chatList.forEach(chatEntity -> {
            sendPhoto.setChatId(chatEntity.getChatId());
            sendingService.sendMessage(sendPhoto);
        });
    }

    private void sendInfWithVideo(String languageCode, ProjectEntity project, AttachEntity attach) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setVideo(new InputFile(attach.getFileId()));
        sendVideo.setParseMode(ParseMode.HTML);
        sendVideo.setCaption(getInformationForChannel(languageCode, project));

        List<ChatEntity> chatList = chatService.getChatList(true, ChatStatus.ACTIVE, ChatRole.ADMINISTRATOR);
        chatList.forEach(chatEntity -> {
            sendVideo.setChatId(chatEntity.getChatId());
            sendingService.sendMessage(sendVideo);
        });
    }

    public void sendProjectInformation(ProjectEntity project, Long chatId, String languageCode, InlineKeyboardMarkup markup) {
        AttachEntity attach = attachRepository.findByProjectId(project.getId());

        if (attach == null || attach.getFileId() == null || attach.getFileId().isBlank()) {
            sendInfo(chatId, languageCode, project, markup);
            return;
        }

        switch (attach.getType()) {
            case PHOTO -> sendInfWithPhoto(chatId, languageCode, project, attach, markup);
            case VIDEO -> sendInfWithVideo(chatId, languageCode, project, attach, markup);
        }

    }

    public void sendProjectInformationToChannel(ProjectEntity project, String languageCode) {
        AttachEntity attach = attachRepository.findByProjectId(project.getId());

        if (attach == null || attach.getFileId() == null || attach.getFileId().isBlank()) {
            sendInfo(languageCode, project);
            return;
        }

        switch (attach.getType()) {
            case PHOTO -> sendInfWithPhoto(languageCode, project, attach);
            case VIDEO -> sendInfWithVideo(languageCode, project, attach);
        }

    }

    public void confirmProject(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        ProjectEntity project = projectRepository.findFirstByProfileUserIdOrderByCreatedDateDesc(chatId);
        sendProjectInformation(project, chatId, languageCode, null);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.IS_IT_TRUE, languageCode));
        sendMessage.setReplyMarkup(buttonService.getConfirmMarkup(languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.PROJECT_CREATE_CONFIRM);

    }

    public void cancelProject(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);


        ProjectEntity first = projectRepository.findFirstByProfileUserIdOrderByCreatedDateDesc(chatId);
        projectRepository.delete(first);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(buttonService.getOfficeMarkup(languageCode));
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_CREATE_CANCEL, languageCode));

        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.OFFICE);
    }

    public void publishProject(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);

        ProjectEntity project = projectRepository.findFirstByProfileUserIdOrderByCreatedDateDesc(chatId);
        projectRepository.changeStatus(project.getId(), ProjectStatus.PUBLISHED);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(buttonService.getOfficeMarkup(languageCode));
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_CREATE_FINISHED, languageCode));
        sendingService.sendMessage(sendMessage);
        profileService.changeStep(chatId, Step.OFFICE);

        sendProjectInformationToChannel(project, languageCode);

    }

    public void requestTechnologies(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_CREATE_TECHNOLOGIES, languageCode));
        sendMessage.setReplyMarkup(buttonService.getBackHomeAndSkipMarkup(languageCode));

        sendingService.sendMessage(sendMessage);

        profileService.changeStep(chatId, Step.PROJECT_CREATE_TECHNOLOGIES);

    }

    public void setTechnologies(Long chatId, String technologies) {
        ProjectEntity project = projectRepository.findFirstByProfileUserIdOrderByCreatedDateDesc(chatId);
        project.setTechnologies(technologies);
        projectRepository.setTechnologies(project.getId(), technologies);
    }


    public void search(Long chatId, String projectId, boolean isAdmin) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String languageCode = profileService.getLanguageCode(chatId);

        int id;
        try {
            id = Integer.parseInt(projectId);
        } catch (RuntimeException e) {
            sendMessage.setText(sentenceService.getSentence(SentenceKey.INCORRECT_ID, languageCode));
            sendingService.sendMessage(sendMessage);
            return;
        }

        Optional<ProjectEntity> optional = projectRepository.findByIdAndVisible(id, true);
        if (optional.isEmpty()) {
            sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_NOT_FOUND, languageCode));
            sendingService.sendMessage(sendMessage);
            return;
        }
        ProjectEntity project = optional.get();
        InlineKeyboardMarkup markup = null;
        List<ProfileEntity> partnerList = project.getPartnerList();
        ProfileEntity partner = partnerList.stream().filter(profile -> profile.getUserId().equals(chatId)).findAny().orElse(null);


        if (project.getProfile().getUserId().equals(chatId)) {
            markup = inlineButtonService.getConnectAndDeleteMarkup("/my/" + 0 + "/" + project.getId(), languageCode);
        } else if (partner != null) {
            markup = inlineButtonService.getConnectAndDeleteMarkup("/join/" + 0 + "/" + project.getId(), languageCode);
        } else if (isAdmin) {
            markup = inlineButtonService.getConnectAndDeleteMarkup("/admin/" + 0 + "/" + project.getId(), languageCode);
        }

        List<ProjectProfileEntity> projectProfile = projectProfileRepository.findByProjectIdAndProfileUserId(project.getId(), chatId);

        if (projectProfile == null) {
            markup = inlineButtonService.getJoinMarkup("/join/" + 0 + "/" + project.getId(), languageCode);
        }
        sendProjectInformation(optional.get(), chatId, languageCode, markup);

    }


    public void sendProjectInformationAfterJoin(ProjectEntity project, Long chatId, String languageCode, InlineKeyboardMarkup markup) {
        AttachEntity attach = attachRepository.findByProjectId(project.getId());

        if (attach == null || attach.getFileId() == null || attach.getFileId().isBlank()) {
            sendInfoAfterJoin(chatId, languageCode, project, markup);
            return;
        }

        switch (attach.getType()) {
            case PHOTO -> sendInfWithPhotoAfterJoin(chatId, languageCode, project, attach, markup);
            case VIDEO -> sendInfWithVideoAfterJoin(chatId, languageCode, project, attach, markup);
        }

    }
}
