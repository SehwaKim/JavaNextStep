package examples;

import java.io.*;

public class IOPractice {
    public static void main(String[] args) throws IOException {
        byte[] arr = new byte[]{100,101};

        InputStream in = new ByteArrayInputStream(arr);

        int i=0;
        try {
            i = in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(i);

        try {
            i = in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(i);

        try {
            i = in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(i);

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[1024];
        InputStream filein = new FileInputStream("d.png");
        OutputStream out = new FileOutputStream("newhome.png");
        try {
            int readCount = 0;
            /*while((readCount = filein.read(buffer)) != -1){
                out.write(buffer);
            }*/
            filein.read(buffer);
            out.write(buffer);
            for(byte e : buffer){
                System.out.println(e);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           out.close();
        }

    }
}
