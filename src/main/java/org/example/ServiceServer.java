/* (C)2024 */
package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.example.activity.SomeActivity;
import org.example.model.Error;
import org.example.model.ImmutableError;
import org.example.model.RequestObject;
import org.example.model.ResponseObject;
import org.example.model.ReturnableModel;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executors;


@Log4j2
public class ServiceServer {
    private final ObjectMapper objectMapper;
    private final SomeActivity someActivity;

    private static final int PORT = 10_000;
    private static final int BACKLOG = 2;

    @Inject
    public ServiceServer(final SomeActivity someActivity, final ObjectMapper objectMapper) {
        this.someActivity = someActivity;
        this.objectMapper = objectMapper;
    }

    public void start() throws IOException {
        log.info("Starting server");
        final HttpServer server = HttpServer.create(new InetSocketAddress(PORT), BACKLOG);
        server.createContext("/some/api", this::handleSomeApi);
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        log.info("Pages server has started");
    }

    private void handleSomeApi(final HttpExchange httpExchange) {
        try {
            setRequestIdInThreadContext();
            if ("POST".equals(httpExchange.getRequestMethod())) {
                final InputStream inputStream = httpExchange.getRequestBody();
                final RequestObject request = objectMapper.readValue(inputStream, RequestObject.class);
                log.info("Received request with request object [{}]", request);
                final ResponseObject response = someActivity.activityEntrypoint(request);
                sendResponse(httpExchange, HttpURLConnection.HTTP_OK, response);
                log.info("Finished activity with success.");
            } else {
                log.info("WritePage request sent but is not POST. Returning 404.");
                sendError(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Request must be POST");
            }
        } catch (final Exception e) {
            log.error("Failed with exception {}", e.toString());
            sendError(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Encountered error. " +
                    "Please try your request again.");
        } finally {
            clearRequestId();
        }
    }

    private void sendError(final HttpExchange httpExchange, final int statusCode, final String errorMessage) {
        final Error error = ImmutableError.builder().error(errorMessage).build();
        sendResponse(httpExchange, statusCode, error);
    }

    private void sendResponse(final HttpExchange httpExchange, final int statusCode, final ReturnableModel model) {
        try {
            final byte[] responseBytes = objectMapper.writeValueAsBytes(model);
            httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
            final OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(responseBytes);
            responseBody.close();
        } catch (Exception e) {
            log.error("Failed to send response. {}", e.toString());
        }
    }

    private void setRequestIdInThreadContext() {
        final String requestId = UUID.randomUUID().toString();
        ThreadContext.put("requestId", requestId);
    }

    private void clearRequestId() {
        ThreadContext.clearAll();
    }
}
