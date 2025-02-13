/* (C)2025 */
package org.example.download;

import lombok.extern.log4j.Log4j2;
import org.example.exception.FileDeletionException;
import org.example.exception.InternalServiceException;
import org.example.model.ImmutableVideoDownloaderTaskResult;
import org.example.model.VideoDownloaderTaskResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.Callable;

@Log4j2
public class VideoDownloaderTask implements Callable<VideoDownloaderTaskResult> {
    private final String pageUrl;
    private final String tempDirName;
    private final File postProcessingScript;
    private Process process;

    public VideoDownloaderTask(final String pageUrl) {
        this.pageUrl = pageUrl;
        this.tempDirName = UUID.randomUUID().toString();
        this.postProcessingScript = loadPostProcessScript("/process_download.sh", ".sh");
    }

    @Override
    public VideoDownloaderTaskResult call() {
        try {
            log.info("Starting VideoDownloaderTask call process.");
            final ProcessBuilder processBuilder = new ProcessBuilder(
                    "yt-dlp",
                    "--verbose",
                    "--no-warnings",
                    "--format", "best",
                    "--retries", "3",
                    "--fragment-retries", "3",
                    "--no-playlist",
                    "--write-thumbnail",
                    "--exec", postProcessingScript.getAbsolutePath() + " {}",
                    pageUrl,
                    "-o",
                    this.tempDirName + "/%(title)s.%(ext)s"

            );
            processBuilder.redirectErrorStream(true);
            log.info("Starting process");
            process = processBuilder.start();
            log.info("Process is alive [{}]", process.isAlive());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                log.info("process info " + process.info());
                while ((line = reader.readLine()) != null) {
                    log.info("[ytp-dl] {}", line);
                }
            } catch (final Exception e) {
                log.warn("Failed to log ytd-lp output.");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error(process.getErrorStream().toString());
                throw new RuntimeException("youtube-dl exited with code: " + exitCode);
            }

            return ImmutableVideoDownloaderTaskResult.builder()
                    .tempDirectoryName(tempDirName)
                    .build();
        } catch (final Exception e) {
            log.error("Encountered error", e);
            throw new InternalServiceException(e);
        }
    }

    public void cancelTask() {
        if (process != null && process.isAlive()) {
            log.info("Cancelling video downloader task");
            process.destroyForcibly();
            deleteTemporaryDirectoryAndAllContents();
        }
    }

    public void deleteTemporaryDirectoryAndAllContents() {
        final Path temporaryDirPath = Path.of(this.tempDirName);
        try {
            if (Files.exists(temporaryDirPath)) {
                Files.delete(temporaryDirPath);
            }
        } catch (final IOException e) {
            throw new FileDeletionException(e);
        } catch (final Exception e) {
            throw new InternalServiceException(e);
        }
    }

    private File loadPostProcessScript(final String resourcePath, final String tempFilePrefix) {
        log.info("Starting to load {}", resourcePath);
        try (final InputStream resourcesInputStream = VideoDownloaderTask.class.getResourceAsStream(resourcePath)) {
            if (resourcesInputStream == null) {
                log.error("Failed to load resource {}", resourcePath);
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            final File loadedScriptFile = File.createTempFile(tempFilePrefix, ".sh");
            Files.copy(resourcesInputStream, loadedScriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (!loadedScriptFile.setExecutable(true)) {
                log.error("Failed to mark {} as executable", loadedScriptFile.getAbsolutePath());
                throw new IOException("Cannot mark script as executable: " + loadedScriptFile.getAbsolutePath());
            }
            loadedScriptFile.deleteOnExit();
            log.info("Returning loaded post processing script");
            return loadedScriptFile;
        } catch (final Exception e) {
            log.error(e);
            throw new RuntimeException("Failed to load post processing script", e);
        }
    }
}
