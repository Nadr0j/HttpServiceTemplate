/* (C)2025 */
package org.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCaptureArtifacts.class)
public abstract class CaptureArtifacts {
    public abstract CaptureArtifactPagesIdentifiers getVideoPreviewGifLocation();
    public abstract CaptureArtifactPagesIdentifiers getVideoLocation();
    public abstract CaptureArtifactMetadata getVideoMetadata();
}
