/* (C)2025 */
package org.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCaptureArtifactPagesIdentifiers.class)
public abstract class CaptureArtifactPagesIdentifiers {
    abstract public String getUser();
    abstract public String getNamespace();
    abstract public String getName();
}
