package com.rigel.app.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.annotation.ApiSecured;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.exception.TaskTitleException;
import com.rigel.app.exception.TaskTitleNotFound;
import com.rigel.app.model.LoginActivity;
import com.rigel.app.model.LoginDetails;
import com.rigel.app.model.LoginRequest;
import com.rigel.app.model.Mail;
import com.rigel.app.model.ThirdPartyResponse;
import com.rigel.app.model.User;
import com.rigel.app.model.dto.*;
import com.rigel.app.security.JwtTokenUtil;
import com.rigel.app.security.JwtUser;
import com.rigel.app.service.ILoginInfoService;
import com.rigel.app.service.IUserLogOutIn;
import com.rigel.app.service.IUserService;
import com.rigel.app.serviceimpl.LoginInfoService;
import com.rigel.app.util.*;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user/")
@ApiSecured
@Tag(name = "User Api", description = "User API endpoints")
public class UserController {

	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private ObjectMapper mapper;

//	@Autowired
//	CryptoAES128 cryptoAES128;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private IUserLogOutIn userLogOutIn;

	@Autowired
	private ILoginInfoService loginInfoService;

	@Autowired
	private Environment environment;

	@RequestMapping(value = "sendEmail", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody(required = true) @Valid Mail mail,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		if (mail == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			Map<String, Object> data = userService.sendEmailToAll(mail);
			response.put("data", data);
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Your account has been created successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> signup(@ModelAttribute @Valid UserDto userDtoReq, BindingResult result,
			HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (userDtoReq == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			System.out.println("aaaaaaaaaaaa--" + userDtoReq.toString());
			ThirdPartyResponse thirdPartyResponse = RAUtility.signupPost(userDtoReq);
			if (thirdPartyResponse != null) {
				User user = thirdPartyResponse.getData().getUser();
				String json = null;
				try {
					json = mapper.writeValueAsString(user);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("dddddddddddddddddd" + user.getLogo());
				UploadFileUtlity.uploadImageNfiles(userDtoReq.getLogo(), "logo", user.getLogo().split("\\.")[0]);
				JsonNode userNode = mapper.valueToTree(user);
				String token = thirdPartyResponse.getData().getAccess_token();
				LoginActivity loginActivity1 = LoginActivity.builder().loginAt(LocalDateTime.now()).token(token)
						.emailId(user.getEmail_id()).mobileNumber(user.getMobile_no()).userObject(json).build();
				loginInfoService.saveLoginActivity(Arrays.asList(loginActivity1));

				data.put("access_token", token);
				data.put("user", user);
				response.put("data", data);
				response.put("status", "OK");
				response.put("code", "200");
				response.put("message", "Your account has been created successfully.");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				throw new TaskTitleException("Record already registered with us.");
			}

		}
	}

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> login(@RequestBody(required = true) @Valid LoginRequest login,
			HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> data2 = new HashMap<>();
		String secret = null;
		LoginActivity loginActivity = loginInfoService.findLoginActivityByUsername(login.getUsername());
//		System.out.println((jwtTokenUtil.isTokenExpired(loginActivity.getToken())) + "------------" + loginActivity);
		if (loginActivity == null || (loginActivity != null
				&& jwtTokenUtil.isTokenExpired(loginActivity != null ? loginActivity.getToken() : null))) {
			ThirdPartyResponse thirdPartyResponse = RAUtility.loginPost(login.toString());
			if (thirdPartyResponse == null) {
				throw new TaskTitleNotFound("Email id not existing with us.");
			} else {
				secret = RAUtility.generateMD5(thirdPartyResponse.getData().toString());
				System.out.println("secret----" + secret);
				User user = thirdPartyResponse.getData().getUser();
				String json = null;
				try {
					json = mapper.writeValueAsString(user);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				UserDto userDto = modelMapper.map(user, UserDto.class);
				if (loginActivity != null) {
					loginActivity.setToken(thirdPartyResponse.getData().getAccess_token());
					loginInfoService.updateLoginActivity(loginActivity);
				} else {
					UploadFileUtlity.downloadImageFromApi("logo", user.getLogo(), Constaints.LOGO_PATH);
					JsonNode userNode = mapper.valueToTree(user);
					LoginActivity loginActivity1 = LoginActivity.builder().loginAt(LocalDateTime.now())
							.token(thirdPartyResponse.getData().getAccess_token()).emailId(user.getEmail_id())
							.mobileNumber(user.getMobile_no()).userObject(json).secret(secret).build();
					loginInfoService.saveLoginActivity(Arrays.asList(loginActivity1));
				}
//				data.put("access_token",cryptoAES128.encrypt(token));
				data.put("secret", secret);
				data.put("user", thirdPartyResponse.getData().getUser());
				response.put("data", data);
				response.put("status", "OK");
				response.put("code", "200");
				response.put("message", "Your account has been logined successfully.");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} else {
			User user = null;
			System.out.println("loginActivity.getUserObject()----" + loginActivity.getUserObject());
			try {
				String obj = loginActivity.getUserObject();
				// Read
				user = mapper.readValue(obj, User.class);
//				loginActivity.getUserObject()
//				MyClass obj = mapper.readValue(userObject, MyClass.class);
//				user = mapper.treeToValue(loginActivity.getUserObject(), User.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			data2.put("access_token", loginActivity.getToken());
			data2.put("user", user);
			secret = RAUtility.generateMD5(data2.toString());

			data.put("secret", secret);
			data.put("user", user);
			response.put("data", data);
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Your account has been logined successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);

		}
	}

	@RequestMapping(value = "send/otp", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sendOtp(
			@RequestBody(required = true) @Valid ResetPasswordRequest sendOtp, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		if (sendOtp != null) {
			ThirdPartyResponse thirdPartyResponse = RAUtility.sendOtp(sendOtp);
			if (thirdPartyResponse == null) {
				throw new TaskTitleNotFound("Email id not existing with us.");
			} else {
				response.put("data", thirdPartyResponse.getData());
				response.put("status", "OK");
				response.put("code", "200");
				response.put("message", "OTP has been sent.");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} else {
			throw new TaskTitleNotFound("Invalid Request");
		}
	}

	@RequestMapping(value = "view/profile", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> viewProfile() {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		LoginActivity loginActivity = userService.findLoginActivity("");
		if (loginActivity == null) {
			throw new TaskTitleNotFound("Email id not existing with us.");
		} else {
			data.put("loginActivity", loginActivity);
			response.put("data", data);
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Profile has been fetch.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/view/file")
	public ResponseEntity<?> viewFile(@RequestParam String path, @RequestParam String fileName, @RequestParam int type)
			throws IOException {

		String basePath;

		switch (path.toLowerCase()) {
		case "logo":
			basePath = Constaints.LOGO_PATH;
			break;
		case "product":
			basePath = Constaints.PRODUCT_IMAGES_PATH;
			break;
		case "invoice":
			basePath = Constaints.INVOICE_FILE_PATH;
			break;
		case "expense":
			basePath = Constaints.EXPENSE_IMAGES_PATH;
			break;
		default:
			throw new RuntimeException("Invalid file type");
		}

		File file = new File(Constaints.PROJECT_DIR + basePath + fileName);

		if (!file.exists()) {
			throw new RuntimeException("File not found");
		}

		// ---------------- TYPE 1: VIEW (STREAM)
		String contentType = Files.probeContentType(file.toPath());
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		if (type == 1) {

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header("Content-Disposition", "inline; filename=\"" + fileName + "\"").body(resource);
		}

		// ---------------- TYPE 2: DOWNLOAD (COPY FILE, NO BYTE[])
		else {

			try {
				String userHome = System.getProperty("user.home");

				String safeFileName = fileName.contains("_") ? fileName.split("_")[1] : fileName;

				Path downloadPath = Paths.get(userHome, "Downloads",Constaints.DOWNLOAD_FOLDER_NAME, safeFileName);

				Files.copy(file.toPath(), downloadPath, StandardCopyOption.REPLACE_EXISTING);

				int count = 1;
				while (Files.exists(downloadPath)) {
				    String newName = "(" + count + ")_" + safeFileName;
				    downloadPath = downloadPath.getParent().resolve(newName);
				    count++;
				}

				Files.copy(file.toPath(), downloadPath);
				
				
				System.out.println("Saved at: " + downloadPath);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
		}
	}

	@RequestMapping(value = "password/reset", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> resetPassword(
			@RequestBody(required = true) @Valid ResetPasswordRequest sendRequest, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		if (sendRequest != null) {
			ThirdPartyResponse thirdPartyResponse = RAUtility.resetPassword(sendRequest);
			if (thirdPartyResponse == null) {
				throw new TaskTitleNotFound("Email id not existing with us.");
			} else {
				response.put("data", thirdPartyResponse.getData());
				response.put("status", "OK");
				response.put("code", "200");
				response.put("message", "Your account password has been change successfully..");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} else {
			throw new TaskTitleNotFound("Invalid Request");
		}
	}
//	    @RequestMapping(value = "logOut",method = RequestMethod.POST)
//		public ResponseEntity<Map<String,Object>> logOut(HttpServletRequest request){
//			Map<String,Object> response=new HashMap<>();
//			Map<String,Object> data=new HashMap<>();
//			String email=jwtTokenUtil.getEmailFromToken(request.getHeader(environment.getProperty("security.jwt.header")).substring(7));
//			User user=userService.findUserByEmailId(email);
//			     	userLogOutIn.logOutUser(user.getId(), user.getEmail_id());		
//					response.put("data", data);
//					response.put("status", "OK");
//					response.put("code", "200");
//					response.put("message","Your account has been logout successfully.");
//					return new ResponseEntity<>(response, HttpStatus.OK);
//			
//	    }

//	@RequestMapping(value = "testapi", method = RequestMethod.GET)
//	public String d() {
//		return "abc";
//	}
//
//	@RequestMapping(value = "testapi2", method = RequestMethod.GET)
//	public String d2() {
//		return "abc";
//	}
//
//	// Deserialize byte[] back to User
//	public User deserializeUser(byte[] data) {
//		if (data == null)
//			return null;
//		try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
//				ObjectInputStream ois = new ObjectInputStream(bis)) {
//
//			return (User) ois.readObject(); // cast to User
//
//		} catch (IOException | ClassNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}
