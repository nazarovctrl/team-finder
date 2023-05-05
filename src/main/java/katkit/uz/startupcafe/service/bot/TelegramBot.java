package katkit.uz.startupcafe.service.bot;

import katkit.uz.startupcafe.config.BotConfig;
import katkit.uz.startupcafe.controller.UpdateController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final UpdateController updateController;

    public TelegramBot(BotConfig botConfig, UpdateController updateController) {
        this.botConfig = botConfig;
        this.updateController = updateController;

        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "get a welcome message"));
        commandList.add(new BotCommand("/help", "info how to use this bot"));
        commandList.add(new BotCommand("/language", "change language"));
        try {
            execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
            log.debug("Command list successfully initialized");
        } catch (TelegramApiException e) {
            log.warn("Command list initializing failed");
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "c")
    CommandLineRunner commandLineRunner(SendingService sendingService) {
        return args -> {
            sendingService.setTelegramBot(this);
        };
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug(update.toString());
        updateController.handle(update);
    }


}
