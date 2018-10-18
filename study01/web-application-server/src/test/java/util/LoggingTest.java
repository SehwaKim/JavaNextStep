package util;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class LoggingTest {
    @Test
    public void loopTest() {
        long start = System.currentTimeMillis();
        int num = 0;
        for (int i = 0; i < 100000; i++) {
            num++;
        }
        long end = System.currentTimeMillis();
        System.out.println("time spent: " + (end - start));
    }

    @Test
    public void printTest() {
        long start = System.currentTimeMillis();
        try {
            System.setOut(new PrintStream(new FileOutputStream("sehwa.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int num = 0;
        for (int i = 0; i < 100000; i++) {
            num++;
            System.out.println("a");
        }
        long end = System.currentTimeMillis();
        System.out.println("time spent: " + (end - start));
        // 422
        // 609
    }
}
