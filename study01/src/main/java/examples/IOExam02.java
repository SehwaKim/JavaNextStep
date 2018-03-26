package examples;

public class IOExam02 {
    public static void main(String[] args) throws Exception {
        int read = -1;
        while ((read = System.in.read()) != -1){
            System.out.println(read);
        }
    }
}
