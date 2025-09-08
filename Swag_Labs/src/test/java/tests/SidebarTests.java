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

public class SidebarTests {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameInput          = By.id("user-name");
    private final By passwordInput          = By.id("password");
    private final By loginButton            = By.id("login-button");
    private final By burgerMenuButton       = By.id("react-burger-menu-btn");
    private final By logoutLink             = By.id("logout_sidebar_link");
    private final By allItemsLink           = By.id("inventory_sidebar_link");
    private final By aboutLink              = By.id("about_sidebar_link");
    private final By resetAppStateLink      = By.id("reset_sidebar_link");
    private final By inventoryList          = By.className("inventory_list");
    private final By loginPageButton        = By.id("login-button");
    private final By cartBadge              = By.className("shopping_cart_badge");

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

    private void openSidebar() {
        wait.until(ExpectedConditions.elementToBeClickable(burgerMenuButton)).click();
    }

    private boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private String getCartBadgeCount() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return "0";
        }
    }

    // TC01: Hamburger icon presence
    @Test
    public void testHamburgerIconPresence() {
        Assert.assertTrue(isElementDisplayed(burgerMenuButton),
                "TC01: Hamburger icon should be present in navbar");
    }

    // TC02: All Items link navigation
    @Test
    public void testAllItemsLinkNavigation() {
        openSidebar();
        wait.until(ExpectedConditions.elementToBeClickable(allItemsLink)).click();
        Assert.assertTrue(isElementDisplayed(inventoryList),
                "TC02: Home page should be displayed after clicking All Items");
    }

    // TC03: About link navigation
    @Test
    public void testAboutLinkNavigation() {
        openSidebar();
        wait.until(ExpectedConditions.elementToBeClickable(aboutLink)).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("saucelabs.com"),
                "TC03: About page should be displayed after clicking About link");
    }

    // TC04: Logout link functionality
    @Test
    public void testLogoutLinkFunctionality() {
        openSidebar();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        Assert.assertTrue(isElementDisplayed(loginPageButton),
                "TC04: Login page should be displayed after logout");
    }

    // TC05: Reset App State resets cart
    @Test
    public void testResetAppState() {
        // Precondition: add an item to cart
        driver.findElement(By.xpath(
                "//div[text()='Sauce Labs Backpack']" +
                        "/ancestor::div[@class='inventory_item']" +
                        "//button[contains(text(), 'Add to cart')]"
        )).click();

        openSidebar();
        wait.until(ExpectedConditions.elementToBeClickable(resetAppStateLink)).click();
        Assert.assertEquals(getCartBadgeCount(), "0",
                "TC05: Cart badge should be 0 after resetting app state");
    }
}
