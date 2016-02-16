package com.kuzdowicz.webscrappers.clients;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TaxesPortalPLClient {

	private WebDriver webDriver;

	private final static String TaxesPortalPLClientUrl = " http://www.finanse.mf.gov.pl/web/wp/pp/sprawdzanie-statusu-podmiotu-w-vat";

	public TaxesPortalPLClient() {

		webDriver = new FirefoxDriver();

	}

	public WebElement getWebElement(WebDriver driver, String id) {
		WebElement myDynamicElement = null;
		try {
			myDynamicElement = (new WebDriverWait(driver, 30))
					.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
			return myDynamicElement;
		} catch (TimeoutException ex) {
			return null;
		}
	}

	public void getIsTaxIdFirmAVatInstance(String nip) {

		webDriver.get(TaxesPortalPLClientUrl);

		WebElement wElInput = getWebElement(webDriver, "b-7");

		wElInput.sendKeys(nip);

		WebElement wElSprawdzBtn = getWebElement(webDriver, "b-8");

		wElSprawdzBtn.click();

		WebElement spanWithResponse = getWebElement(webDriver, "caption2_b-3");

		String reponseText = spanWithResponse.getAttribute("textContent");

		System.out.println("-----------------------------------------");
		System.out.println(spanWithResponse.getTagName());
		System.out.println(spanWithResponse.getAttribute("innerHTML"));
		System.out.println(reponseText);

	}

	public static void main(String[] args) {

		TaxesPortalPLClient seleniumClient = new TaxesPortalPLClient();

		String nip = "5272525995";

		seleniumClient.getIsTaxIdFirmAVatInstance(nip);

	}

}
