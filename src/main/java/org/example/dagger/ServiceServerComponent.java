/* (C)2024 */
package org.example.dagger;

import dagger.Component;
import org.example.ServiceServer;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ServiceModules.class})
public interface ServiceServerComponent {
    ServiceServer serviceServer();
}
