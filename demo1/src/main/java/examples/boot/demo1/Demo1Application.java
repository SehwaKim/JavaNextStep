package examples.boot.demo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo1Application {

	public static void main(String[] args) {
		SpringApplication.run(Demo1Application.class, args); //스프링부트는 자바어플리케이션처럼 그냥 main메소드 run하면된다
	}
}
