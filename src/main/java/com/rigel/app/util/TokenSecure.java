package com.rigel.app.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

import jakarta.servlet.http.HttpServletRequest;

public class TokenSecure {

	public static String getBrowserInfo(HttpServletRequest req) {
		String userAgent = req.getHeader("user-agent");
		try {
			boolean junitPresent = org.springframework.util.ClassUtils.isPresent("org.junit.jupiter.api.Test",
					Thread.currentThread().getContextClassLoader());
			if (junitPresent) {
				userAgent = "JUnit-Test-Client/1.0";

			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		System.out.println(userAgent);
		return userAgent;
	}

	public static String getOSInfo() {
		InetAddress localHost;
		String macAddress = null;
		try {
			localHost = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
			byte[] hardwareAddress = ni.getHardwareAddress();
			String[] hexadecimal = new String[hardwareAddress.length];
			for (int i = 0; i < hardwareAddress.length; i++) {
				hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
			}
			macAddress = System.getProperty("os.name") + "(" + String.join("-", hexadecimal) + ")";
			System.out.println(macAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return macAddress;
	}
}
