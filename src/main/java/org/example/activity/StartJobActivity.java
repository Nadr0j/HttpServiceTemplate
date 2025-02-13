/* (C)2025 */
package org.example.activity;

import lombok.extern.log4j.Log4j2;
import org.example.model.*;
import org.example.orchestrator.VideoIngestionOrchestrator;

import javax.inject.Inject;
import java.time.Instant;

@Log4j2
public class StartJobActivity {
    private final VideoIngestionOrchestrator videoIngestionOrchestrator;
    @Inject
    public StartJobActivity(final VideoIngestionOrchestrator videoIngestionOrchestrator) {
        this.videoIngestionOrchestrator = videoIngestionOrchestrator;
    }

    public JobStatusResponse startJob(final StartJobRequest requestObject) {
        log.info("Starting job for url " + requestObject.getWebpage());
        videoIngestionOrchestrator.startIngestion(requestObject.getWebpage());


        final CaptureArtifactPagesIdentifiers captureArtifact = ImmutableCaptureArtifactPagesIdentifiers.builder()
                .user("")
                .name("")
                .namespace("")
                .build();

        return ImmutableJobStatusResponse.builder()
                .jobId("1")
                .jobMessage("")
                .jobStatus(JobStatus.COMPLETED)
                .createdAt(Instant.now().getEpochSecond())
                .updatedAt(Instant.now().getEpochSecond())
                .artifacts(ImmutableCaptureArtifacts.builder()
                        .videoLocation(captureArtifact)
                        .videoPreviewGifLocation(captureArtifact)
                        .videoMetadata(ImmutableCaptureArtifactMetadata.builder()
                                .title("")
                                .sourceUrl("")
                                .addTags("")
                                .build())
                        .build())
                .build();
    }
}
