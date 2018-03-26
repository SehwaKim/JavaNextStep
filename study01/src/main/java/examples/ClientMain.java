package examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) {
        String ip = "10.10.118.35";
        int port = 9090;
        PrintWriter pw = null;
        BufferedReader br = null;
        try{
            Socket socket = new Socket(ip, port);
            System.out.println("접속 완료");
            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                String line = br.readLine();
                if("quit".equals(line)){
                    break;
                }
                pw.println(line);
                pw.flush();
                //flush를 안하면 OS는 버퍼가 다 찰때까지 갖고만있고 안보냄.
                //근데 flush하면 그때그때 다 보내라고 OS한테 명령내리고 네트워크에 확 보내버리는 것
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            try{
                pw.close();
                br.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

    }
}