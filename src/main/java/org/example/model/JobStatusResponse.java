/* (C)2025 */
package org.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableJobStatusResponse.class)
public abstract class JobStatusResponse extends ReturnableModel {
    public abstract String getJobId();
    public abstract JobStatus getJobStatus();
    public abstract CaptureArtifacts getArtifacts();
    public abstract long getCreatedAt();
    public abstract long getUpdatedAt();
    public abstract String getJobMessage();
}
