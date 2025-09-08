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

public class CartTests {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput       = By.id("user-name");
    private final By passwordInput       = By.id("password");
    private final By loginButton         = By.id("login-button");
    private final By inventoryList       = By.className("inventory_list");
    private final By cartLink            = By.className("shopping_cart_link");
    private final By cartList            = By.className("cart_list");
    private final By continueShoppingBtn = By.id("continue-shopping");
    private final String addToCartXPath  =
            "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[contains(text(), 'Add to cart')]";
    private final By removeButton        = By.cssSelector("button.cart_button");
    private final By cartBadge           = By.className("shopping_cart_badge");
    private final By cartQuantity        = By.className("cart_quantity");
    private final By cartItemName        = By.className("inventory_item_name");
    private final By checkoutBtn         = By.id("checkout");
    private final By checkoutFirstName   = By.id("first-name");

    // Constants
    private static final String BASE_URL       = "https://www.saucedemo.com/";
    private static final String STANDARD_USER  = "standard_user";
    private static final String SECRET_SAUCE   = "secret_sauce";
    private static final Duration TIMEOUT      = Duration.ofSeconds(10);

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

        // Precondition: login and wait for inventory page
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
        By locator = By.xpath(String.format(addToCartXPath, productName));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void removeFromCart() {
        wait.until(ExpectedConditions.elementToBeClickable(removeButton)).click();
    }

    private String getCartBadgeCount() {
        try {
            return wait.until(ExpectedConditions
                            .visibilityOfElementLocated(cartBadge))
                    .getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return "0";
        }
    }

    private boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // TC01: Verify cart icon redirects to cart page
    @Test
    public void testCartIconRedirectsToCartPage() {
        driver.findElement(cartLink).click();
        Assert.assertTrue(isElementDisplayed(cartList),
                "TC01: Cart page should be displayed after clicking cart icon");
    }

    // TC03: Verify remove button functionality
    @Test
    public void testRemoveButtonFunctionality() {
        addToCart("Sauce Labs Backpack");
        driver.findElement(cartLink).click();
        removeFromCart();
        Assert.assertEquals(getCartBadgeCount(), "0",
                "TC03: Cart badge should show 0 after removing item");
    }

    // TC05: Verify continue shopping redirects to home page
    @Test
    public void testContinueShoppingRedirectsToHomePage() {
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingBtn)).click();
        Assert.assertTrue(isElementDisplayed(inventoryList),
                "TC05: Home page should be displayed after clicking Continue Shopping");
    }

    // TC07: Verify product quantity display in cart
    @Test
    public void testProductQuantityDisplay() {
        addToCart("Sauce Labs Backpack");
        driver.findElement(cartLink).click();
        WebElement quantity = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cartQuantity));
        Assert.assertEquals(quantity.getText(), "1",
                "TC07: Product quantity should be 1 in cart");
    }

    // TC09: Verify product name consistency between home and cart
    @Test
    public void testProductNameConsistency() {
        addToCart("Sauce Labs Backpack");
        driver.findElement(cartLink).click();
        WebElement name = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cartItemName));
        Assert.assertEquals(name.getText(), "Sauce Labs Backpack",
                "TC09: Product name in cart should match");
    }

    // TC11: Verify checkout button redirects to checkout page
    @Test
    public void testCheckoutButtonRedirects() {
        addToCart("Sauce Labs Backpack");
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.elementToBeClickable(checkoutBtn)).click();
        Assert.assertTrue(wait.until(ExpectedConditions
                        .visibilityOfElementLocated(checkoutFirstName)).isDisplayed(),
                "TC11: Checkout step one page should be displayed");
    }
}
