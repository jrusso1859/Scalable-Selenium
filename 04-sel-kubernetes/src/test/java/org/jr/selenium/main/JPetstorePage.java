package org.jr.selenium.main;

import java.util.Date;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Integration test example.
 */
public class JPetstorePage implements PageProcessor {
	private static Logger logger = LoggerFactory.getLogger(JPetstorePage.class);
    //private static String DRIVER_PATH = "/opt/chrome/chromedriver";  
    private String gridUrl = null;
    
    private RemoteWebDriver driver;       

    public JPetstorePage() {
    	setup();
    }
    
    private void startGridDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
    	chromeOptions.addArguments("--headless",
            "--disable-gpu",
            "--ignore-certificate-errors",
            "--disable-extensions",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--allow-insecure-localhost",
            "allow-running-insecure-content");
        chromeOptions.setCapability("browserVersion", "95.0");
        chromeOptions.setCapability("platformName", "Linux");
        chromeOptions.setAcceptInsecureCerts(true);
        chromeOptions.setCapability("se:name", "Pet Store main page test"); 
        //chromeOptions.setCapability("se:sampleMetadata", "Sample metadata value");
	    logger.info("Starting remote web driver.");
        logger.info("Grid url: {}.", gridUrl);
        try {
            driver = new RemoteWebDriver(new URL(gridUrl), chromeOptions);
        } catch ( MalformedURLException mfUrlE ) {
            logger.error("Error opening grid url: {}: {}", gridUrl, mfUrlE.getMessage());
	    mfUrlE.printStackTrace();
	    }
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
    
    public void storeScreenshot() {
    	logger.debug("Taking screen shot");
        TakesScreenshot ssDriver = (TakesScreenshot) driver;
        File ssFile = ssDriver.getScreenshotAs(OutputType.FILE);
        storeScreenshotToLocal(ssFile);
    }
    
    public void setup() {
        this.gridUrl = String.format("%s/wd/hub", System.getProperty("SEL_GRID_URL"));
        startGridDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));  
        driver.manage().window().maximize();  
    }
    
    public void openPage()
    {
        String baseUrl = "https://petstore.octoperf.com/actions/Catalog.action";
        driver.get(baseUrl);  
        logger.debug("URL: " + driver.getCurrentUrl());  
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
