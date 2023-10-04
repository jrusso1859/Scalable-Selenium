package org.jr.selenium.main;

public interface PageProcessor {
	public void openPage();
    public String getPageTitle();
    public void storeScreenshot();
    public void closeSessions();
}
