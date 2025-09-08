package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object class for the login functionality.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Login form fields
    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(id = "sign_in_btn")
    private WebElement signInButton;

    /**
     * Constructor initializes WebDriver and WebDriverWait.
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        PageFactory.initElements(driver, this);
    }

    /**
     * Waits for the loader to disappear from the page.
     */
    private void waitForLoaderToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loader")));
        } catch (TimeoutException ignored) {
        }
    }

    /**
     * Navigates to the homepage and waits until the page is loaded.
     */
    public void navigateToWebsite() {
        driver.get("https://advantageonlineshopping.com/#/");
        wait.until(ExpectedConditions.titleContains("Advantage"));
    }

    /**
     * Opens the login popup by clicking the user icon.
     */
    public void openLoginForm() {
        waitForLoaderToDisappear();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("menuUserLink"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".PopUp")));
    }

    /**
     * Enters the provided username.
     */
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput)).clear();
        usernameInput.sendKeys(username);
    }

    /**
     * Enters the provided password.
     */
    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput)).clear();
        passwordInput.sendKeys(password);
    }

    /**
     * Submits the login form by clicking the sign-in button.
     * Falls back to JavaScript click if normal click fails.
     */
    public void clickLoginButton() {
        waitForLoaderToDisappear();
        try {
            wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
        } catch (WebDriverException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signInButton);
        }
    }
}
