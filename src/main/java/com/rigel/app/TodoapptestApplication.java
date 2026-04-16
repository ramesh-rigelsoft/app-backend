package com.rigel.app;

import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.rigel.app.util.Constaints;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


@SpringBootApplication //(exclude = SecurityAutoConfiguration.class)
public class TodoapptestApplication {

	public static void main(String[] args) {
		   String logPath = "C:\\Program Files\\RigelEIMS\\todoapp.log";
//		   deleteLogFile(logPath);
//	    ConfigurableApplicationContext context = SpringApplication.run(TodoapptestApplication.class, args);
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(TodoapptestApplication.class);

		ConfigurableApplicationContext context=builder.headless(false)      // <<< THIS IS IMPORTANT
               .run(args);
       
		SwingUtilities.invokeLater(() -> {
            FXApp.startBrowser(context,args);
        });
	}
}

//jpackage --type msi --name TodoApp --input . --main-jar todoapp.jar --main-class com.app.todoapp.TodoapptestApplication --app-version 1.0.0 --win-menu --win-shortcut --win-dir-chooser
