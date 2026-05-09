package com.rigel.app.serviceimpl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Supplier;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.SupplierDTO;
import com.rigel.app.service.ISupplierService;
import com.rigel.app.util.StateUtil;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;

@Service
public class GSTNumberService {

	@Autowired
	private ISupplierService supplierService;
	
	@Autowired
	private ObjectMapper mapper;

	public void openGstBrowser(String gstin, int ownerId) {

		WebDriver driver = null;
		String status1 = null;

		try {
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			driver.manage().window().maximize();

			driver.get("https://services.gst.gov.in/services/searchtp");

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dimmer-holder")));

			try {
				WebElement gstField = wait.until(ExpectedConditions.elementToBeClickable(By.name("for_gstin")));
				gstField.clear();
				gstField.sendKeys(gstin);

			} catch (Exception ex) {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("document.getElementById('for_gstin').value='" + gstin + "';");
			}

			System.out.println("GSTIN entered, solve CAPTCHA manually");
			Thread.sleep(20000);

			driver.findElement(By.id("lotsearch")).click();

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//strong[contains(text(),'Legal Name of Business')]")));

			String legalName = driver
					.findElement(By.xpath(
							"//strong[contains(text(),'Legal Name of Business')]/parent::p/following-sibling::p"))
					.getText();

			String principalPlace = driver
					.findElement(By.xpath(
							"//strong[contains(text(),'Principal Place of Business')]/parent::p/following-sibling::p"))
					.getText();

			String status = driver
					.findElement(
							By.xpath("//strong[contains(text(),'GSTIN / UIN  Status')]/parent::p/following-sibling::p"))
					.getText();

			String stateCode = gstin.substring(0, 2);
			String stateName = StateUtil.getStateName(stateCode);

			// ✅ VALIDATION
			if (status == null || !"Active".equalsIgnoreCase(status)) {
				throw new BadGatewayRequest("GST is not active. Current status: " + status);
			}
			status1 = status;
			Supplier existingSupplier = supplierService.searchSupplier(
					SupplierCreteria.builder().startIndex(0).maxRecords(1).userId(ownerId).gstNumber(gstin).build())
					.stream().findFirst().orElse(null);
			if (existingSupplier == null) {
				SupplierDTO dto = SupplierDTO.builder().gstNumber(gstin).pinCode(extractPincode(principalPlace))
						.ownerId(ownerId).supplierName(legalName).address(principalPlace).status(status)
						.state(stateName).stateCode(stateCode).build();
				supplierService.saveSupplier(dto);
			} else {
				existingSupplier.setPinCode(extractPincode(principalPlace));
				existingSupplier.setSupplierName(legalName);
				existingSupplier.setStatus(status);
				existingSupplier.setState(stateName);
				existingSupplier.setStateCode(stateCode);
				existingSupplier.setAddress(principalPlace);
				SupplierDTO supplierDto= mapper.convertValue(existingSupplier, SupplierDTO.class);
				supplierService.saveSupplier(supplierDto);
			}
			System.out.println("GST Saved Successfully");

		} catch (BadGatewayRequest e) {
			throw e; // ✔ IMPORTANT: controller tak jayega

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadGatewayRequest("Please Fill Manualy, GST Portal issue for fetching GST Details");

		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}

	public static String extractPincode(String address) {
		if (address == null)
			return null;

		Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
		Matcher matcher = pattern.matcher(address);

		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}

//package com.rigel.app.serviceimpl;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.time.Duration;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.rigel.app.exception.BadGatewayRequest;
//import com.rigel.app.model.Supplier;
//import com.rigel.app.model.dto.SupplierDTO;
//import com.rigel.app.service.ISupplierService;
//import com.rigel.app.util.StateUtil;
//
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.JavascriptExecutor;
//
//@Service
//public class GSTNumberService {
//
//	@Autowired
//	private ISupplierService supplierService;
//	
//    private static WebDriver driver;
//
//    public void openGstBrowser(String gstin,int ownerId) {
//        try {
//            WebDriverManager.chromedriver().setup();
//            driver = new ChromeDriver();
//            driver.manage().window().maximize();
//
//            driver.get("https://services.gst.gov.in/services/searchtp");
//
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
//
//            // ✅ Wait until overlay disappears
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dimmer-holder")));
//
//            // ✅ Try normal sendKeys
//            try {
//                WebElement gstField = wait.until(ExpectedConditions.elementToBeClickable(By.name("for_gstin")));
//                gstField.clear();
//                gstField.sendKeys(gstin);
//            } catch (Exception ex) {
//                // ✅ Fallback: JavaScript if click intercepted
//                JavascriptExecutor js = (JavascriptExecutor) driver;
//                js.executeScript("document.getElementById('for_gstin').value='" + gstin + "';");
//            }
//
//            System.out.println("GSTIN entered, now solve CAPTCHA manually");
//
//            // 2️⃣ WAIT for user to solve captcha
//            Thread.sleep(20000);
//
//            // 3️⃣ CLICK SEARCH BUTTON
//            WebElement searchBtn = driver.findElement(By.id("lotsearch"));
//            searchBtn.click();
//
//            System.out.println("Search clicked");
//
//            // 4️⃣ WAIT FOR RESULT LOAD
//            wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.xpath("//strong[contains(text(),'Legal Name of Business')]")
//            ));
//
//            // 5️⃣ SCRAPE RESULT (using correct DOM structure)
//            String legalName = driver.findElement(By.xpath("//strong[contains(text(),'Legal Name of Business')]/parent::p/following-sibling::p")).getText();
//            String tradeName = driver.findElement(By.xpath("//strong[contains(text(),'Trade Name')]/parent::p/following-sibling::p")).getText();
//            String effectiveDate = driver.findElement(By.xpath("//strong[contains(text(),'Effective Date of registration')]/parent::p/following-sibling::p")).getText();
//            String constitution = driver.findElement(By.xpath("//strong[contains(text(),'Constitution of Business')]/parent::p/following-sibling::p")).getText();
//            String status = driver.findElement(By.xpath("//strong[contains(text(),'GSTIN / UIN  Status')]/parent::p/following-sibling::p")).getText();
//            String taxpayerType = driver.findElement(By.xpath("//strong[contains(text(),'Taxpayer Type')]/parent::p/following-sibling::p")).getText();
//            String principalPlace = driver.findElement(By.xpath("//strong[contains(text(),'Principal Place of Business')]/parent::p/following-sibling::p")).getText();
//            String stateCode = gstin.substring(0, 2);
//            String stateName=StateUtil.getStateName(stateCode);
//           
//            if (status == null || !"Active".equalsIgnoreCase(status)) {
//                throw new BadGatewayRequest("GST is not active. Current status: " + status);
//            }
//            SupplierDTO supplierDTO=SupplierDTO.builder().gstNumber(gstin).ownerId(ownerId).supplierName(legalName).address(principalPlace).status(status).state(stateName).stateCode(stateCode).build();
//            System.out.println(supplierDTO.toString());
//            supplierService.saveSupplier(supplierDTO);
//     
//            
//            
//            // ✅ Print to console
//            System.out.println("===== GST RESULT =====");
//            System.out.println("GSTIN: " + gstin);
//            System.out.println("Legal Name: " + legalName);
//            System.out.println("Trade Name: " + tradeName);
//            System.out.println("Effective Date: " + effectiveDate);
//            System.out.println("Constitution: " + constitution);
//            System.out.println("Status: " + status);
//            System.out.println("Taxpayer Type: " + taxpayerType);
//            System.out.println("Principal Place: " + principalPlace);
//
//          
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (driver != null) {
//                driver.quit(); // ✅ Graceful cleanup
//            }
//        }
//    }
//    
//}
