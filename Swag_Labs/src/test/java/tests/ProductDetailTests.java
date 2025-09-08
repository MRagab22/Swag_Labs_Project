package tests;

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

public class ProductDetailTests {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        // TestNG will generate reports automatically in the test-output directory
    }

    @BeforeMethod
    public void setUp() {
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\user\\Downloads\\chromedriver-win64\\chromedriver.exe");

        // Configure ChromeOptions
        ChromeOptions options = new ChromeOptions();

        // 1. Disable Chrome's built-in password manager entirely
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        // 2. Suppress the “Chrome is being controlled by automated test software” infobar
        options.setExperimentalOption("excludeSwitches",
                Arrays.asList("enable-automation", "enable-logging"));
        options.setExperimentalOption("useAutomationExtension", false);

        // 3. Disable notifications and run in incognito
        options.addArguments("--disable-notifications");
        options.addArguments("--incognito");

        // Initialize ChromeDriver with configured options
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Navigate to SauceDemo login page
        driver.get("https://www.saucedemo.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Perform login
        login("standard_user", "secret_sauce");

        // If a JS alert appears, accept it
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ignored) {
        }

        // If an HTML modal appears (e.g., “Change your password”), close it
        try {
            WebElement closeBtn = driver.findElement(By.cssSelector(".modal__close"));
            if (closeBtn.isDisplayed()) {
                closeBtn.click();
            }
        } catch (NoSuchElementException ignored) {
        }

        // Verify products page is displayed
        Assert.assertTrue(isProductsPageDisplayed(), "Login failed or products page not displayed");
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }

    // Helpers

    private void login(String username, String password) {
        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    private boolean isProductsPageDisplayed() {
        try {
            return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.className("inventory_list"))).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void selectProduct(String productName) {
        By productLink = By.xpath("//a[contains(., '" + productName + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(productLink)).click();
    }

    private boolean isProductDetailsDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_name"))).isDisplayed()
                    && wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_desc"))).isDisplayed()
                    && wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_price"))).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private String getProductName() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_name"))).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String getProductDescription() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_desc"))).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String getProductPrice() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_price"))).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private void addToCart() {
        By addToCartButton = By.cssSelector("button.btn.btn_primary.btn_small.btn_inventory");
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
    }

    private void removeFromCart() {
        By removeFromCartButton = By.cssSelector("button.btn.btn_secondary.btn_small.btn_inventory");
        wait.until(ExpectedConditions.elementToBeClickable(removeFromCartButton)).click();
    }

    private String getCartBadgeCount() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge"))).getText();
        } catch (Exception e) {
            return "0";
        }
    }

    private void clickBackToProducts() {
        By backToProductsButton = By.id("back-to-products");
        wait.until(ExpectedConditions.elementToBeClickable(backToProductsButton)).click();
    }

    // Test Cases

    @Test
    public void testTC01VerifyBackpackDetails() {
        selectProduct("Sauce Labs Backpack");
        Assert.assertTrue(isProductDetailsDisplayed(), "TC01: Product details should be displayed");
        Assert.assertEquals(getProductName(), "Sauce Labs Backpack", "TC01: Product name does not match");
        clickBackToProducts();
    }

    @Test
    public void testTC02VerifyBikeLightDetails() {
        selectProduct("Sauce Labs Bike Light");
        Assert.assertTrue(isProductDetailsDisplayed(), "TC02: Product details should be displayed");
        Assert.assertEquals(getProductName(), "Sauce Labs Bike Light", "TC02: Product name does not match");
        clickBackToProducts();
    }

    @Test
    public void testTC03VerifyBoltTShirtDetails() {
        selectProduct("Sauce Labs Bolt T-Shirt");
        Assert.assertTrue(isProductDetailsDisplayed(), "TC03: Product details should be displayed");
        Assert.assertEquals(getProductName(), "Sauce Labs Bolt T-Shirt", "TC03: Product name does not match");
        clickBackToProducts();
    }

    @Test
    public void testTC04VerifyFleeceJacketDetails() {
        selectProduct("Sauce Labs Fleece Jacket");
        Assert.assertTrue(isProductDetailsDisplayed(), "TC04: Product details should be displayed");
        Assert.assertEquals(getProductName(), "Sauce Labs Fleece Jacket", "TC04: Product name does not match");
        clickBackToProducts();
    }

    @Test
    public void testTC05VerifyOnesieDetails() {
        selectProduct("Sauce Labs Onesie");
        Assert.assertTrue(isProductDetailsDisplayed(), "TC05: Product details should be displayed");
        Assert.assertEquals(getProductName(), "Sauce Labs Onesie", "TC05: Product name does not match");
        clickBackToProducts();
    }

    @Test
    public void testTC06VerifyRedTShirtDetails() {
        selectProduct("Test.allTheThings() T-Shirt (Red)");
        Assert.assertTrue(isProductDetailsDisplayed(), "TC06: Product details should be displayed");
        Assert.assertEquals(getProductName(), "Test.allTheThings() T-Shirt (Red)", "TC06: Product name does not match");
        clickBackToProducts();
    }

    @Test
    public void testTC07AddBackpackToCart() {
        selectProduct("Sauce Labs Backpack");
        addToCart();
        Assert.assertEquals(getCartBadgeCount(), "1", "TC07: Cart badge should show 1 item");
        clickBackToProducts();
    }

    @Test
    public void testTC08AddBikeLightToCart() {
        selectProduct("Sauce Labs Bike Light");
        addToCart();
        Assert.assertEquals(getCartBadgeCount(), "1", "TC08: Cart badge should show 1 item");
        clickBackToProducts();
    }

    @Test
    public void testTC09AddBoltTShirtToCart() {
        selectProduct("Sauce Labs Bolt T-Shirt");
        addToCart();
        Assert.assertEquals(getCartBadgeCount(), "1", "TC09: Cart badge should show 1 item");
        clickBackToProducts();
    }

    @Test
    public void testTC10RemoveBackpackFromCart() {
        selectProduct("Sauce Labs Backpack");
        addToCart();
        removeFromCart();
        Assert.assertEquals(getCartBadgeCount(), "0", "TC10: Cart badge should show 0 items");
        clickBackToProducts();
    }

    @Test
    public void testTC11RemoveBikeLightFromCart() {
        selectProduct("Sauce Labs Bike Light");
        addToCart();
        removeFromCart();
        Assert.assertEquals(getCartBadgeCount(), "0", "TC11: Cart badge should show 0 items");
        clickBackToProducts();
    }

    @Test
    public void testTC12BackToProductsFromBackpack() {
        selectProduct("Sauce Labs Backpack");
        clickBackToProducts();
        Assert.assertTrue(isProductsPageDisplayed(), "TC12: Should return to products page");
    }

    @Test
    public void testTC13BackToProductsFromBikeLight() {
        selectProduct("Sauce Labs Bike Light");
        clickBackToProducts();
        Assert.assertTrue(isProductsPageDisplayed(), "TC13: Should return to products page");
    }

    @Test
    public void testTC14VerifyBackpackPrice() {
        selectProduct("Sauce Labs Backpack");
        String price = getProductPrice();
        Assert.assertTrue(price.contains("$"), "TC14: Price should be displayed with $");
        clickBackToProducts();
    }

    @Test
    public void testTC15VerifyBikeLightDescription() {
        selectProduct("Sauce Labs Bike Light");
        String description = getProductDescription();
        Assert.assertFalse(description.isEmpty(), "TC15: Description should not be empty");
        clickBackToProducts();
    }
}
