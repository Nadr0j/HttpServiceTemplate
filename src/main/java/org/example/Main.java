/* (C)2024 */
package org.example;


import org.example.dagger.DaggerServiceServerComponent;
import org.example.dagger.ServiceServerComponent;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final ServiceServerComponent pagesServerComponent = DaggerServiceServerComponent.create();
        final ServiceServer server = pagesServerComponent.serviceServer();
        server.start();
    }
}
