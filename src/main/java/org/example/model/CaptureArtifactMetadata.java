/* (C)2025 */
package org.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableCaptureArtifactMetadata.class)
public abstract class CaptureArtifactMetadata {
    public abstract String getTitle();
    @Value.Default
    public List<String> getTags() {
        return List.of();
    }
    public abstract String getSourceUrl();
}
