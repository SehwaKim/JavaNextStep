package webserver;

import db.DataBase;
import http.HttpMethod;
import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try(InputStream in = connection.getInputStream();
            OutputStream out = connection.getOutputStream()) {

            // handling request
            // - start -
            HttpRequest request = null;
            try {
                request = new HttpRequest(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // - end -

            DataOutputStream dos = new DataOutputStream(out);

            if(request.getMethod().equals(HttpMethod.POST)) {
                if("/user/create".equals(request.getPath())) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String body = IOUtils.readData(br, Integer.parseInt(request.getHeader("Content-Length")));
                    Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                    User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                    log.debug("User: {}", user);
                    DataBase.addUser(user);
                    response302Header(dos, "/index.html");
                    return;
                }

                if("/user/login".equals(request.getPath())) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String body = IOUtils.readData(br, Integer.parseInt(request.getHeader("Content-Length")));
                    Map<String, String> params = HttpRequestUtils.parseQueryString(body);

                    User user = DataBase.findUserById(params.get("userId"));
                    if (Objects.isNull(user)) {
                        responseResource(out, "/user/login_failed.html");
                        return;
                    }

                    if (user.getPassword().equals(params.get("password"))) {
                        response302LoginSuccessHeader(dos);
                    } else {
                        responseResource(out, "/user/login_failed.html");
                    }
                    return;
                }
            }

            if("/user/list".equals(request.getPath())) {
//                if (!logined) {
//                    responseResource(out, "/user/login.html");
//                    return;
//                }
                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                byte[] body = sb.toString().getBytes();
                response200Header(dos, body.length);
                responseBody(dos, body);

            } else if (request.getPath().endsWith(".css")) {
                byte[] body = Files.readAllBytes(new File("./webapp" + request.getPath()).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            } else {
                byte[] body = "Hello World".getBytes();
                responseResource(out, request.getPath());
            }

        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }


    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url);
            dos.writeBytes("\r\n");
        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body,0,body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void response200Header(DataOutputStream dos, int contentLength) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: "+contentLength+"\r\n");
            dos.writeBytes("\r\n");
        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }

}