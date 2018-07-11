package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChattingServer {
    public static void main(String[] args) {
        ServerSocket server = null;
        try{
            server = new ServerSocket(8080);
            System.out.println("client를 기다립니다.");

            while(true) {
                Socket client = server.accept();

                new Thread(() -> {
                    try {
                        handlesocket(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }finally {
            try {
                server.close();
            }catch (Exception e){}
        }
    }

    private static void handlesocket(Socket client) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter pw = new PrintWriter(client.getOutputStream());



    }
}
