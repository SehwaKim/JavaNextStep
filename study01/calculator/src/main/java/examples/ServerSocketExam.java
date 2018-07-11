package examples;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketExam {
    public static void main(String[] args) {
        ServerSocket server = null;
        PrintWriter pw = null;
        BufferedReader br = null;
        try{
            server = new ServerSocket(9090);

            System.out.println("client 접속을 기다립니다.");
            Socket client = server.accept();
            System.out.println("client 접속이 되었습니다.");
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String fileName = br.readLine();
            pw = new PrintWriter(new FileOutputStream("c:\\temp\\"+fileName));
            String line = "";
            while((line = br.readLine()) != null){
                if("quit".equals(line)){
                    break;
                }
                pw.println(line);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            try {
                pw.close();
                br.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

}