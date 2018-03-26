package examples;

import java.io.*;

// 키보드로부터 한줄씩 입력받아
// 파일 input.txt에 한줄씩 저장하시오
// quit[enter]입력하면 프로그램이 종료됩니다.
public class IOExam04 {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //System.in은 InputStream
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream("input3.txt"));
            // pw = new PrintWriter("input2.txt");

            String line;
            while((line = br.readLine()) != null){
                if(line.equals("quit")){ //"quit".equals(line) line이 널이면 널포인터익셉션뜰수도있어서 이렇게 쓴다!
                    break;
                }
                pw.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }finally {
            pw.close();
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
