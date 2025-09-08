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

public class CheckoutTests {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput             = By.id("user-name");
    private final By passwordInput             = By.id("password");
    private final By loginButton               = By.id("login-button");
    private final String addToCartXPathTemplate =
            "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[contains(text(), 'Add to cart')]";
    private final By cartLink                  = By.className("shopping_cart_link");
    private final By checkoutBtn               = By.id("checkout");
    private final By firstNameInput            = By.id("first-name");
    private final By lastNameInput             = By.id("last-name");
    private final By postalCodeInput           = By.id("postal-code");
    private final By continueBtn               = By.id("continue");
    private final By finishBtn                 = By.id("finish");
    private final By backToProductsBtn         = By.id("back-to-products");
    private final By inventoryList             = By.className("inventory_list");
    private final By cartList                  = By.className("cart_list");
    private final By completeHeader            = By.className("complete-header");
    private final By errorMessage              = By.cssSelector("[data-test='error']");
    private final By itemTotalLabel            = By.className("summary_subtotal_label");

    // Constants
    private static final String BASE_URL      = "https://www.saucedemo.com/";
    private static final String STANDARD_USER = "standard_user";
    private static final String SECRET_SAUCE  = "secret_sauce";
    private static final Duration TIMEOUT     = Duration.ofSeconds(10);

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

        // Precondition: login
        login(STANDARD_USER, SECRET_SAUCE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Helper methods

    private void login(String user, String pass) {
        driver.findElement(usernameInput).sendKeys(user);
        driver.findElement(passwordInput).sendKeys(pass);
        driver.findElement(loginButton).click();
    }

    private void addToCart(String productName) {
        By locator = By.xpath(String.format(addToCartXPathTemplate, productName));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void startCheckout() {
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.elementToBeClickable(checkoutBtn)).click();
    }

    private void fillCheckoutInfo(String first, String last, String zip) {
        driver.findElement(firstNameInput).sendKeys(first);
        driver.findElement(lastNameInput).sendKeys(last);
        driver.findElement(postalCodeInput).sendKeys(zip);
        driver.findElement(continueBtn).click();
    }

    private boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    private String getErrorText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
        } catch (TimeoutException e) {
            return "";
        }
    }

    // TC01: Valid checkout info redirects to overview
    @Test
    public void testValidCheckoutInfoRedirectsToOverview() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        Assert.assertTrue(isDisplayed(cartList),
                "TC01: Checkout Overview page should be displayed after valid input");
    }

    // TC16: Single-character checkout info is accepted
    @Test
    public void testSingleCharacterCheckoutInfo() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("M", "A", "12345");
        Assert.assertTrue(isDisplayed(cartList),
                "TC16: Checkout Overview page should be displayed with single-character inputs");
    }

    // TC35: Finish button completes checkout
    @Test
    public void testFinishButtonFunctionality() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        driver.findElement(finishBtn).click();
        Assert.assertTrue(isDisplayed(completeHeader),
                "TC35: Checkout Complete page should be displayed after clicking Finish");
    }

    // TC03: Empty first name shows error
    @Test
    public void testEmptyFirstNameError() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("", "Ali", "12345");
        Assert.assertTrue(getErrorText().contains("First Name is required"),
                "TC03: Error message should be displayed for empty First Name");
    }

    // TC06: Empty zip code shows error
    @Test
    public void testEmptyZipCodeError() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "");
        Assert.assertTrue(getErrorText().contains("Postal Code is required"),
                "TC06: Error message should be displayed for empty Zip Code");
    }

    // TC20: Item total displays correct price
    @Test
    public void testItemTotalDisplayed() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        WebElement total = wait.until(
                ExpectedConditions.visibilityOfElementLocated(itemTotalLabel));
        Assert.assertTrue(total.getText().contains("$29.99"),
                "TC20: Item total should display $29.99 in Checkout Overview");
    }

    // TC30: Back to home after checkout complete
    @Test
    public void testBackToHomeAfterCheckout() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        driver.findElement(finishBtn).click();
        driver.findElement(backToProductsBtn).click();
        Assert.assertTrue(isDisplayed(inventoryList),
                "TC30: Home page should be displayed after clicking Back to Home");
    }
}
