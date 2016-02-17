package com.kuzdowicz.webscrappers.clients;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.RequestHeaders;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;

import net.anthavio.phanbedder.Phanbedder;

public class TaxesPortalPLClient {

	private final static Logger logger = Logger.getLogger(TaxesPortalPLClient.class);

	private final static String TaxesPortalPLClientUrl = "https://pfr.mf.gov.pl/?link=VAT&;";

	private final static long PAGE_LOAD_TIMEOUT_IN_SECONDS = 60;

	private final static long WEB_DRIVER_WAIT = 60;

	private static WebElement getWebElementWiatIfNotPresent(final WebDriver driver, final String id) {

		WebElement myDynamicElement = null;
		try {
			myDynamicElement = (new WebDriverWait(driver, WEB_DRIVER_WAIT))
					.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
			return myDynamicElement;

		} catch (TimeoutException ex) {
			return null;
		}
	}

	private static WebElement getWebElementWiatIfNotVissable(final WebDriver driver, final String id) {

		WebElement myDynamicElement = null;
		try {
			myDynamicElement = (new WebDriverWait(driver, WEB_DRIVER_WAIT))
					.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
			return myDynamicElement;

		} catch (TimeoutException ex) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static WebDriver getWebDriverImplWithJBrowser() {

		System.setProperty("jbd.warnconsole", "false");

		return new JBrowserDriver(Settings.builder().timezone(Timezone.EUROPE_WARSAW).userAgent(UserAgent.CHROME)
				.ignoreDialogs(true).requestHeaders(RequestHeaders.CHROME).build());

	}

	private static WebDriver getWebDriverImplWithPhantomJs() {

		File phantomjs = Phanbedder.unpack();
		DesiredCapabilities dcaps = new DesiredCapabilities();
		dcaps.setJavascriptEnabled(true);
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());

		PhantomJSDriver phantomJSDriver = new PhantomJSDriver(dcaps);
		phantomJSDriver.setLogLevel(Level.OFF);

		return phantomJSDriver;

	}

	public static String fetchVatStatus(final String taxId) {

		logger.info("fetchVatStatus()");

		boolean argumentNIPjestPrawidlowy = StringUtils.length(taxId) == 10 && StringUtils.isNumeric(taxId);
		boolean argumentNIPJestNieprawidlowy = !argumentNIPjestPrawidlowy;

		String reponseText = "";
		WebDriver webDriver = null;

		try {

			if (argumentNIPJestNieprawidlowy) {

				String alert = "tax id is invalid";
				return alert;

			}

			webDriver = getWebDriverImplWithPhantomJs();

			webDriver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			webDriver.manage().timeouts().setScriptTimeout(PAGE_LOAD_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

			// -------------------------------------------

			webDriver.get(TaxesPortalPLClientUrl);

			WebElement wElInput = getWebElementWiatIfNotPresent(webDriver, "b-7");

			wElInput.sendKeys(taxId);

			WebElement wElCheckBtn = getWebElementWiatIfNotPresent(webDriver, "b-8");

			wElCheckBtn.click();

			WebElement spanWithResponse = getWebElementWiatIfNotVissable(webDriver, "caption2_b-3");

			reponseText = spanWithResponse.getAttribute("textContent");

		} catch (Exception e) {

			logger.debug(e);

			reponseText = "service is unavailable";

		} finally {

			if (webDriver != null) {

				logger.debug("close webDriver");

				webDriver.quit();
			}

		}

		return reponseText;

	}

	public static void main(final String[] args) {

		String taxId = "8732789712";
		String result = TaxesPortalPLClient.fetchVatStatus(taxId);
		System.out.println(result);

	}

}
