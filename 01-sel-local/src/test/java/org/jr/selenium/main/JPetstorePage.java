package org.jr.selenium.main;

import java.util.Date;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;

/**
 * Integration test example.
 */

public class JPetstorePage implements PageProcessor {
	private static Logger logger = LoggerFactory.getLogger(JPetstorePage.class);
    private WebDriver driver;       

    public JPetstorePage() {
    	setup();
    }
    
    private void startDriverWithOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);  
    }

    private String getTestCategory() {
        String methodName = Thread.currentThread().getStackTrace()[5].getMethodName();
        logger.debug("Category: " + methodName);
        return methodName;
    }
    
    private String makeFileName() {
        Date now = new Date();
        String pictureGroup = String.format("pictures/%s/%s", 
            getTestCategory(), 
            new SimpleDateFormat("yyMMdd").format(now));
        String minsec = new SimpleDateFormat("HHmmss").format(now);
        new File(pictureGroup).mkdirs();
        return String.format("./%s/%s.png", pictureGroup, minsec);
    }
        
    private void storeScreenshotToLocal(File screenShot) {
        String fileName = makeFileName();
    	logger.info("Storing screen shot to "+ fileName);
        try {
            FileUtils.copyFile(screenShot, new File(fileName));    
        } catch ( IOException ioe ) {
          logger.error("ERROR saving screenshot: " + ioe.getMessage());
        }
    }
    
    public void storeScreenshot() {
    	logger.debug("Taking screen shot");
        TakesScreenshot ssDriver = (TakesScreenshot) driver;
        File ssFile = ssDriver.getScreenshotAs(OutputType.FILE);
        storeScreenshotToLocal(ssFile);
    }
    
    public void setup() {
        startDriverWithOptions();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));  
        driver.manage().window().maximize();  
    }
    

    public void openPage()
    {
        String baseUrl = "https://petstore.octoperf.com/actions/Catalog.action";
        driver.get(baseUrl);  
        logger.info("URL: " + driver.getCurrentUrl());  
    }
    
    public String getPageTitle() {
    	return driver.getTitle();
    }
    
    public void closeSessions() {
        if (driver != null ) {
            driver.quit();
        }
    }
}
