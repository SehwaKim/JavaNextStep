package examples;

import java.io.*;

public class IO2 {
    public static void main(String[] args) throws IOException {
        byte[] buffer = new byte[]{1, 2, 3, 4, 5}; //읽을 대상
        InputStream in = new ByteArrayInputStream(buffer);
        OutputStream out = new FileOutputStream("null.txt");

        int readCount = 0;
        while((readCount = in.read()) != -1){ //읽은걸 readCount에 저장?
            out.write(readCount);
        }
        out.close();
        in.close();
    }
}
