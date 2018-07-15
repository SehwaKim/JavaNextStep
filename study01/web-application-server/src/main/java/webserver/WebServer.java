package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import controller.Controller;
import controller.IndexController;
import controller.user.UserCreateController;
import controller.user.UserFormController;
import controller.user.UserListController;
import controller.user.UserLoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    public static Map<String, Controller> contollers;

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        init();

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            log.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                RequestHandler requestHandler = new RequestHandler(connection);
                requestHandler.start();
            }
        }
    }

    private static void init() {
        contollers.put("/index.html", new IndexController());
        contollers.put("/user/form.html", new UserFormController());
        contollers.put("/user/list", new UserListController());
        contollers.put("/user/login", new UserLoginController());
        contollers.put("/user/create", new UserCreateController());
    }
}
