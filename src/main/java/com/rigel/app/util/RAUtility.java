package com.rigel.app.util;

import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Table;
import com.rigel.app.model.ThirdPartyResponse;
import com.rigel.app.model.ThirdPartyResponse.Data;
import com.rigel.app.model.dto.ResetPasswordRequest;
import com.rigel.app.model.dto.UserDto;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.QRCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.*;

import java.security.MessageDigest;

public class RAUtility {

	private static final Font BARCODE_TEXT_FONT = new Font("Arial", Font.PLAIN, 12);

	public static long getCurrentDateTime() {
		ZoneId zoneId = ZoneId.of("Asia/Kolkata");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

		// Epoch milliseconds
		long epochMilli = zonedDateTime.toInstant().toEpochMilli();

		return epochMilli;
	}

	public static LocalDateTime epochToLocalDateTime(long epochMs) {
		return Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static ThirdPartyResponse sendOtp(ResetPasswordRequest request) {
		try {
			// 1. RestTemplate
			RestTemplate restTemplate = new RestTemplate();

			// 2. URL
			String url = Constaints.USER_DOMAIN + "/api/user/send/otp";

			// 4. Headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// 5. HttpEntity (Body + Headers)
			HttpEntity<ResetPasswordRequest> entity = new HttpEntity<>(request, headers);

			// 6. API Call
			ResponseEntity<ThirdPartyResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity,
					ThirdPartyResponse.class);

			// 7. Response
			System.out.println("Status Code: " + response.getStatusCode());
			System.out.println("Response Body: " + response.getBody());

			return response.getBody();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ThirdPartyResponse resetPassword(ResetPasswordRequest request) {
		try {
			// 1. RestTemplate
			RestTemplate restTemplate = new RestTemplate();

			// 2. URL
			String url = Constaints.USER_DOMAIN + "/api/user/password/reset";

			// 4. Headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// 5. HttpEntity (Body + Headers)
			HttpEntity<ResetPasswordRequest> entity = new HttpEntity<>(request, headers);

			// 6. API Call
			ResponseEntity<ThirdPartyResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity,
					ThirdPartyResponse.class);

			// 7. Response
			System.out.println("Status Code: " + response.getStatusCode());
			System.out.println("Response Body: " + response.getBody());

			return response.getBody();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ThirdPartyResponse loginPost(String loginRequest) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			// 1. Create HttpClient
			HttpClient client = HttpClient.newHttpClient();

			// 3. Build POST request
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Constaints.USER_DOMAIN + "/api/user/login"))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(loginRequest))
					.build();

			// 4. Send request and get response
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//		         JSONObject json = new JSONObject(responseBody);
//		         JSONObject data = json.getJSONObject("data");
//
//		         String token = data.getString("access_token");
//		         JSONObject userJson = data.getJSONObject("user");

			ThirdPartyResponse thirdPartyResponse = objectMapper.readValue(response.body(), ThirdPartyResponse.class);

			// 5. Print response
			System.out.println("Status code: " + response.statusCode());
			System.out.println("Response body: " + thirdPartyResponse.getData().getAccess_token());
			return thirdPartyResponse;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String generateMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] messageDigest = md.digest(input.getBytes());

			StringBuilder hexString = new StringBuilder();

			for (byte b : messageDigest) {
				String hex = Integer.toHexString(0xff & b);

				if (hex.length() == 1) {
					hexString.append('0');
				}

				hexString.append(hex);
			}

			return hexString.toString(); // 32 char
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ThirdPartyResponse signupPost(UserDto req) {
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			// 🔥 IMPORTANT: Object type use karo
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

			body.add("email_id", req.getEmail_id());
			body.add("mobile_no", req.getMobile_no());
			body.add("password", req.getPassword());
			body.add("name", req.getName());

			// Company fields
			body.add("companyName", req.getCompanyName());
			body.add("companyLogo", req.getCompanyLogo());

			body.add("gstNumber", req.getGstNumber());
			body.add("panNumber", req.getPanNumber());
			body.add("cinNumber", req.getCinNumber());

			body.add("addressLine1", req.getAddressLine1());
			body.add("addressLine2", req.getAddressLine2());
			body.add("city", req.getCity());
			body.add("state", req.getState());
			body.add("country", req.getCountry());
			body.add("pincode", req.getPincode());

			body.add("website", req.getWebsite());
			body.add("companyType", req.getCompanyType());
			body.add("companyEmployeeCount", req.getCompanyEmployeeCount());

			// ✅ File (logo)
			MultipartFile file = req.getLogo(); // ya req.getLogo()

			if (file != null && !file.isEmpty()) {
				body.add("logo",
						new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
			}
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			ResponseEntity<String> response = restTemplate.exchange(Constaints.USER_DOMAIN + "/api/user/signup",
					HttpMethod.POST, requestEntity, String.class);

			return objectMapper.readValue(response.getBody(), ThirdPartyResponse.class);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void getBarCodePDF(List<String> ls, int width, int height, int perRow, String fileName) {

		try {

			PdfWriter writer = new PdfWriter(fileName + ".pdf");
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);

			Table table = new Table(perRow);

			for (String cd : ls) {

				Barcode128 barcode = new Barcode128(pdf);
				barcode.setCode(cd);

				PdfFormXObject barcodeObject = barcode.createFormXObject(pdf);
				Image barcodeImage = new Image(barcodeObject);

				barcodeImage.setWidth(width);
				barcodeImage.setHeight(height);

				Cell cell = new Cell();
				cell.setBorder(Border.NO_BORDER);

				cell.add(barcodeImage);
				cell.add(new Paragraph(cd));

				table.addCell(cell);
			}

			document.add(table);
			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String callOwnServer() {
		try {
			HttpClient client = HttpClient.newHttpClient();
			com.rigel.app.model.HttpResponse response = new com.rigel.app.model.HttpResponse();
			try {
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Constaints.OWNSERVER_RESULT)).GET()
						.build();

				HttpResponse<String> respons = client.send(request, HttpResponse.BodyHandlers.ofString());
				System.out.println(respons);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return response.body();

		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}
}
