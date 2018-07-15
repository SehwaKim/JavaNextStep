package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private Map<String, String> headers;
    private OutputStream out;

    public HttpResponse(OutputStream out) {
        this.out = out;
        headers = new HashMap<>();
    }

    public void forward(String s) {
    }

    public void forwardBody(String fileName) throws IOException {
        Path filePath = FileSystems.getDefault().getPath(fileName);
        File file = new File(fileName);
        long length = file.length();

        String contentType = "";

        if (fileName.endsWith(".css")) {
            contentType = "text/css";
        }

        if (fileName.endsWith(".html")) {
            contentType = "text/html";
        }

        Files.copy(filePath, out); // nio2
    }

    public void responseBody(byte[] body) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.write(body, 0, body.length);
            dos.flush();
        }
    }

    public void sendRedirect(String redirectLocation) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + redirectLocation + "\r\n");
//            dos.writeBytes("Set-Cookie: " + cookies + "\r\n");
            dos.writeBytes("\r\n");
        }
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void response200Header(int contentLength) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
//            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + contentLength + "\r\n");
            dos.writeBytes("\r\n");
        }
    }

    public void processHeaders() {

    }
}
