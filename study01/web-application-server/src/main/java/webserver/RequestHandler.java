package webserver;

import java.io.*;
import java.net.Socket;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = "";
            line = br.readLine();
            String[] arr = line.split(" ");
            String method = arr[0].trim();
            String url = arr[1].trim();
            String httpVersion = arr[2].trim();
            String contentType = "";
            String contentLength = "";
            String userAgent = "";
            StringBuilder requestBody = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if ("".equals(line)) {
                    log.debug("빈줄");
                    break;
                }
                arr = line.split(":");
                if (line.startsWith("Content-Type")) {
                    contentType = arr[1].trim();
                }
                if (line.startsWith("Content-Length")) {
                    contentLength = arr[1].trim();
                }
                if (line.startsWith("User-Agent")) {
                    userAgent = arr[1].trim();
                }
            }

            log.debug("method: " + method);
            log.debug("url: " + url);
            log.debug("httpVersion: " + httpVersion);
            log.debug("contentType:" + contentType);
            log.debug("contentLength: " + contentLength);
            log.debug("requestBody:" + requestBody);
            log.debug("userAgent: " + userAgent);

            DataOutputStream dos = new DataOutputStream(out);

            response(out, url, dos);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(OutputStream out, String url, DataOutputStream dos) throws IOException {
        if (url.equals("/")) {
            log.debug("/에 대한 응답 전송");
            byte[] body = {};
            body = "voodoo people".getBytes();
            response200Header(dos, body.length);
            responseBody(dos, body);

            return;
        }

        String fileName = "";

        if (url.equals("/index.html")) {
            fileName = "webapp/index.html";
        }
        if (url.equals("/user/form.html")) {
            fileName = "webapp/user/form.html";
        }
        if (url.startsWith("/user/create")) {
            String[] token = url.split("\\?");
            String[] parameters = token[1].split("&");
            String userId = "";
            String password = "";
            String name = "";
            String email = "";

            for (int i = 0; i < parameters.length; i++) {
                String[] pair = parameters[i].split("=");
                if (pair[0].startsWith("userId")) {
                    userId = pair[1].trim();
                }
                if (pair[0].startsWith("password")) {
                    password = pair[1].trim();
                }
                if (pair[0].startsWith("name")) {
                    name = pair[1].trim();
                }
                if (pair[0].startsWith("email")) {
                    email = pair[1].trim();
                }
            }

            User user = new User(userId, password, name, email);

            log.debug(user.toString());
        }

        if ("".equals(fileName)) {
            fileName = "webapp/404.html";
        }

        byte[] buffer = new byte[1024];
        File file = new File(fileName);
        int length = (int) file.length();
        response200Header(dos, length);
        FileInputStream fileInputStream = new FileInputStream(file);
        int cnt = 0;
        while ((cnt = fileInputStream.read(buffer, 0, 1024)) > 0) {
            out.write(buffer);
            out.flush();
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
