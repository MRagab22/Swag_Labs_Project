package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object class for the "Create New Account" functionality.
 */
public class CreateAccountPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    // UI Elements
    @FindBy(id = "menuUser")
    private WebElement userIcon;

    @FindBy(xpath = "//a[text()='CREATE NEW ACCOUNT']")
    private WebElement createNewAccountLink;

    @FindBy(css = ".loader")
    private WebElement loader;

    @FindBy(name = "usernameRegisterPage")
    private WebElement usernameInput;

    @FindBy(name = "emailRegisterPage")
    private WebElement emailInput;

    @FindBy(name = "passwordRegisterPage")
    private WebElement passwordInput;

    @FindBy(name = "confirm_passwordRegisterPage")
    private WebElement confirmPasswordInput;

    @FindBy(name = "first_nameRegisterPage")
    private WebElement firstNameInput;

    @FindBy(name = "last_nameRegisterPage")
    private WebElement lastNameInput;

    @FindBy(name = "phone_numberRegisterPage")
    private WebElement phoneInput;

    @FindBy(name = "countryListboxRegisterPage")
    private WebElement countryDropdown;

    @FindBy(name = "cityRegisterPage")
    private WebElement cityInput;

    @FindBy(name = "addressRegisterPage")
    private WebElement addressInput;

    @FindBy(name = "state_/_province_/_regionRegisterPage")
    private WebElement stateInput;

    @FindBy(name = "postal_codeRegisterPage")
    private WebElement postalCodeInput;

    @FindBy(name = "i_agree")
    private WebElement agreeCheckbox;

    @FindBy(id = "register_btn")
    private WebElement registerButton;

    /**
     * Constructor initializes driver, wait, and PageFactory elements.
     */
    public CreateAccountPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Waits until the loading spinner disappears.
     */
    private void waitForLoaderToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOf(loader));
        } catch (TimeoutException ignored) {
        }
    }

    /**
     * Navigates to the main website homepage.
     */
    public void navigateToHomePage() {
        driver.get("https://advantageonlineshopping.com/#/");
    }

    /**
     * Opens the registration form by clicking the user icon and then "CREATE NEW ACCOUNT".
     */
    public void openRegistrationForm() {
        waitForLoaderToDisappear();
        wait.until(ExpectedConditions.elementToBeClickable(userIcon)).click();
        wait.until(ExpectedConditions.visibilityOf(createNewAccountLink));
        waitForLoaderToDisappear();
        js.executeScript("arguments[0].click();", createNewAccountLink);
    }

    /**
     * Fills in all required fields of the registration form.
     */
    public void fillRegistrationForm(
            String username, String email, String password,
            String firstName, String lastName, String phone,
            String country, String city, String address,
            String state, String postalCode) {

        wait.until(ExpectedConditions.visibilityOf(usernameInput)).sendKeys(username);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        confirmPasswordInput.sendKeys(password); // repeat password
        firstNameInput.sendKeys(firstName);
        lastNameInput.sendKeys(lastName);
        phoneInput.sendKeys(phone);
        new Select(countryDropdown).selectByVisibleText(country);
        cityInput.sendKeys(city);
        addressInput.sendKeys(address);
        stateInput.sendKeys(state);
        postalCodeInput.sendKeys(postalCode);
    }

    /**
     * Selects the "I Agree" checkbox if not already selected.
     */
    public void agreeToTerms() {
        if (!agreeCheckbox.isSelected()) {
            agreeCheckbox.click();
        }
    }

    /**
     * Clicks the register button to submit the form.
     */
    public void submitRegistration() {
        wait.until(ExpectedConditions.elementToBeClickable(registerButton)).click();
    }

    /**
     * Verifies if registration was successful by checking the final URL.
     * @return true if successful, false otherwise
     */
    public boolean isRegistrationSuccessful() {
        try {
            wait.until(ExpectedConditions.urlToBe("https://advantageonlineshopping.com/#/"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
