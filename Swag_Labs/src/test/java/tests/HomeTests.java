package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeTests {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput    = By.id("user-name");
    private final By passwordInput    = By.id("password");
    private final By loginButton      = By.id("login-button");
    private final By inventoryList    = By.className("inventory_list");
    private final By sortContainer    = By.className("product_sort_container");
    private final By productNames     = By.className("inventory_item_name");
    private final By addToCartBtn     = By.id("add-to-cart-sauce-labs-backpack");
    private final By removeBtn        = By.id("remove-sauce-labs-backpack");
    private final By cartBadge        = By.className("shopping_cart_badge");
    private final By productDetail    = By.className("inventory_details");

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

    private WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // TC03: Verify sorting products A to Z works correctly
    @Test
    public void testNameAToZSorting() {
        Select sortDropdown = new Select(waitForVisibility(sortContainer));
        sortDropdown.selectByValue("az");  // TC03.1
        List<WebElement> names = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(productNames)
        );
        Assert.assertEquals(names.get(0).getText(), "Sauce Labs Backpack",
                "First product should be 'Sauce Labs Backpack' after A to Z sorting"); // TC03.2
    }

    // TC12: Verify sorting products Z to A works correctly
    @Test
    public void testNameZToASorting() {
        Select sortDropdown = new Select(waitForVisibility(sortContainer));
        sortDropdown.selectByValue("za");  // TC12.1
        List<WebElement> names = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(productNames)
        );
        Assert.assertEquals(names.get(0).getText(), "Test.allTheThings() T-Shirt (Red)",
                "First product should be 'Test.allTheThings() T-Shirt (Red)' after Z to A sorting"); // TC12.2
    }

    // TC13: Verify sorting products Price (High to Low) works correctly
    @Test
    public void testPriceHighToLowSorting() {
        Select sortDropdown = new Select(waitForVisibility(sortContainer));
        sortDropdown.selectByValue("hilo");  // TC13.1
        List<WebElement> names = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(productNames)
        );
        Assert.assertEquals(names.get(0).getText(), "Sauce Labs Fleece Jacket",
                "First product should be 'Sauce Labs Fleece Jacket' after High to Low sorting"); // TC13.2
    }

    // TC14: Verify sorting products Price (Low to High) works correctly
    @Test
    public void testPriceLowToHighSorting() {
        Select sortDropdown = new Select(waitForVisibility(sortContainer));
        sortDropdown.selectByValue("lohi");  // TC14.1
        List<WebElement> names = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(productNames)
        );
        Assert.assertEquals(names.get(0).getText(), "Sauce Labs Onesie",
                "First product should be 'Sauce Labs Onesie' after Low to High sorting"); // TC14.2
    }

    // TC15: Verify "Add to Cart" functionality for product 1
    @Test
    public void testAddToCartProduct1() {
        driver.findElement(addToCartBtn).click();
        Assert.assertTrue(driver.findElement(removeBtn).isDisplayed(),
                "'Remove' button should be visible after adding product to cart"); // TC15.1
    }

    // TC21: Verify "Remove" functionality for product 1
    @Test
    public void testRemoveProduct1() {
        driver.findElement(addToCartBtn).click();
        driver.findElement(removeBtn).click();
        Assert.assertTrue(driver.findElement(addToCartBtn).isDisplayed(),
                "'Add to Cart' button should be visible after removing product"); // TC21.1
    }

    // TC27: Verify product name navigation for product 1
    @Test
    public void testProductNameNavigationProduct1() {
        driver.findElement(productNames).click();
        Assert.assertTrue(waitForVisibility(productDetail).isDisplayed(),
                "Product detail page should be displayed after clicking product name"); // TC27.1
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory-item.html"),
                "URL should contain '/inventory-item.html'"); // TC27.2
    }

    // TC46: Verify cart counter after adding product
    @Test
    public void testCartCounterAfterAdding() {
        driver.findElement(addToCartBtn).click();
        String badgeCount = waitForVisibility(cartBadge).getText();
        Assert.assertEquals(badgeCount, "1",
                "Cart badge should display '1' after adding one product"); // TC46.1
    }

    // TC47: Verify cart counter after removing product
    @Test
    public void testCartCounterAfterRemoving() {
        driver.findElement(addToCartBtn).click();
        driver.findElement(removeBtn).click();
        List<WebElement> badge = driver.findElements(cartBadge);
        Assert.assertTrue(badge.isEmpty(),
                "Cart badge should not be visible after removing product"); // TC47.1
    }
}
