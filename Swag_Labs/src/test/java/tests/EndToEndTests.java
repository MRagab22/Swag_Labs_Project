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

public class EndToEndTests {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput        = By.id("user-name");
    private final By passwordInput        = By.id("password");
    private final By loginButton          = By.id("login-button");
    private final String addToCartXPath   =
            "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[contains(text(),'Add to cart')]";
    private final By cartLink             = By.className("shopping_cart_link");
    private final By removeButton         = By.cssSelector("button.cart_button");
    private final By checkoutButton       = By.id("checkout");
    private final By firstNameInput       = By.id("first-name");
    private final By lastNameInput        = By.id("last-name");
    private final By postalCodeInput      = By.id("postal-code");
    private final By continueButton       = By.id("continue");
    private final By finishButton         = By.id("finish");
    private final By completeHeader       = By.className("complete-header");
    private final By itemTotalLabel       = By.className("summary_subtotal_label");
    private final By productSortContainer = By.className("product_sort_container");
    private final By cartBadge            = By.className("shopping_cart_badge");

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
    private void login() {
        driver.findElement(usernameInput).sendKeys(STANDARD_USER);
        driver.findElement(passwordInput).sendKeys(SECRET_SAUCE);
        driver.findElement(loginButton).click();
    }

    private void addToCart(String productName) {
        By locator = By.xpath(String.format(addToCartXPath, productName));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void removeFromCart() {
        wait.until(ExpectedConditions.elementToBeClickable(removeButton)).click();
    }

    private void startCheckout() {
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
    }

    private void fillCheckoutInfo(String first, String last, String zip) {
        driver.findElement(firstNameInput).sendKeys(first);
        driver.findElement(lastNameInput).sendKeys(last);
        driver.findElement(postalCodeInput).sendKeys(zip);
        driver.findElement(continueButton).click();
    }

    private void completeCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
    }

    private boolean isCheckoutComplete() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(completeHeader)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private String getCartCount() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
        } catch (TimeoutException e) {
            return "0";
        }
    }

    // E2E01: Complete purchase flow
    @Test
    public void testCompletePurchaseFlow() {
        login();
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        completeCheckout();
        Assert.assertTrue(isCheckoutComplete(),
                "E2E01: Checkout complete page should display 'Thank you for your order!'");
    }

    // E2E09: Purchase after re-login
    @Test
    public void testPurchaseAfterRelogin() {
        login();
        addToCart("Sauce Labs Backpack");
        driver.findElement(By.id("react-burger-menu-btn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();
        login();
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        completeCheckout();
        Assert.assertTrue(isCheckoutComplete(),
                "E2E09: Checkout complete page should display after re-login");
    }

    // E2E03: Purchase with multiple items
    @Test
    public void testPurchaseWithMultipleItems() {
        login();
        addToCart("Sauce Labs Backpack");
        addToCart("Sauce Labs Bike Light");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        String total = wait.until(ExpectedConditions.visibilityOfElementLocated(itemTotalLabel)).getText();
        Assert.assertTrue(total.contains("$39.98"),
                "E2E03: Item total should reflect multiple items");
        completeCheckout();
        Assert.assertTrue(isCheckoutComplete(),
                "E2E03: Checkout complete page should display for multiple items");
    }

    // E2E05: Purchase after sorting
    @Test
    public void testPurchaseAfterSorting() {
        login();
        WebElement sort = wait.until(ExpectedConditions.elementToBeClickable(productSortContainer));
        sort.sendKeys("az");
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        completeCheckout();
        Assert.assertTrue(isCheckoutComplete(),
                "E2E05: Checkout complete page should display after sorting");
    }

    // E2E07: Purchase after removing items
    @Test
    public void testPurchaseAfterRemovingItems() {
        login();
        addToCart("Sauce Labs Backpack");
        addToCart("Sauce Labs Bike Light");
        driver.findElement(cartLink).click();
        removeFromCart();
        Assert.assertEquals(getCartCount(), "1",
                "E2E07: Cart badge should show 1 item after removal");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        completeCheckout();
        Assert.assertTrue(isCheckoutComplete(),
                "E2E07: Checkout complete page should display after removing items");
    }

    // E2E11: Purchase with minimal inputs
    @Test
    public void testPurchaseWithMinimalInputs() {
        login();
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("M", "A", "12345");
        completeCheckout();
        Assert.assertTrue(isCheckoutComplete(),
                "E2E11: Checkout complete page should display with minimal inputs");
    }
}
