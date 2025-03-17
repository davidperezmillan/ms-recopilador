package com.davidperezmillan.recopilador.infrastructure.scheduler;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SchedulerTasks {


    private final TorrentUseCase torrentUseCase;

    public SchedulerTasks(TorrentUseCase torrentUseCase) {
        this.torrentUseCase = torrentUseCase;
    }

    @Scheduled(fixedRate = 60000) // 60000 milliseconds = 1 minute
    public void runTask() {
        torrentUseCase.addTorrents();
    }
}