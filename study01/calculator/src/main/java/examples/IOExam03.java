package examples;

import java.io.FileOutputStream;

public class IOExam03 {
    public static void main(String[] args) throws Exception {
        // 파일에 1byte씩 1024*1024번 저장 -----> 6000밀리세컨초
        FileOutputStream fos = new FileOutputStream("data.dat");

        long start = System.currentTimeMillis();
        //int value=0;
        //for(int i=0;i<1024*1024;i++){
        //    fos.write(value);
        //}

        // 파일에 1024byte씩 1024번 저장 -----> 9밀리세컨초
        byte[] buffer = new byte[1024];
        for(int i=0;i<1024;i++){
            fos.write(buffer);
        }

        long end = System.currentTimeMillis();
        System.out.println(end-start);

        fos.close();
    }
}
