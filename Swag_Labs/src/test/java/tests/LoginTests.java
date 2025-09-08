package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginTests {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput  = By.id("user-name");
    private final By passwordInput  = By.id("password");
    private final By loginButton    = By.id("login-button");
    private final By inventoryList  = By.className("inventory_list");
    private final By errorMessage   = By.cssSelector("[data-test='error']");

    // Constants
    private static final String BASE_URL           = "https://www.saucedemo.com/";
    private static final String TITLE              = "Swag Labs";
    private static final String PLACEHOLDER_USER   = "Username";
    private static final String PLACEHOLDER_PASS   = "Password";
    private static final Duration TIMEOUT          = Duration.ofSeconds(10);

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();


        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);


        options.setExperimentalOption("excludeSwitches",
                Arrays.asList("enable-automation", "enable-logging"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-notifications", "--incognito");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get(BASE_URL);

        wait = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Helper methods
    private void login(String username, String password) {
        driver.findElement(usernameInput).sendKeys(username);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginButton).click();
    }

    private String getErrorText() {
        try {
            return wait.until(ExpectedConditions
                            .visibilityOfElementLocated(errorMessage))
                    .getText();
        } catch (TimeoutException e) {
            return "";
        }
    }

    private boolean isProductsPageDisplayed() {
        try {
            return wait.until(ExpectedConditions
                            .visibilityOfElementLocated(inventoryList))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    // TC01: Verify successful login for standard user
    @Test
    public void testValidLoginStandardUser() {
        login("standard_user", "secret_sauce");
        Assert.assertTrue(isProductsPageDisplayed(),
                "TC01: Home page should be displayed after valid login.");
    }

    // TC02: Verify login button is enabled on page load
    @Test
    public void testLoginButtonIsEnabled() {
        Assert.assertTrue(driver.findElement(loginButton).isEnabled(),
                "TC02: Login button should be enabled on page load.");
    }

    // TC43: Verify presence of placeholder texts
    @Test
    public void testPlaceholdersPresence() {
        String userPl = driver.findElement(usernameInput).getAttribute("placeholder");
        String passPl = driver.findElement(passwordInput).getAttribute("placeholder");
        Assert.assertEquals(userPl, PLACEHOLDER_USER,
                "TC43: Username placeholder should be 'Username'.");
        Assert.assertEquals(passPl, PLACEHOLDER_PASS,
                "TC43: Password placeholder should be 'Password'.");
    }

    // TC55: Verify the login page title
    @Test
    public void testLoginPageTitle() {
        Assert.assertEquals(driver.getTitle(), TITLE,
                "TC55: Page title should be 'Swag Labs'.");
    }

    // DataProvider for invalid login scenarios
    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLoginData() {
        return new Object[][] {
                {"standard_user",    "",               "Password is required"},
                {"",                 "secret_sauce",   "Username is required"},
                {"invalid_user",     "invalid_pass",   "Username and password do not match"},
                {"locked_out_user",  "secret_sauce",   "Sorry, this user has been locked out"}
        };
    }

    // TC46/47/50/52: Invalid login scenarios using data-driven approach
    @Test(dataProvider = "invalidLogins")
    public void testInvalidLoginScenarios(String user, String pass, String expectedError) {
        login(user, pass);
        String actualError = getErrorText();
        Assert.assertTrue(actualError.contains(expectedError),
                String.format("Expected error '%s' but got '%s'", expectedError, actualError));
    }
}
