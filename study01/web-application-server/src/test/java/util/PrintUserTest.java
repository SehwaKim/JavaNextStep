package util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PrintUserTest {
    @Test
    public void print() {
//        User user = new User("dvno", "1235", "sehwa", "sehwa@@");
//        DataBase.addUser(user);
//        StringBuilder builder = new StringBuilder();
//        Collection<User> users = DataBase.findAll();
//        users.forEach(builder::append);
//        System.out.println(builder);
    }

    @Test
    public void test() {
        File f = new File("./webapp/index.html");
        try {
            byte[] body = Files.readAllBytes(f.toPath());
            System.out.println(body.length);

            String url = "/index.html";
            body = Files.readAllBytes(new File("./webapp"+url).toPath());
            System.out.println(body.length);

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
