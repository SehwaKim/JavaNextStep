package util;

import org.junit.Test;

import java.io.File;

public class FileTest {
    @Test
    public void read() {
        File file = new File("webapp/index.html");
        System.out.println(file.getName());
        System.out.println(file.exists());
    }
}
