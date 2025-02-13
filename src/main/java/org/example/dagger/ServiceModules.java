/* (C)2024 */
package org.example.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import org.example.orchestrator.VideoIngestionOrchestrator;

import javax.inject.Singleton;

@Module
public class ServiceModules {
    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    public VideoIngestionOrchestrator provideVideoIngestionOrchestrator() {
        return new VideoIngestionOrchestrator();
    }
}
