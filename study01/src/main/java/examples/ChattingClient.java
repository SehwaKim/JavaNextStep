package examples;

import java.io.*;
import java.net.Socket;

public class ChattingClient {
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("localhost",8080);
            System.out.println("서버에 접속되었습니다.");

            //sendMsg();
            //recieveMsg();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void sendMsg(Socket socket) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(socket.getOutputStream());

        String line = "";
        while((line = br.readLine()) != null){ //키보드로부터 한줄 읽고
            if("quit".equals(line)){
                break;
            }
            pw.println(line); //한줄 보내기
            pw.flush();
        }
    }

    public void recieveMsg(Socket socket) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(socket.getOutputStream());

    }
}
