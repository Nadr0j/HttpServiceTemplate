/* (C)2025 */
package org.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStartJobRequest.class)
public abstract class StartJobRequest {
    abstract public String getWebpage();

    @Value.Check
    protected void validate() {
        if (getWebpage() == null || !getWebpage().matches("^(https?://).+")) {
            throw new IllegalArgumentException("Invalid webpage URL: " + getWebpage());
        }
    }
}
