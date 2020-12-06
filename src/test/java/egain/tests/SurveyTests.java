package egain.tests;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import utility.ExcelUtils;

public class SurveyTests {

	static WebDriver driver = null;
	static DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	static LocalDate myObj = LocalDate.now();
	static String formattedDate;

	@BeforeMethod
	public void open() {
		System.setProperty("webdriver.chrome.driver",
				System.getProperty("user.dir") + "\\src\\test\\java\\drivers\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.navigate().to("http://survey-ui.s3-website-us-east-1.amazonaws.com/");
		myObj = myObj.plusDays(1);
		formattedDate = myFormatObj.format(myObj);
	}

	@Test(dataProvider = "testdata")
	public void sampleTestCaseForEgain(String status, String surveyName, String surveyURL, String email,
			String caseClosure, String activityClosure, String accessibility)
			throws ParseException, InterruptedException {
		driver.findElement(By.xpath("//button[text()='Create New Survey']")).click();
		driver.findElement(By.xpath("//input[./following-sibling::*[contains(.,'" + status + "')]]")).click();
		driver.findElement(By.id("inputName")).sendKeys(surveyName);

		System.out.println("The date is " + formattedDate);

		driver.findElement(By.id("inputDate")).sendKeys(formattedDate);
		driver.findElement(By.id("inputURL")).sendKeys(surveyURL);
		driver.findElement(By.id("inputEmail")).sendKeys(email);
		driver.findElement(By.xpath("//input[./following-sibling::*[contains(.,'" + caseClosure + "')]]")).click();
		driver.findElement(By.xpath("//input[./following-sibling::*[contains(.,'" + activityClosure + "')]]")).click();

		WebElement dropDown = driver.findElement(By.id("inputState"));
		Select element = new Select(dropDown);
		element.selectByVisibleText(accessibility);

		driver.findElement(By.xpath("//button[text()='Submit']")).click();

		WebElement survey = driver
				.findElement(By.xpath("//table[@class='table table-striped']//td[1][text()='" + surveyName + "']"));
		WebElement surveyStatus = driver.findElement(By.xpath("//table//tr[@class='row align-items-start']//td[text()='"
				+ surveyName + "']//..//td//span[text()='Active']"));
		
		Assert.assertTrue(survey.isDisplayed(), "Survey not displayed");
		Assert.assertTrue(surveyStatus.isDisplayed(), "Survey Active not displayed");
	}

	@AfterMethod
	public void close() {
		driver.quit();
	}

	@DataProvider(name = "testdata")
	public Object[][] mtLoginData() throws Exception {
		Object[][] fbdata = ExcelUtils.getDataFromExcel(
				System.getProperty("user.dir") + "\\src\\test\\java\\testdata\\testdata.xlsx", "data");
		return fbdata;
	}
}
