package examples;

public class HelloMain {
    public static void main(String[] args) throws Exception {
        for(int i=1; i<=2; i++){
            String className1 = "examples.Hello" + i;
            //className1에 해당하는 클래스 정보는 clazz에 대입
            Class clazz = Class.forName(className1);

            Hello hello = (Hello)clazz.newInstance();

            hello.hello();
        }
    }
}
