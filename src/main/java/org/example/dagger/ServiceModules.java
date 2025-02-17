/* (C)2024 */
package org.example.dagger;

import com.github.nadr0j.pagesclient.ApiClient;
import com.github.nadr0j.pagesclient.Configuration;
import com.github.nadr0j.pagesclient.api.DefaultApi;
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

    @Provides
    @Singleton
    public DefaultApi providePagesClient() {
        final ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("10.0.0.102");
        return new DefaultApi(defaultClient);
    }
}
