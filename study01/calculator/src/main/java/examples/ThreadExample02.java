package examples;

public class ThreadExample02 {
    public static void main(String[] args){
        Thread thread1 = new Thread(new MyThread02("*"));
        Thread thread2 = new Thread(new MyThread02("-"));
        Thread thread3 = new Thread(new MyThread02("|"));

        thread1.start();
        thread2.start();
        thread3.start();

        new Thread(()->{
            for(int i=0;i<10;i++){
                System.out.print(i);
                try {
                    Thread.sleep((int)(Math.random()*1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("main stop!!");
    }
}

class MyThread02 implements Runnable {
    private String name;

    public MyThread02(String name){
        this.name = name;
    }

    public void run(){
        for(int i=0;i<=10;i++){
            System.out.print(name);
            try {
                Thread.sleep((int)(Math.random()*1000));
            } catch (InterruptedException e) {
            }
        }
    }
}
