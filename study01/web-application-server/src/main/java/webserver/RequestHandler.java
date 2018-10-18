package webserver;

import db.DataBase;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            log.debug("request line: {}", line);

            if (line == null) {
                return;
            }

            String[] token = line.split(" ");

            int contentLength = 0;
            Boolean logined = false;

            while(true) {
                line = br.readLine();
                if ("".equals(line)) {
                    break;
                }
                log.debug("header: {}", line);
                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                }
                if (line.contains("Cookie")) {
                    logined = isLogin(line);
                }
            }

            String method = token[0].toLowerCase();
            String url = token[1];

            DataOutputStream dos = new DataOutputStream(out);

            if(method.equals("post")) {
                if("/user/create".equals(url)) {
                    String body = IOUtils.readData(br, contentLength);
                    Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                    User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                    log.debug("User: {}", user);
                    DataBase.addUser(user);
                    response302Header(dos, "/index.html");
                    return;
                }

                if("/user/login".equals(url)) {
                    String body = IOUtils.readData(br, contentLength);
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

            if("/user/list".equals(url)) {
                if (!logined) {
                    responseResource(out, "/user/login.html");
                    return;
                }
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

            } else if (url.endsWith(".css")) {
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            } else {
                byte[] body = "Hello World".getBytes();
                responseResource(out, url);
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

    private Boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String val = cookies.get("logined");
        return Boolean.parseBoolean(val);
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

    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    /*private byte[] getUserList() {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n" +
                "<html lang=\"kr\">\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "<table>\n");

        for (String id : userDB.keySet()) {
            builder.append("<tr>\n" +
                    "<td>"+userDB.get(id).getName()+"</td>" +
                    "</tr>\n");
        }

        builder.append("</table>\n" +
                "</body>\n" +
                "</html>");

        return builder.toString().getBytes();
    }*/

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