package org.jr.selenium.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTest {
	
	private static PageProcessor mainPage = null;
	private static Logger logger = LoggerFactory.getLogger(LocalTest.class);
    
    @BeforeAll
    static void setup() {
        logger.info("LocalTest -- Setup");
		mainPage = new JPetstorePage();
    }

    @Test
	public void jPetstoreMainPageTest() {
        logger.debug("Opening page.");
		mainPage.openPage();
        try {
            Assertions.assertEquals("JPetStore Demo", mainPage.getPageTitle());
        } catch (AssertionFailedError afe) {
            throw afe;
        } finally {
            mainPage.storeScreenshot();
        }
        
	}
    
    @AfterAll
    static void shutDown() {
        logger.info("Complete.");
		mainPage.closeSessions();
    }        
}
