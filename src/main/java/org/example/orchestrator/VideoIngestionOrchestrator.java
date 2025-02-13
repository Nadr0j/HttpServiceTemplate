/* (C)2025 */
package org.example.orchestrator;

import lombok.extern.log4j.Log4j2;
import org.example.download.VideoDownloaderTask;
import org.example.model.VideoDownloaderTaskResult;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class VideoIngestionOrchestrator {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void startIngestion(final String pageUrl) {
        log.info("Starting ingestion.");
        CompletableFuture.supplyAsync(() -> {
            log.info("Starting task completable");
            final VideoDownloaderTask videoDownloaderTask = new VideoDownloaderTask(pageUrl);
            VideoDownloaderTaskResult result = videoDownloaderTask.call();
            log.info("Finished download task {}", result);
            return result;
        }, executor);
    }
}
