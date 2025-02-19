package com.davidperezmillan.recopilador.infrastructure.scheduler;

import com.davidperezmillan.recopilador.apllication.usecases.DownloadUseCase;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SchedulerTasks {

    private final DownloadUseCase downloadUseCase;

    public SchedulerTasks(DownloadUseCase downloadUseCase) {
        this.downloadUseCase = downloadUseCase;
    }

    @Scheduled(fixedRate = 60000) // 60000 milliseconds = 1 minute
    public void runTask() {
        try {
            downloadUseCase.downloadAllTorrent();
        } catch (TransmissionException e) {
            log.info("Error downloading torrents: {}",e.getMessage());
        }

    }
}