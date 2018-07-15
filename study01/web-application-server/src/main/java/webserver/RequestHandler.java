package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
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
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void makeHttpResponse(HttpRequest request, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        String url = request.getHeader("path");

        if (url.equals("/")) {
            byte[] body;
            body = "hello world".getBytes();
//            response200Header(dos, body.length, "text/html");
//            writeBody(dos, body);
            return;
        }

        String fileName = "";

        if (url.endsWith(".css")) {
            fileName = "webapp" + url;
        }

        if (url.equals("/index.html")) {
            fileName = "webapp/index.html";
        }
        if (url.equals("/user/form.html")) {
            fileName = "webapp/user/form.html";
        }

        if (url.equals("/user/list")) {
            String logined = "";

//            if (!"".equals(request.getCookies())) {
//                logined = request.getCookies().split("=")[1];
//            }
//
            if ("true".equals(logined)) {
                StringBuilder builder = new StringBuilder();
                Collection<User> users = DataBase.findAll();
                users.forEach(builder::append);
                byte[] body = builder.toString().getBytes();
//                response200Header(dos, body.length, "text/html");
//                writeBody(dos, body);
            }else {
//                response302Header(dos, "/user/login.html", request.getCookies());
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

            if ("GET".equals(request.getMethod())) {
                String[] token = url.split("\\?");
                parameters = token[1].split("&");
            }
            if ("POST".equals(request.getMethod())) {
//                parameters = request.getBody().toString().split("&");
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

//            response302Header(dos, redirectLocation, cookie);
            return;
        }

        if (url.startsWith("/user/create")) {
            String userId = "";
            String password = "";
            String name = "";
            String email = "";
            String[] parameters = {};

            if ("GET".equals(request.getMethod())) {
                String[] token = url.split("\\?");
                parameters = token[1].split("&");
            }
            if ("POST".equals(request.getMethod())) {
//                parameters = request.getBody().toString().split("&");
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

//            response302Header(dos, "/index.html", "");

            return;
        }
    }
}
