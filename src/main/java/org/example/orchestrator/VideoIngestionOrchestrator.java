/* (C)2025 */
package org.example.orchestrator;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.download.VideoDownloaderTask;
import org.example.model.VideoDownloaderTaskResult;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class VideoIngestionOrchestrator {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void startIngestion(final String pageUrl) {
        log.info("Starting ingestion.");
        // Create mongodb record for the job and set the status to IN_PROGRESS
        final Map<String, String> threadContext = ThreadContext.getContext();

        CompletableFuture.supplyAsync(() -> {
            log.info("Starting task completable");
            ThreadContext.putAll(threadContext);
            final VideoDownloaderTask videoDownloaderTask = new VideoDownloaderTask(pageUrl);
            VideoDownloaderTaskResult result = videoDownloaderTask.call();
            log.info("Finished download task {}", result);
            return result;
        }, executor);

        log.info("Async video downloader task supplied to async thread.");
    }
}
