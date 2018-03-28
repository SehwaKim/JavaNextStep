package examples;

public class LambdaPractice {

    public static void main(String[] args){

        //hello world를 출력하는 스레드 생성
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world");
            }
        }).start();


        //lambda로 표현
        new Thread(()->{
            System.out.println("hello world");
        }).start();


        //MyFunction을 익명클래스로 구현했다. 근데 이렇게 인터페이스 생성자 호출해도 되는건가?
        MyFunction func = new MyFunction() {
            @Override
            public int max(int a, int b) {
                return a > b ? a : b;
            }
        };

        //람다식으로 MyFunction의 익명클래스를 대체해보자
        //func = (a, b) -> a > b ? a : b; //함수형 인터페이스의 메소드 오버라이드

        int maxVal = func.max(10,100);
        System.out.println(maxVal);

    }
}
