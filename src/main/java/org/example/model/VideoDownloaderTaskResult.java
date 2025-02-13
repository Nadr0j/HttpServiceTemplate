/* (C)2025 */
package org.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableVideoDownloaderTaskResult.class)
public abstract class VideoDownloaderTaskResult {
    public abstract String getTempDirectoryName();
}
