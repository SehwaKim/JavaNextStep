package util;

import db.DataBase;
import model.User;
import org.junit.Test;

import java.util.Collection;

public class PrintUserTest {
    @Test
    public void print() {
        User user = new User("dvno", "1235", "sehwa", "sehwa@@");
        DataBase.addUser(user);
        StringBuilder builder = new StringBuilder();
        Collection<User> users = DataBase.findAll();
        users.forEach(builder::append);
        System.out.println(builder);
    }
}
