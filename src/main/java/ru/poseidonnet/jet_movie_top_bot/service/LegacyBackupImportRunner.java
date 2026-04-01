package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegacyBackupImportRunner implements ApplicationRunner {

    private final PollsContainerService pollsContainerService;

    @Override
    public void run(ApplicationArguments args) {
        pollsContainerService.importLegacyBackupIfNeeded();
    }
}