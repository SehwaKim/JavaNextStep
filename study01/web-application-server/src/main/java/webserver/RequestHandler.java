package webserver;

import controller.Controller;
import controller.CreateUserController;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
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

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String path = request.getPath();

            /*if("/user/create".equals(path)) {
                createUser(request, response);
            }

            if("/user/login".equals(path)) {
                login(request, response);
            }

            if("/user/list".equals(path)) {
                listUser(request, response);
            } else {
                response.forward(path);
            }*/

            Controller controller = RequestMapping.getController(path);

            if (controller == null) {
                response.forward(getDefaultPath(path));
                return;
            }

            controller.service(request, response);

        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if ("/".equals(path)) {
            return "/index.html";
        }
        return path;
    }
}