package examples;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WasMain {
    public static void main(String[] args){
        ServerSocket listener = null;
        try{
            listener = new ServerSocket(8080); //8080이란 포트에서 기다리는 서버소켓생성
            System.out.println("client를 기다립니다.");
            while(true) {
                Socket client = listener.accept(); //블러킹 메소드-- 기다리는것!!! 클라이언트 오면 낚아채서 변수에 저장

                //System.out.println("접속한 client : "+client.toString());

                new Thread(() -> {
                    try {
                        handleSocket(client); //refactor -> extract -> method
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }finally {
            try {
                listener.close();
            }catch (Exception e){}
        }
    }

    private static void handleSocket(Socket client) throws IOException {
        OutputStream out = client.getOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));

        InputStream in = client.getInputStream();
        //DataInputStream dis = new DataInputStream(in);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        HttpRequest httpRequest = new HttpRequest();

        String line = null;
        line = br.readLine();
        String[] lineArr = line.split(" ");

        httpRequest.setMethod(lineArr[0]);
        httpRequest.setPath(lineArr[1]);

        String info = null;
        String head = null;

        while((line = br.readLine()) != null) {
            if ("".equals(line)) {
                break;
            }
            lineArr = line.split(": ");
            head = lineArr[0];
            info = lineArr[1];
            /*if(head.equals("Host")){
                httpRequest.setHost(info);
            }else if(head.equals("Content-Length")){
                httpRequest.setContentLength(Integer.parseInt(info));
            }else if(head.equals("User-Agent")){
                httpRequest.setUserAgent(info);
            }else if(head.equals("Content-Type")){
                httpRequest.setContentType(info);
            }*/
            if (line.startsWith("Host:")) {
                httpRequest.setHost(info);
            } else if (line.startsWith("Content-Length: ")) {
                httpRequest.setContentLength(Integer.parseInt(info));
            } else if (line.startsWith("User-Agent: ")) {
                httpRequest.setUserAgent(info);
            } else if (line.startsWith("Content-Type: ")) {
                httpRequest.setContentType(info);
            }
        }

            /*byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = in.read(buffer)) != -1){ // 연결이 끊어질때까지 계속 읽어들여서 출력하겠다
                System.out.write(buffer, 0, count);
            }*/

        System.out.println(httpRequest);

        String baseDir = "/tmp/wasroot";
        String fileName = httpRequest.getPath();
        if("/".equals(fileName)){
            fileName = "/index.html";
        }
        fileName = baseDir + fileName;

        String contentType = "text/html; charset=UTF-8";
        if(fileName.endsWith(".png")){
            contentType =  "image/png";
        }

        File file = null;
        try{
            file = new File(fileName); // java.io.File
        }catch (Exception e){
            file = null;
        }

        if(file.exists()){
            long fileLength = file.length();

            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-Type: " + contentType);
            pw.println("Content-Length: " + fileLength);
            pw.println();
            pw.flush(); // 헤더와 빈줄을 출력

        }else {
            fileName = baseDir + "/errorPage.html";
            file = new File(fileName);
            contentType = "text/html; charset=UTF-8";

            pw.println("HTTP/1.1 404");
            pw.println("Content-Type: " + contentType);
            pw.println();
            pw.flush(); // 헤더와 빈줄을 출력
        }
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int readCount = 0;
        while((readCount = fis.read(buffer)) != -1){
            out.write(buffer,0,readCount);
        }
        out.flush();

        out.close();
        in.close();
        client.close(); // 클라이언트와 접속이 close된다.
    }
}
