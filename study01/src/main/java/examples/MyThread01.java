package examples;

public class MyThread01 extends Thread {
    private String threadName;

    public MyThread01(String threadName){
        this.threadName = threadName;
    }

    public void run(){
        for(int i=0;i<=10;i++){
            System.out.print(threadName);
            try {
                Thread.sleep((int)(Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        MyThread01 thread1 = new MyThread01("*");
        MyThread01 thread2 = new MyThread01("-");
        MyThread01 thread3 = new MyThread01("|");

        thread1.start();
        thread2.start();
        thread3.start();

        System.out.println("end!!");
    }
}
