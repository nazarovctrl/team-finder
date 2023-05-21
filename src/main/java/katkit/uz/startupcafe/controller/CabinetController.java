package katkit.uz.startupcafe.controller;

import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.Step;
import katkit.uz.startupcafe.service.CabinetService;
import katkit.uz.startupcafe.service.ProfileService;
import katkit.uz.startupcafe.service.SentenceService;
import org.springframework.stereotype.Component;


@Component
public class CabinetController {
    private final EditCabinetController editCabinetController;
    private final CabinetService localCabinetService;
    private final ProfileService profileService;
    private final SentenceService sentenceService;

    public CabinetController(EditCabinetController editCabinetController, CabinetService localCabinetService, ProfileService profileService, SentenceService sentenceService) {
        this.editCabinetController = editCabinetController;
        this.localCabinetService = localCabinetService;
        this.profileService = profileService;
        this.sentenceService = sentenceService;
    }

    public void handle(Long chatId, String text) {
        ButtonKey buttonKey = sentenceService.getButtonKey(text);

        if (buttonKey != null) {
            switch (buttonKey) {
                case PROFILE_INFORMATION -> {
                    localCabinetService.sendInformation(chatId);
                    return;
                }
                case PROFILE_EDIT -> {
                    localCabinetService.toEditCabinet(chatId);
                    return;
                }
            }
        }

        Step step = profileService.getStep(chatId);

        if (step.name().startsWith(Step.PROFILE_EDIT.name())) {
            editCabinetController.handle(chatId, text);
        }


    }


}
