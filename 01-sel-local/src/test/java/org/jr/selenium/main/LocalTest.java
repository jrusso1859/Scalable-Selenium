package org.jr.selenium.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class LocalTest {

    private static PageProcessor mainPage = null;

    @BeforeAll
    static void setup() {
        mainPage = new JPetstorePage();
    }

    @Test
    public void jPetstoreMainPageTest() {
    mainPage.openPage();
        Assertions.assertEquals("JPetStore Demo", mainPage.getPageTitle());
        mainPage.storeScreenshot();
    }

    @AfterAll
    static void shutDown() {
        mainPage.closeSessions();
    }
}
