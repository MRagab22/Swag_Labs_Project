package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class Register {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;
    }

    @Test(description = "Register")
    public void register() {
        // 1. Navigate to home page
        driver.get("https://advantageonlineshopping.com/#/");

        // 2. Click user icon
        wait.until(ExpectedConditions.elementToBeClickable(By.id("menuUser"))).click();

        // 3. Bypass loader and click "CREATE NEW ACCOUNT" via JS
        By createNewAccount = By.xpath("//a[text()='CREATE NEW ACCOUNT']");
        wait.until(ExpectedConditions.presenceOfElementLocated(createNewAccount));
        // تأكد أن العنصر مرئي وأن اللودر مختفٍ
        wait.until(ExpectedConditions.visibilityOfElementLocated(createNewAccount));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loader")));
        // ثم نفذ نقر JS
        WebElement link = driver.findElement(createNewAccount);
        js.executeScript("arguments[0].click();", link);

        // 4. Fill in registration form
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("usernameRegisterPage")))
                .sendKeys("mohamed15");
        driver.findElement(By.name("emailRegisterPage")).sendKeys("mohamed@gmail.com");
        driver.findElement(By.name("passwordRegisterPage")).sendKeys("4T5555aa");
        driver.findElement(By.name("confirm_passwordRegisterPage")).sendKeys("4T5555aa");
        driver.findElement(By.name("first_nameRegisterPage")).sendKeys("Mohamed");
        driver.findElement(By.name("last_nameRegisterPage")).sendKeys("Ahmed");
        driver.findElement(By.name("phone_numberRegisterPage")).sendKeys("01010079536");

        Select country = new Select(driver.findElement(By.name("countryListboxRegisterPage")));
        country.selectByVisibleText("Egypt");

        driver.findElement(By.name("cityRegisterPage")).sendKeys("Cairo");
        driver.findElement(By.name("addressRegisterPage")).sendKeys("123 Tahrir St");
        driver.findElement(By.name("state_/_province_/_regionRegisterPage")).sendKeys("Cairo");
        driver.findElement(By.name("postal_codeRegisterPage")).sendKeys("12698");

        // 5. Agree to Terms & Conditions
        WebElement termsCheckbox = driver.findElement(By.name("i_agree"));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }

        // 6. Click "REGISTER"
        driver.findElement(By.id("register_btn")).click();

        // Verify redirect to homepage
        wait.until(ExpectedConditions.urlToBe("https://advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://advantageonlineshopping.com/#/");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
