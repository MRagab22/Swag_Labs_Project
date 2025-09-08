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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IntegrationTests {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput               = By.id("user-name");
    private final By passwordInput               = By.id("password");
    private final By loginButton                 = By.id("login-button");
    private final String addToCartXPathTemplate  =
            "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[contains(text(), 'Add to cart')]";
    private final By cartLink                    = By.className("shopping_cart_link");
    private final By cartBadge                   = By.className("shopping_cart_badge");
    private final By inventoryItemName           = By.className("inventory_item_name");
    private final By inventoryItemPrice          = By.className("inventory_item_price");
    private final By checkoutButton              = By.id("checkout");
    private final By firstNameInput              = By.id("first-name");
    private final By lastNameInput               = By.id("last-name");
    private final By postalCodeInput             = By.id("postal-code");
    private final By continueButton              = By.id("continue");
    private final By finishButton                = By.id("finish");
    private final By backToProductsBtn           = By.id("back-to-products");
    private final By productSort                 = By.className("product_sort_container");
    private final By completeHeader              = By.className("complete-header");

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

        // Precondition: login and wait for products page
        login(STANDARD_USER, SECRET_SAUCE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(productSort));
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

    private String getCartBadgeCount() {
        try {
            return wait.until(ExpectedConditions
                            .visibilityOfElementLocated(cartBadge))
                    .getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return "0";
        }
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

    private boolean isCheckoutCompleteDisplayed() {
        try {
            return wait.until(ExpectedConditions
                            .visibilityOfElementLocated(completeHeader))
                    .isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    private List<String> getCartProductNames() {
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartLink));
        List<WebElement> items = driver.findElements(inventoryItemName);
        return items.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    // INT01: Add product to cart and verify
    @Test
    public void testAddProductToCart() {
        addToCart("Sauce Labs Backpack");
        Assert.assertEquals(getCartBadgeCount(), "1",
                "INT01: Cart badge should show 1 item");
        driver.findElement(cartLink).click();
        String name = wait.until(ExpectedConditions
                .visibilityOfElementLocated(inventoryItemName)).getText();
        Assert.assertEquals(name, "Sauce Labs Backpack",
                "INT01: Product in cart should be Sauce Labs Backpack");
    }

    // INT03: Product details persist into checkout
    @Test
    public void testProductDetailsInCheckout() {
        addToCart("Sauce Labs Backpack");
        driver.findElement(cartLink).click();
        String cartName  = wait.until(ExpectedConditions
                .visibilityOfElementLocated(inventoryItemName)).getText();
        String cartPrice = wait.until(ExpectedConditions
                .visibilityOfElementLocated(inventoryItemPrice)).getText();

        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");

        String chkName  = wait.until(ExpectedConditions
                .visibilityOfElementLocated(inventoryItemName)).getText();
        String chkPrice = wait.until(ExpectedConditions
                .visibilityOfElementLocated(inventoryItemPrice)).getText();

        Assert.assertEquals(chkName, cartName,
                "INT03: Product name should match in checkout");
        Assert.assertEquals(chkPrice, cartPrice,
                "INT03: Product price should match in checkout");
    }

    // INT05: Cart persists after re-login
    @Test
    public void testCartPersistsAfterRelogin() {
        addToCart("Sauce Labs Backpack");
        driver.findElement(By.id("react-burger-menu-btn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("logout_sidebar_link"))).click();

        login(STANDARD_USER, SECRET_SAUCE);
        Assert.assertEquals(getCartBadgeCount(), "1",
                "INT05: Cart badge should still show 1 item after re-login");
    }

    // INT09: Checkout completion updates inventory
    @Test
    public void testCheckoutCompletionUpdatesInventory() {
        addToCart("Sauce Labs Backpack");
        startCheckout();
        fillCheckoutInfo("Mohamed", "Ali", "12345");
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
        Assert.assertTrue(isCheckoutCompleteDisplayed(),
                "INT09: Checkout complete page should be displayed");
        driver.findElement(backToProductsBtn).click();
        Assert.assertEquals(getCartBadgeCount(), "0",
                "INT09: Cart should be empty after checkout");
    }
}