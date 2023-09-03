package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpAssetHandler implements HttpRequestHandler {
    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.isAssetRequest();
    }

    @Override
    public void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final var responseBody = new StringBuilder();

        final URL indexPageURL = this.getClass().getClassLoader().getResource("static/assets/" + httpRequest.getEndPoint());
        final File indexFile;
        try {
            indexFile = new File(indexPageURL.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (
                final FileInputStream fileInputStream = new FileInputStream(indexFile);
                final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
        ) {
            while (bufferedReader.ready()) {
                responseBody
                        .append(bufferedReader.readLine())
                        .append(System.lineSeparator());
            }
        }

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/javascript;charset=utf-8 ",
                "Content-Length: " + responseBody.toString().getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}