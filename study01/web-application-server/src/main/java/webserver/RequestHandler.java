package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;

import db.DataBase;
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
            String method = arr[0].trim().toUpperCase();
            String url = arr[1].trim();
            String host = "";
            String httpVersion = arr[2].trim();
            String contentType = "";
            String contentLength = "";
            String userAgent = "";
            String cookies = "";
            StringBuilder requestBody = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if ("".equals(line)) {
                    log.debug("빈줄");
                    break;
                }
                if (line.startsWith("Host")) {
                    host = line.substring(line.indexOf(":") + 1).trim();
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
                if (line.startsWith("Cookie")) {
                    cookies = arr[1].trim();
                }
            }

            if (!"".equals(contentLength)) {
                while ((line = br.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            log.debug("method: " + method);
            log.debug("url: " + url);
            log.debug("httpVersion: " + httpVersion);
            log.debug("host: " + host);
            log.debug("contentType:" + contentType);
            log.debug("contentLength: " + contentLength);
            log.debug("requestBody:" + requestBody);
            log.debug("userAgent: " + userAgent);
            log.debug("cookie: " + cookies);
            log.debug(requestBody.toString());

            DataOutputStream dos = new DataOutputStream(out);

            response(cookies, host, out, url, dos, method, requestBody);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(String cookies, String host, OutputStream out, String url, DataOutputStream dos, String method, StringBuilder requestBody) throws IOException {
        if (url.equals("/")) {
            log.debug("/에 대한 응답 전송");
            byte[] body;
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

        if (url.equals("/user/list")) {
            String logined = "";

            if (!"".equals(cookies)) {
                logined = cookies.split("=")[1];
            }

            if ("true".equals(logined)) {
                StringBuilder builder = new StringBuilder();
                Collection<User> users = DataBase.findAll();
                users.forEach(builder::append);
                byte[] body = builder.toString().getBytes();
                response200Header(dos, body.length);
                responseBody(dos, body);
            }else {
                response302Header(dos, "http://" + host + "/user/login.html", cookies);
            }

            return;
        }

        if (url.equals("/user/login_failed.html")) {
            fileName = "webapp/user/login_failed.html";
        }else if (url.equals("/user/login.html")) {
            fileName = "webapp/user/login.html";
        }else if (url.startsWith("/user/login")) {
            String userId = "";
            String password = "";
            String[] parameters = {};

            if ("GET".equals(method)) {
                String[] token = url.split("\\?");
                parameters = token[1].split("&");
            }
            if ("POST".equals(method)) {
                parameters = requestBody.toString().split("&");
            }

            for (int i = 0; i < parameters.length; i++) {
                String[] pair = parameters[i].split("=");
                if (pair[0].startsWith("userId")) {
                    userId = pair[1].trim();
                }
                if (pair[0].startsWith("password")) {
                    password = pair[1].trim();
                }
            }

            User user = DataBase.findUserById(userId);
            boolean identical = false;
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    identical = true;
                }
            }

            String cookie = "";
            String redirectLocation = "";

            if (identical) {
                cookie = "logined=true;";
                redirectLocation = "/index.html";
            }else {
                cookie = "logined=false;";
                redirectLocation = "/user/login_failed.html";
            }

            response302Header(dos, "http://" + host + redirectLocation, cookie);
            return;
        }

        if (url.startsWith("/user/create")) {
            String userId = "";
            String password = "";
            String name = "";
            String email = "";
            String[] parameters = {};

            if ("GET".equals(method)) {
                String[] token = url.split("\\?");
                parameters = token[1].split("&");
            }
            if ("POST".equals(method)) {
                parameters = requestBody.toString().split("&");
            }

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
            DataBase.addUser(user);
            log.debug(user.toString());

            response302Header(dos, "http://" + host + "/index.html", "");
//            response200Header(dos, 0);
//            responseBody(dos, new byte[]{});
//            fileName = "webapp/404.html";
            return;
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

    private void response302Header(DataOutputStream dos, String redirectLocation, String cookie) {
        log.debug(redirectLocation);
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("Location: " + redirectLocation + "\r\n");
            dos.writeBytes("Status: 302\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
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
