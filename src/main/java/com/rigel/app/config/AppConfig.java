package com.rigel.app.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rigel.app.util.Constaints;

import jakarta.annotation.PostConstruct;

@Configuration
public class AppConfig implements WebMvcConfigurer {

	@Autowired
	HeadersInterceptors headersInterceptors;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/logo/**").addResourceLocations("file:" + Constaints.LOGO_PATH);
		registry.addResourceHandler("/invoice/**").addResourceLocations("file:" + Constaints.INVOICE_FILE_PATH);
		registry.addResourceHandler("/product/**").addResourceLocations("file:" + Constaints.PRODUCT_IMAGES_PATH);
		registry.addResourceHandler("/expense/**").addResourceLocations("file:" + Constaints.EXPENSE_IMAGES_PATH);

	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
//		mapper.setVisibility(
//				((Object) mapper)
////				.getSerializationConfig()
//				.getDefaultVisibilityChecker()
////				withFieldVisibility(JsonAutoDetect.Visibility.ANY)
//                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
//                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
//                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		return mapper;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(headersInterceptors);
	}

	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost("smtp.gmail.com");
		javaMailSender.setPort(587);
		javaMailSender.setPassword("holtediqjrtthrfu");
		javaMailSender.setUsername("rameshkumar13111@gmail.com");
		javaMailSender.setJavaMailProperties(getMailProperties());
		return javaMailSender;
	}

	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.port", "25");
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.debug", "false");
		return properties;
	}

	// @PostConstruct
	// public void createResourceFolders() {
	// 	createFolder(Constaints.LOGO_PATH);
	// 	createFolder(Constaints.PRODUCT_IMAGES_PATH);
	// 	createFolder(Constaints.INVOICE_FILE_PATH);
	// 	createFolder(Constaints.EXPENSE_IMAGES_PATH);
	// }

	// private void createFolder(String path) {
	// 	File dir = new File(path);
	// 	if (!dir.exists()) {
	// 		boolean created = dir.mkdirs();
	// 		if (created) {
	// 			System.out.println("Folder created: " + path);
	// 		} else {
	// 			System.err.println("Failed to create folder: " + path);
	// 		}
	// 	}
	// }
}
