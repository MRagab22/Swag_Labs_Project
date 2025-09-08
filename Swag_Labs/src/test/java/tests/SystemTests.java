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

public class SystemTests {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput               = By.id("user-name");
    private final By passwordInput               = By.id("password");
    private final By loginButton                 = By.id("login-button");
    private final String addToCartXPathTemplate  = "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[contains(text(), 'Add to cart')]";
    private final By cartLink                    = By.className("shopping_cart_link");
    private final By errorMessage                = By.cssSelector("[data-test='error']");
    private final By productSort                 = By.className("product_sort_container");
    private final By checkoutButton              = By.id("checkout");
    private final By firstNameInput              = By.id("first-name");
    private final By lastNameInput               = By.id("last-name");
    private final By postalCodeInput             = By.id("postal-code");
    private final By continueButton              = By.id("continue");
    private final By hamburgerMenu               = By.id("react-burger-menu-btn");
    private final By allItemsLink                = By.id("inventory_sidebar_link");

    // Constants
    private static final String BASE_URL         = "https://www.saucedemo.com/";
    private static final String STANDARD_USER    = "standard_user";
    private static final String SECRET_SAUCE     = "secret_sauce";
    private static final Duration TIMEOUT        = Duration.ofSeconds(10);

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Disable password manager prompts
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        // Hide Selenium automation flags
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
    private void attemptLogin(String user, String pass) {
        driver.findElement(usernameInput).sendKeys(user);
        driver.findElement(passwordInput).sendKeys(pass);
        driver.findElement(loginButton).click();
    }

    private void addToCart(String productName) {
        By locator = By.xpath(String.format(addToCartXPathTemplate, productName));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private boolean isErrorDisplayed(String expected) {
        try {
            WebElement err = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(errorMessage));
            String actualText = err.getText();
            System.out.println("SYS03: Actual error message: " + actualText);
            return err.isDisplayed() && actualText.contains(expected);
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("SYS03: Error message element not found or not visible");
            return false;
        }
    }

    private boolean isOnLoginPage() {
        try {
            return wait.until(ExpectedConditions
                            .visibilityOfElementLocated(usernameInput))
                    .isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    private void openSidebar() {
        wait.until(ExpectedConditions.elementToBeClickable(hamburgerMenu)).click();
    }

    // SYS03: Verify invalid inputs rejected in Checkout
    @Test
    public void testInvalidCheckoutInput() {
        attemptLogin(STANDARD_USER, SECRET_SAUCE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(productSort));
        addToCart("Sauce Labs Backpack");
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
        driver.findElement(firstNameInput).sendKeys("Mohamed");
        driver.findElement(lastNameInput).sendKeys("Ali");
        driver.findElement(postalCodeInput).sendKeys(""); // Empty ZIP to trigger required field error
        driver.findElement(continueButton).click();
        Assert.assertTrue(isErrorDisplayed("Error: Postal Code is required"),
                "SYS03: Error message should appear for missing ZIP code");
    }

    // SYS04: Verify sidebar functionality across pages
    @Test
    public void testSidebarNavigation() {
        attemptLogin(STANDARD_USER, SECRET_SAUCE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(productSort));
        // Open sidebar in Inventory and click All Items
        openSidebar();
        wait.until(ExpectedConditions.elementToBeClickable(allItemsLink)).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),
                "SYS04: Sidebar should navigate to Inventory from Inventory page");
        // Go to Cart
        driver.findElement(cartLink).click();
        // Open sidebar in Cart and click All Items
        openSidebar();
        wait.until(ExpectedConditions.elementToBeClickable(allItemsLink)).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),
                "SYS04: Sidebar should navigate to Inventory from Cart page");
    }

    // SYS08: Verify error message UI on invalid login
    @Test
    public void testInvalidLoginError() {
        attemptLogin(STANDARD_USER, "wrong_password");
        Assert.assertTrue(isErrorDisplayed("Epic sadface: Username and password do not match"),
                "SYS08: Error message should be displayed for invalid login credentials");
        Assert.assertTrue(isOnLoginPage(),
                "SYS08: User should remain on login page after invalid login");
    }

    // SYS10: Verify SQL injection attempt in login
    @Test
    public void testSqlInjectionLogin() {
        String maliciousInput = "' OR '1'='1";
        attemptLogin(maliciousInput, maliciousInput);
        Assert.assertTrue(isErrorDisplayed("Epic sadface: Username and password do not match"),
                "SYS10: Error message should be displayed for SQL injection attempt");
        Assert.assertTrue(isOnLoginPage(),
                "SYS10: SQL injection attempt should not grant access");
    }
}