package com.rigel.app.serviceimpl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;

public class GSTNumberService {

    private static WebDriver driver;

    public static void openGstBrowser(String gstin) {
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();

            driver.get("https://services.gst.gov.in/services/searchtp");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            // ✅ Wait until overlay disappears
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dimmer-holder")));

            // ✅ Try normal sendKeys
            try {
                WebElement gstField = wait.until(ExpectedConditions.elementToBeClickable(By.name("for_gstin")));
                gstField.clear();
                gstField.sendKeys(gstin);
            } catch (Exception ex) {
                // ✅ Fallback: JavaScript if click intercepted
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("document.getElementById('for_gstin').value='" + gstin + "';");
            }

            System.out.println("GSTIN entered, now solve CAPTCHA manually");

            // 2️⃣ WAIT for user to solve captcha
            Thread.sleep(20000);

            // 3️⃣ CLICK SEARCH BUTTON
            WebElement searchBtn = driver.findElement(By.id("lotsearch"));
            searchBtn.click();

            System.out.println("Search clicked");

            // 4️⃣ WAIT FOR RESULT LOAD
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//strong[contains(text(),'Legal Name of Business')]")
            ));

            // 5️⃣ SCRAPE RESULT (using correct DOM structure)
            String legalName = driver.findElement(By.xpath("//strong[contains(text(),'Legal Name of Business')]/parent::p/following-sibling::p")).getText();
            String tradeName = driver.findElement(By.xpath("//strong[contains(text(),'Trade Name')]/parent::p/following-sibling::p")).getText();
            String effectiveDate = driver.findElement(By.xpath("//strong[contains(text(),'Effective Date of registration')]/parent::p/following-sibling::p")).getText();
            String constitution = driver.findElement(By.xpath("//strong[contains(text(),'Constitution of Business')]/parent::p/following-sibling::p")).getText();
            String status = driver.findElement(By.xpath("//strong[contains(text(),'GSTIN / UIN  Status')]/parent::p/following-sibling::p")).getText();
            String taxpayerType = driver.findElement(By.xpath("//strong[contains(text(),'Taxpayer Type')]/parent::p/following-sibling::p")).getText();
            String principalPlace = driver.findElement(By.xpath("//strong[contains(text(),'Principal Place of Business')]/parent::p/following-sibling::p")).getText();

            // ✅ Print to console
            System.out.println("===== GST RESULT =====");
            System.out.println("GSTIN: " + gstin);
            System.out.println("Legal Name: " + legalName);
            System.out.println("Trade Name: " + tradeName);
            System.out.println("Effective Date: " + effectiveDate);
            System.out.println("Constitution: " + constitution);
            System.out.println("Status: " + status);
            System.out.println("Taxpayer Type: " + taxpayerType);
            System.out.println("Principal Place: " + principalPlace);

          
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit(); // ✅ Graceful cleanup
            }
        }
    }
    
}
