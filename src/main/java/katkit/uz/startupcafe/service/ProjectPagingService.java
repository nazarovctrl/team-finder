package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.entity.AttachEntity;
import katkit.uz.startupcafe.entity.ProfileEntity;
import katkit.uz.startupcafe.entity.ProjectEntity;
import katkit.uz.startupcafe.entity.ProjectProfileEntity;
import katkit.uz.startupcafe.enums.AttachType;
import katkit.uz.startupcafe.enums.ProjectStatus;
import katkit.uz.startupcafe.enums.SentenceKey;
import katkit.uz.startupcafe.repository.AttachRepository;
import katkit.uz.startupcafe.repository.ProjectProfileRepository;
import katkit.uz.startupcafe.repository.ProjectRepository;
import katkit.uz.startupcafe.service.bot.SendingService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectPagingService {
    private final SentenceService sentenceService;
    private final ProjectProfileRepository projectProfileRepository;
    private final AttachRepository attachRepository;
    private final ProjectRepository projectRepository;
    private final ProfileService profileService;
    private final ProjectService projectService;
    private final SendingService sendingService;
    private final InlineButtonService inlineButtonService;

    public ProjectPagingService(SentenceService sentenceService, ProjectProfileRepository projectProfileRepository, AttachRepository attachRepository, ProjectRepository projectRepository, ProfileService profileService, ProjectService projectService, SendingService sendingService, InlineButtonService inlineButtonService) {
        this.sentenceService = sentenceService;
        this.projectProfileRepository = projectProfileRepository;
        this.attachRepository = attachRepository;
        this.projectRepository = projectRepository;
        this.profileService = profileService;
        this.projectService = projectService;
        this.sendingService = sendingService;

        this.inlineButtonService = inlineButtonService;
    }


    private Page<ProjectEntity> getAndCheckPage(Integer messageId, Long chatId, Integer currentPage) {

        Page<ProjectEntity> page = getMyPage(chatId, currentPage);

        if (page.hasContent()) {
            return page;
        }

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        sendingService.sendMessage(deleteMessage);
        return null;
    }

    private Page<ProjectEntity> getAndCheckPageJoin(Integer messageId, Long chatId, Integer currentPage) {

        Page<ProjectEntity> page = getJoinedPage(chatId, currentPage);

        if (page.hasContent()) {
            return page;
        }

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        sendingService.sendMessage(deleteMessage);
        return null;
    }

    public void getMyProjectPage(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        Page<ProjectEntity> page = getMyPage(chatId, 0);

        if (!page.hasContent()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sentenceService.getSentence(SentenceKey.MY_PROJECT_EMPTY, languageCode));
            sendingService.sendMessage(sendMessage);
            return;
        }
        ProjectEntity project = page.getContent().get(0);
        InlineKeyboardMarkup markup;
        if (page.getTotalPages() == 1) {
            markup = inlineButtonService.getConnectDeleteAndCurrentMarkup("/my/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
        } else {
            markup = inlineButtonService.getConnectDeleteAndNextMarkup("/my/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
        }

        projectService.sendProjectInformation(project, chatId, languageCode, markup);

    }


    public Page<ProjectEntity> getMyPage(Long userId, int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedDate");

        Pageable pageable = PageRequest.of(page, 1, sort);

        Page<ProjectEntity> all = projectRepository.findByProfileUserIdAndStatusAndVisible(userId, ProjectStatus.PUBLISHED, pageable, true);
        List<ProjectEntity> content = all.getContent();
        return new PageImpl<>(content, pageable, all.getTotalElements());
    }

    public Page<ProjectEntity> getPage(Long userId, int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedDate");
        ProfileEntity profile = profileService.getByUserId(userId);
        Pageable pageable = PageRequest.of(page, 1, sort);

        Page<ProjectEntity> all = projectRepository.findByProfileUserIdAndStatusAndVisible(userId, profile, ProjectStatus.PUBLISHED, true, pageable);
        List<ProjectEntity> content = all.getContent();

        return new PageImpl<>(content, pageable, all.getTotalElements());

    }

    public Page<ProjectEntity> getAdminPage(int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedDate");
        Pageable pageable = PageRequest.of(page, 1, sort);

        Page<ProjectEntity> all = projectRepository.findByStatusAndVisible(ProjectStatus.PUBLISHED, true, pageable);
        List<ProjectEntity> content = all.getContent();

        return new PageImpl<>(content, pageable, all.getTotalElements());

    }

    public void getProjectPage(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        Page<ProjectEntity> page = getPage(chatId, 0);

        if (!page.hasContent()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_EMPTY, languageCode));
            sendingService.sendMessage(sendMessage);
            return;
        }
        ProjectEntity project = page.getContent().get(0);
        List<ProjectProfileEntity> projectProfile = projectProfileRepository.findByProjectIdAndProfileUserId(project.getId(), chatId);

        InlineKeyboardMarkup markup;
        if (projectProfile == null || projectProfile.isEmpty()) {
            if (page.getTotalPages() == 1) {
                markup = inlineButtonService.getJoinAndCurrentMarkup("/people/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
            } else {
                markup = inlineButtonService.getJoinAndNextMarkup("/people/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
            }
        } else {
            if (page.getTotalPages() == 1) {
                markup = inlineButtonService.getCurrentMarkup(1, page.getTotalPages());
            } else {
                markup = inlineButtonService.getNextMarkup("/people/" + 0 + "/" + project.getId(), 1, page.getTotalPages());
            }
        }

        projectService.sendProjectInformation(project, chatId, languageCode, markup);
    }

    public void getAdminProjectPage(Long chatId) {
        String languageCode = profileService.getLanguageCode(chatId);
        Page<ProjectEntity> page = getAdminPage(0);

        if (!page.hasContent()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sentenceService.getSentence(SentenceKey.PROJECT_EMPTY, languageCode));
            sendingService.sendMessage(sendMessage);
            return;
        }
        ProjectEntity project = page.getContent().get(0);

        InlineKeyboardMarkup markup;

        if (page.getTotalPages() == 1) {
            markup = inlineButtonService.getDeleteAndCurrentMarkup("/admin/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
        } else {
            markup = inlineButtonService.getDeleteAndNextMarkup("/admin/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
        }


        projectService.sendProjectInformation(project, chatId, languageCode, markup);
    }

    private void editMyPageMessage(Integer pId, Page<ProjectEntity> page, Integer prevPage, Message message) {
        ProjectEntity project = page.getContent().get(0);
        String languageCode = project.getProfile().getLanguageCode();
        AttachEntity attach = attachRepository.findByProjectId(project.getId());
        AttachEntity prevAttach = attachRepository.findByProjectId(pId);
        InlineKeyboardMarkup editMarkup = getMyEditMarkup(project.getId(), languageCode, page.getTotalPages(), prevPage);


        sendEditPage(project, prevAttach, attach, message, languageCode, editMarkup);
    }

    private void editJoinPageMessage(Integer pId, Page<ProjectEntity> page, Integer prevPage, Message message) {
        ProjectEntity project = page.getContent().get(0);
        String languageCode = profileService.getLanguageCode(message.getChatId());
        AttachEntity attach = attachRepository.findByProjectId(project.getId());
        AttachEntity prevAttach = attachRepository.findByProjectId(pId);
        InlineKeyboardMarkup editMarkup = getJoinEditMarkup(project.getId(), languageCode, page.getTotalPages(), prevPage);

        sendEditPage(project, prevAttach, attach, message, languageCode, editMarkup);
    }


    private void sendEditPage(ProjectEntity project, AttachEntity prevAttach, AttachEntity attach, Message message, String languageCode, InlineKeyboardMarkup editMarkup) {
        if (prevAttach.getType() == null) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setMessageId(message.getMessageId());
            deleteMessage.setChatId(message.getChatId());
            sendingService.sendMessage(deleteMessage);
            projectService.sendProjectInformation(project, message.getChatId(), languageCode, editMarkup);
            return;
        }

        if (attach.getType() != null) {
            EditMessageMedia editMessageMedia = new EditMessageMedia();
            editMessageMedia.setChatId(message.getChatId());
            editMessageMedia.setMessageId(message.getMessageId());


            InputMedia inputMedia = new InputMediaVideo();

            if (attach.getType().equals(AttachType.PHOTO)) {
                inputMedia = new InputMediaPhoto();
            }
            inputMedia.setMedia(attach.getFileId());
            editMessageMedia.setMedia(inputMedia);
            sendingService.sendMessage(editMessageMedia);

            EditMessageCaption caption = new EditMessageCaption();
            caption.setParseMode(ParseMode.MARKDOWN);
            caption.setChatId(message.getChatId());
            caption.setCaption(projectService.getInformation(languageCode, project));
            caption.setMessageId(message.getMessageId());
            caption.setReplyMarkup(editMarkup);
            sendingService.sendMessage(caption);
            return;
        }

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(message.getChatId());
        sendingService.sendMessage(deleteMessage);
        projectService.sendProjectInformation(project, message.getChatId(), languageCode, editMarkup);

    }


    private InlineKeyboardMarkup getMyEditMarkup(Integer projectId, String languageCode, Integer totalPages, Integer currentPage) {

        InlineKeyboardMarkup markup = null;
        if (totalPages - 1 == currentPage && currentPage == 0) {
            markup = inlineButtonService.getConnectDeleteAndCurrentMarkup("/my/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 > currentPage && currentPage == 0) {
            markup = inlineButtonService.getConnectDeleteAndNextMarkup("/my/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 > currentPage && currentPage > 0) {
            markup = inlineButtonService.getConnectDeleteBackAndNextMarkup("/my/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 == currentPage && currentPage > 0) {
            markup = inlineButtonService.getConnectDeleteAndBackMarkup("/my/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        }
        return markup;

    }

    private InlineKeyboardMarkup getJoinEditMarkup(Integer projectId, String languageCode, Integer totalPages, Integer currentPage) {

        InlineKeyboardMarkup markup = null;
        if (totalPages - 1 == currentPage && currentPage == 0) {
            markup = inlineButtonService.getConnectDeleteAndCurrentMarkup("/join/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 > currentPage && currentPage == 0) {
            markup = inlineButtonService.getConnectDeleteAndNextMarkup("/join/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 > currentPage && currentPage > 0) {
            markup = inlineButtonService.getConnectDeleteBackAndNextMarkup("/join/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 == currentPage && currentPage > 0) {
            markup = inlineButtonService.getConnectDeleteAndBackMarkup("/join/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        }
        return markup;

    }

    private InlineKeyboardMarkup getEditMarkup(Integer projectId, Long userId, String languageCode, Integer totalPages, Integer currentPage) {
        List<ProjectProfileEntity> projectProfile = projectProfileRepository.findByProjectIdAndProfileUserId(projectId, userId);
        InlineKeyboardMarkup markup = null;

        if (projectProfile == null || projectProfile.isEmpty()) {
            if (totalPages - 1 == currentPage && currentPage == 0) {
                markup = inlineButtonService.getJoinAndCurrentMarkup("/people/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
            } else if (totalPages - 1 > currentPage && currentPage == 0) {
                markup = inlineButtonService.getJoinAndNextMarkup("/people/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
            } else if (totalPages - 1 > currentPage && currentPage > 0) {
                markup = inlineButtonService.getJoinBackAndNextMarkup("/people/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
            } else if (totalPages - 1 == currentPage && currentPage > 0) {
                markup = inlineButtonService.getJoinAndBackMarkup("/people/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
            }
        } else {
            if (totalPages - 1 == currentPage && currentPage == 0) {
                markup = inlineButtonService.getCurrentMarkup(1 + currentPage, totalPages);
            } else if (totalPages - 1 > currentPage && currentPage == 0) {
                markup = inlineButtonService.getNextMarkup("/people/" + currentPage + "/" + projectId, 1 + currentPage, totalPages);
            } else if (totalPages - 1 > currentPage && currentPage > 0) {
                markup = inlineButtonService.getBackAndNextMarkup("/people/" + currentPage + "/" + projectId, 1 + currentPage, totalPages);
            } else if (totalPages - 1 == currentPage && currentPage > 0) {
                markup = inlineButtonService.getBackMarkup("/people/" + currentPage + "/" + projectId, 1 + currentPage, totalPages);
            }
        }
        return markup;
    }

    private InlineKeyboardMarkup getAdminEditMarkup(Integer projectId, String languageCode, Integer totalPages, Integer currentPage) {
        InlineKeyboardMarkup markup = null;


        if (totalPages - 1 == currentPage && currentPage == 0) {
            markup = inlineButtonService.getDeleteAndCurrentMarkup("/admin/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 > currentPage && currentPage == 0) {
            markup = inlineButtonService.getDeleteAndNextMarkup("/admin/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 > currentPage && currentPage > 0) {
            markup = inlineButtonService.getDeleteBackAndNextMarkup("/admin/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        } else if (totalPages - 1 == currentPage && currentPage > 0) {
            markup = inlineButtonService.getDeleteAndBackMarkup("/admin/" + currentPage + "/" + projectId, 1 + currentPage, totalPages, languageCode);
        }

        return markup;
    }


    private void editPageMessage(Integer pId, Page<ProjectEntity> page, Integer prevPage, Message message) {
        ProjectEntity project = page.getContent().get(0);
        String languageCode = project.getProfile().getLanguageCode();
        AttachEntity attach = attachRepository.findByProjectId(project.getId());
        AttachEntity prevAttach = attachRepository.findByProjectId(pId);
        InlineKeyboardMarkup editMarkup = getEditMarkup(project.getId(), message.getChatId(), languageCode, page.getTotalPages(), prevPage);
        sendEditPage(project, prevAttach, attach, message, languageCode, editMarkup);
    }

    private void editAdminPageMessage(Integer pId, Page<ProjectEntity> page, Integer prevPage, Message message) {
        ProjectEntity project = page.getContent().get(0);
        String languageCode = project.getProfile().getLanguageCode();
        AttachEntity attach = attachRepository.findByProjectId(project.getId());
        AttachEntity prevAttach = attachRepository.findByProjectId(pId);
        InlineKeyboardMarkup editMarkup = getAdminEditMarkup(project.getId(), languageCode, page.getTotalPages(), prevPage);
        sendEditPage(project, prevAttach, attach, message, languageCode, editMarkup);
    }

    public void sendDeleteConfirmAndCancel(String data, CallbackQuery callbackQuery) {

        String[] split = data.split("/");
        int pId = Integer.parseInt(split[3]);
        int currantPage = Integer.parseInt(split[2]);
        Message message = callbackQuery.getMessage();
        String languageCode = profileService.getLanguageCode(message.getChatId());

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setChatId(message.getChatId());

        if (split[1].equals("my")) {
            editMessageReplyMarkup.setReplyMarkup(inlineButtonService.getConfirmAndCancelMarkup("/my/" + currantPage + "/" + pId, languageCode));
        } else if (split[1].equals("join")) {
            editMessageReplyMarkup.setReplyMarkup(inlineButtonService.getConfirmAndCancelMarkup("/join/" + currantPage + "/" + pId, languageCode));
        } else if (split[1].equals("admin")) {
            editMessageReplyMarkup.setReplyMarkup(inlineButtonService.getConfirmAndCancelMarkup("/admin/" + currantPage + "/" + pId, languageCode));

        }
        sendingService.sendMessage(editMessageReplyMarkup);
    }

    public void deleteConfirmProject(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        if (currentPage > 0) {
            currentPage = currentPage - 1;
        }

        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page;
        if (split[1].equals("my")) {
            projectRepository.changeVisible(pId, false);
            page = getAndCheckPage(message.getMessageId(), message.getChatId(), currentPage);
            if (page == null) {
                return;
            }
            editMyPageMessage(pId, page, currentPage, message);
        } else if (split[1].equals("admin")) {
            projectRepository.changeVisible(pId, false);
            Optional<ProjectEntity> optional = projectRepository.findById(pId);
            if (optional.isEmpty()) {
                return;
            }
            ProjectEntity project = optional.get();
            page = getAdminPage(currentPage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setChatId(project.getProfile().getUserId());
            sendMessage.setText(String.format(sentenceService.getSentence(SentenceKey.PROJECT_DELETED_BY_ADMIN, project.getProfile().getLanguageCode()), project.getId()));
            sendingService.sendMessage(sendMessage);

            if (page == null || !page.hasContent()) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(message.getChatId());
                deleteMessage.setMessageId(message.getMessageId());
                sendingService.sendMessage(deleteMessage);
                return;
            }
            editAdminPageMessage(pId, page, currentPage, message);


        } else {
            Optional<ProjectEntity> optional = projectRepository.findById(pId);
            if (optional.isEmpty()) {
                return;
            }
            ProjectEntity project = optional.get();
            List<ProfileEntity> partnerList = project.getPartnerList();

            partnerList.removeIf(profile -> profile.getUserId().equals(message.getChatId()));

            projectRepository.save(project);

            page = getAndCheckPageJoin(message.getMessageId(), message.getChatId(), currentPage);
            if (page == null) {
                return;
            }
            editJoinPageMessage(pId, page, currentPage, message);
        }


    }

    public void deleteCancelProject(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        int currentPage = Integer.parseInt(split[2]);
        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page;
        InlineKeyboardMarkup editMarkup;
        if (split[1].equals("my")) {
            page = getAndCheckPage(message.getMessageId(), message.getChatId(), currentPage);

            if (page == null) {
                return;
            }

            ProjectEntity project = page.getContent().get(0);
            String languageCode = project.getProfile().getLanguageCode();
            editMarkup = getMyEditMarkup(project.getId(), languageCode, page.getTotalPages(), currentPage);


        } else if (split[1].equals("admin")) {
            page = getAdminPage(currentPage);
            if (page == null) {
                return;
            }
            ProjectEntity project = page.getContent().get(0);
            String languageCode = profileService.getLanguageCode(message.getChatId());
            editMarkup = getAdminEditMarkup(project.getId(), languageCode, page.getTotalPages(), currentPage);
        } else {
            page = getAndCheckPageJoin(message.getMessageId(), message.getChatId(), currentPage);

            if (page == null) {
                return;
            }
            ProjectEntity project = page.getContent().get(0);
            String languageCode = profileService.getLanguageCode(message.getChatId());
            editMarkup = getJoinEditMarkup(project.getId(), languageCode, page.getTotalPages(), currentPage);
        }


        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setChatId(message.getChatId());
        editMessageReplyMarkup.setReplyMarkup(editMarkup);
        sendingService.sendMessage(editMessageReplyMarkup);

    }

    public void toMyPrevPage(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int prevPage = currentPage - 1;
        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page = getAndCheckPage(message.getMessageId(), message.getChatId(), prevPage);

        if (page == null) {
            return;
        }
        editMyPageMessage(pId, page, prevPage, message);
    }

    public void toAdminPrevPage(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int prevPage = currentPage - 1;
        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page = getAdminPage(prevPage);

        if (page == null) {
            return;
        }
        editAdminPageMessage(pId, page, prevPage, message);
    }

    public void toPrevPage(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int prevPage = currentPage - 1;
        Message message = callbackQuery.getMessage();


        Page<ProjectEntity> page = getPage(message.getChatId(), currentPage);

        if (!checkPage(page, message.getChatId(), message.getMessageId())) {
            return;
        }

        editPageMessage(pId, page, prevPage, message);

    }


    public void toMyNextPage(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int nextPage = currentPage + 1;
        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page = getAndCheckPage(message.getMessageId(), message.getChatId(), nextPage);

        if (page == null) {
            return;
        }
        editMyPageMessage(pId, page, nextPage, message);
    }

    public void toAdminNextPage(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int nextPage = currentPage + 1;
        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page = getAdminPage(nextPage);

        if (page == null) {
            return;
        }
        editAdminPageMessage(pId, page, nextPage, message);
    }

    public void toNextPage(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int nextPage = currentPage + 1;
        Message message = callbackQuery.getMessage();


        Page<ProjectEntity> page = getPage(message.getChatId(), currentPage);

        if (!checkPage(page, message.getChatId(), message.getMessageId())) {
            return;
        }


        editPageMessage(pId, page, nextPage, message);

    }

    private boolean checkPage(Page<ProjectEntity> page, Long chatId, Integer messageId) {
        if (!page.hasContent()) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            sendingService.sendMessage(deleteMessage);
            return false;
        }
        return true;
    }


    public void joinProject(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        Message message = callbackQuery.getMessage();

        Optional<ProjectEntity> optional = projectRepository.findById(pId);

        ProjectEntity project = optional.get();
        if (optional.isEmpty() || !project.getVisible() || project.getProfile().getUserId().equals(message.getChatId())) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(message.getChatId());
            deleteMessage.setMessageId(message.getMessageId());
            sendingService.sendMessage(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyToMessageId(message.getMessageId());
            sendMessage.setText(sentenceService.getSentence(SentenceKey.ITS_YOURS_PROJECT, project.getProfile().getLanguageCode()));
            sendingService.sendMessage(sendMessage);
            return;
        }

        ProfileEntity profile = profileService.getByUserId(message.getChatId());
        if (!profile.getIsRegistered()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(sentenceService.getSentence(SentenceKey.REGISTER, profile.getLanguageCode()));
            sendMessage.setChatId(message.getChatId());
            sendingService.sendMessage(sendMessage);
            return;
        }
        ProjectEntity projectEntity = projectRepository.checkPartnerIsExists(profile, project.getId());

        if (projectEntity != null) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(message.getChatId());
            deleteMessage.setMessageId(message.getMessageId());
            sendingService.sendMessage(deleteMessage);
            return;
        }
        ProfileEntity creator = project.getProfile();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(creator.getUserId());
        String information = projectService.getInformation(creator.getLanguageCode(), project);
        sendMessage.setText(profileService.getInformation(profile) + "/" + information);
        sendMessage.setReplyMarkup(inlineButtonService.getAddAndCancelMarkup("/" + profile.getUserId() + "/" + project.getId(), creator.getLanguageCode()));
        sendingService.sendMessage(sendMessage);


        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        sendingService.sendMessage(deleteMessage);


        ProjectProfileEntity projectProfile = new ProjectProfileEntity();
        projectProfile.setProjectId(project.getId());
        projectProfile.setProfileId(profile.getId());
        projectProfileRepository.save(projectProfile);

        getProjectPage(message.getChatId());

        sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(sentenceService.getSentence(SentenceKey.JOIN_REQUEST, profile.getLanguageCode()));
        sendingService.sendMessage(sendMessage);

        //
//        List<ProfileEntity> partnerList = project.getPartnerList();
//        partnerList.add(profile);
//        project.setPartnerList(partnerList);
//        projectRepository.save(project);
//
//        DeleteMessage deleteMessage = new DeleteMessage();
//        deleteMessage.setChatId(message.getChatId());
//        deleteMessage.setMessageId(message.getMessageId());
//        sendingService.sendMessage(deleteMessage);
//
//        getProjectPage(message.getChatId());
    }


    public void getJoinedProjectPage(Long chatId) {

        Page<ProjectEntity> page = getJoinedPage(chatId, 0);
        String languageCode = profileService.getLanguageCode(chatId);

        if (!page.hasContent()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sentenceService.getSentence(SentenceKey.MY_PROJECT_EMPTY, languageCode));
            sendingService.sendMessage(sendMessage);
            return;
        }
        ProjectEntity project = page.getContent().get(0);
        InlineKeyboardMarkup markup;
        if (page.getTotalPages() == 1) {
            markup = inlineButtonService.getConnectDeleteAndCurrentMarkup("/join/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
        } else {
            markup = inlineButtonService.getConnectDeleteAndNextMarkup("/join/" + 0 + "/" + project.getId(), 1, page.getTotalPages(), languageCode);
        }

        projectService.sendProjectInformationAfterJoin(project, chatId, languageCode, markup);
    }

    private Page<ProjectEntity> getJoinedPage(Long chatId, int currentPage) {
        ProfileEntity profile = profileService.getByUserId(chatId);
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedDate");

        Pageable pageable = PageRequest.of(currentPage, 1, sort);

        Page<ProjectEntity> all = projectRepository.getJoinedProjectPage(profile, ProjectStatus.PUBLISHED, true, pageable);
        List<ProjectEntity> content = all.getContent();
        return new PageImpl<>(content, pageable, all.getTotalElements());
    }

    public void toJoinNextPage(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int nextPage = currentPage + 1;
        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page = getAndCheckPageJoin(message.getMessageId(), message.getChatId(), nextPage);

        if (page == null) {
            return;
        }
        editJoinPageMessage(pId, page, nextPage, message);
    }

    public void toJoinPrevPage(String data, CallbackQuery callbackQuery) {

        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);

        int prevPage = currentPage - 1;
        Message message = callbackQuery.getMessage();

        Page<ProjectEntity> page = getAndCheckPageJoin(message.getMessageId(), message.getChatId(), prevPage);

        if (page == null) {
            return;
        }
        editJoinPageMessage(pId, page, prevPage, message);
    }

    public void connect(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Integer pId = Integer.valueOf(split[3]);
        int currentPage = Integer.parseInt(split[2]);
        Message message = callbackQuery.getMessage();

        String languageCode = profileService.getLanguageCode(message.getChatId());

        List<ProfileEntity> profileList = new ArrayList<>();
        profileList.add(projectRepository.getProfileByProjectId(pId));
        profileList.addAll(projectRepository.getPartnerList(pId));

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setReplyMarkup(inlineButtonService.getProfileAndBackMarkup(profileList, "/" + split[1] + "/" + currentPage, languageCode));
        editMessageReplyMarkup.setChatId(message.getChatId());
        editMessageReplyMarkup.setMessageId(message.getMessageId());

        sendingService.sendMessage(editMessageReplyMarkup);
    }

    public void backForConnect(String data, CallbackQuery callbackQuery) {

        String[] split = data.split("/");
        int currentPage = Integer.parseInt(split[2]);
        Message message = callbackQuery.getMessage();

        InlineKeyboardMarkup editMarkup;

        if (split[1].equals("join")) {
            Page<ProjectEntity> page = getAndCheckPageJoin(message.getMessageId(), message.getChatId(), currentPage);

            if (page == null) {
                return;
            }
            ProjectEntity project = page.getContent().get(0);
            String languageCode = profileService.getLanguageCode(message.getChatId());
            editMarkup = getJoinEditMarkup(project.getId(), languageCode, page.getTotalPages(), currentPage);
        } else {
            Page<ProjectEntity> page = getAndCheckPage(message.getMessageId(), message.getChatId(), currentPage);
            if (page == null) {
                return;
            }
            ProjectEntity project = page.getContent().get(0);
            String languageCode = project.getProfile().getLanguageCode();
            editMarkup = getMyEditMarkup(project.getId(), languageCode, page.getTotalPages(), currentPage);
        }
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setChatId(message.getChatId());
        editMessageReplyMarkup.setReplyMarkup(editMarkup);
        sendingService.sendMessage(editMessageReplyMarkup);


    }

    public void addToProject(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Long userId = Long.valueOf(split[1]);
        Integer pId = Integer.valueOf(split[2]);
        Message message = callbackQuery.getMessage();

        Optional<ProjectEntity> optional = projectRepository.findById(pId);
        if (optional.isEmpty()) {
            return;
        }
        ProjectEntity project = optional.get();

        ProfileEntity profile = profileService.getByUserId(userId);

        projectProfileRepository.deleteByProjectIdAndProfileId(project.getId(), profile.getId());

        List<ProfileEntity> partnerList = project.getPartnerList();
        partnerList.add(profile);
        project.setPartnerList(partnerList);
        projectRepository.save(project);


        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(sentenceService.getSentence(SentenceKey.JOIN_REQUEST_ACCEPTED, profile.getLanguageCode()));
        sendMessage.setChatId(profile.getUserId());
        sendingService.sendMessage(sendMessage);


        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        sendingService.sendMessage(deleteMessage);

    }

    public void rejectJoinProject(String data, CallbackQuery callbackQuery) {
        String[] split = data.split("/");
        Long userId = Long.valueOf(split[1]);
        Integer pId = Integer.valueOf(split[2]);
        Message message = callbackQuery.getMessage();

        Optional<ProjectEntity> optional = projectRepository.findById(pId);
        if (optional.isEmpty()) {
            return;
        }
        ProjectEntity project = optional.get();

        ProfileEntity profile = profileService.getByUserId(userId);

        projectProfileRepository.deleteByProjectIdAndProfileId(project.getId(), profile.getId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(sentenceService.getSentence(SentenceKey.JOIN_REQUEST_REJECTED, profile.getLanguageCode()));
        sendMessage.setChatId(profile.getUserId());
        sendingService.sendMessage(sendMessage);


        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        sendingService.sendMessage(deleteMessage);

    }
}

