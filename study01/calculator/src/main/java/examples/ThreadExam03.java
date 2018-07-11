package examples;

public class ThreadExam03 {
    public static void main(String[] args) { //psvm!!!
        Cube cube = new Cube();
        MyThread03 t1 = new MyThread03(cube,1);
        MyThread03 t2 = new MyThread03(cube,2);
        MyThread03 t3 = new MyThread03(cube,3);
        //하나의 인스턴스를 스레드들이 공통으로 갖게하니까 cube는 공유 객체다

        t1.start();
        t2.start();
        t3.start();

    }
}

class Cube {
    int x = 1;

    public synchronized void a(){
        while(x != 1){
            try {
                wait();
            }catch (Exception ex){

            }
        }

        for (int i = 0; i < 10; i++) {
            System.out.print("a");
            try {
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        x = 2;
        notifyAll();
    }

    public synchronized void b(){
        while(x != 2){
            try {
                wait();
            }catch (Exception ex){

            }
        }
        for(int i=0;i<10;i++){
            System.out.print("b");
            try {
                Thread.sleep((int)(Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        x = 3;
        notifyAll();
    }

    public synchronized void c(){
        while(x != 3){
            try {
                wait();
            }catch (Exception ex){

            }
        }
        for(int i=0;i<10;i++){
            System.out.print("c");
            try {
                Thread.sleep((int)(Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class MyThread03 extends Thread{
    private Cube cube;
    private int flag;

    public MyThread03(Cube cube, int flag){
        this.cube = cube;
        this.flag = flag;
    }

    public void run(){
        if(flag == 1){
            cube.a();
        }else if(flag == 2){
            cube.b();
        }else if(flag == 3){
            cube.c();
        }
    }
}
