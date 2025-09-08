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

public class ProductDetailTests_2 {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput       = By.id("user-name");
    private final By passwordInput       = By.id("password");
    private final By loginButton         = By.id("login-button");
    private final By inventoryList       = By.className("inventory_list");
    private final By productLinkTemplate = By.xpath("//a[contains(., '%s')]");
    private final By addToCartButton     = By.cssSelector("button.btn.btn_primary.btn_small.btn_inventory");
    private final By removeButton        = By.cssSelector("button.btn.btn_secondary.btn_small.btn_inventory");
    private final By backToProductsBtn   = By.id("back-to-products");
    private final By productImage        = By.className("inventory_details_img");
    private final By productPrice        = By.className("inventory_details_price");
    private final By cartBadge           = By.className("shopping_cart_badge");

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
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation", "enable-logging"));
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

    private void selectProduct(String productName) {
        By locator = By.xpath(String.format(productLinkTemplate.toString().replace("By.xpath: ", ""), productName));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void clickAddToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
    }

    private String getCartBadgeCount() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return "0";
        }
    }

    private boolean isRemoveButtonDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(removeButton)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    // TC01: Verify 'Add to Cart' button clickability on detail page
    @Test
    public void testAddToCartButtonClickability() {
        selectProduct("Sauce Labs Backpack");
        clickAddToCart();
        Assert.assertTrue(isRemoveButtonDisplayed(),
                "TC01: Add to Cart button should change to Remove");
    }

    // TC02: Verify 'Add to Cart' toggles to 'Remove'
    @Test
    public void testAddToCartButtonChangesToRemove() {
        selectProduct("Sauce Labs Backpack");
        clickAddToCart();
        Assert.assertTrue(isRemoveButtonDisplayed(),
                "TC02: Add to Cart button should change to Remove after clicking");
    }

    // TC03: Verify cart badge updates after adding product
    @Test
    public void testCartIconUpdatesAfterAddingProduct() {
        selectProduct("Sauce Labs Backpack");
        clickAddToCart();
        Assert.assertEquals(getCartBadgeCount(), "1",
                "TC03: Cart badge should show 1 item after adding product");
    }

    // TC04: Verify 'Back to Products' navigation
    @Test
    public void testBackToProductsButton() {
        selectProduct("Sauce Labs Backpack");
        wait.until(ExpectedConditions.elementToBeClickable(backToProductsBtn)).click();
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList)).isDisplayed(),
                "TC04: Home page should be displayed after clicking Back to Products");
    }

    // TC06: Verify product image visibility on detail page
    @Test
    public void testProductImageVisibility() {
        selectProduct("Sauce Labs Backpack");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(productImage)).isDisplayed(),
                "TC06: Product image should be visible on product detail page");
    }

    // TC08: Verify product price consistency on detail page
    @Test
    public void testProductPriceConsistency() {
        selectProduct("Sauce Labs Backpack");
        String price = wait.until(ExpectedConditions.visibilityOfElementLocated(productPrice)).getText();
        Assert.assertEquals(price, "$29.99",
                "TC08: Product price should be consistent on product detail page");
    }
}
