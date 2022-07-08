package cz.cvut.fit.alekhkir.tjv.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        var app = new SpringApplication(Application.class);
        app.setWebApplicationType(WebApplicationType.NONE); // stopping when we type "exit" in terminal
        app.run(args);
    }
}
