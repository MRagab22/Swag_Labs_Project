package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CreateAccountPage;
import utils.BaseTest;

public class CreateAccountTest extends BaseTest {

    @Test(description = "TC_CA_1: Create Account with Valid Data")
    public void TC_CA_1_CreateAccountWithValidData() {
        try {
            CreateAccountPage page = createAccountPage();

            System.out.println("🚀 Starting TC_CA_1: Create Account with Valid Data");

            // Step 1: Navigate to website
            System.out.println("Step 1: Navigate to https://advantageonlineshopping.com/#/");
            page.navigateToHomePage();
            System.out.println("Step 1: Navigated to homepage successfully");

            // Step 2: Click user icon, select "CREATE NEW ACCOUNT"
            System.out.println("Step 2: Opening registration form...");
            page.openRegistrationForm();
            System.out.println("Step 2: Registration form opened successfully");

            // Step 3: Enter all valid data as specified in test case
            System.out.println("Step 3: Filling registration form with test data...");

            // Test Data as specified
            String username = "mohamed15";
            String email = "mohamed@gmail.com";
            String password = "4T5555aa";
            String firstName = "Mohamed";
            String lastName = "Ahmed";
            String phoneNumber = "01010079536";
            String country = "Egypt";
            String city = "Cairo";
            String address = "123 Tahrir St";
            String state = "Cairo";
            String postalCode = "12698";

            System.out.println("Test Data:");
            System.out.println("- Username: " + username);
            System.out.println("- Email: " + email);
            System.out.println("- Password: " + password);
            System.out.println("- First Name: " + firstName);
            System.out.println("- Last Name: " + lastName);
            System.out.println("- Phone: " + phoneNumber);
            System.out.println("- Country: " + country);
            System.out.println("- City: " + city);
            System.out.println("- Address: " + address);
            System.out.println("- State: " + state);
            System.out.println("- Postal Code: " + postalCode);

            page.fillRegistrationForm(
                    username,
                    email,
                    password,
                    firstName,
                    lastName,
                    phoneNumber,
                    country,
                    city,
                    address,
                    state,
                    postalCode
            );

            System.out.println("✅ Step 3: All form fields filled successfully");

            // Step 4: Check "I agree to Terms & Conditions"
            System.out.println("📝 Step 4: Agreeing to Terms & Conditions...");
            page.agreeToTerms();
            System.out.println("✅ Step 4: 'I agree to Terms & Conditions' checkbox checked");

            // Step 5: Click "REGISTER"
            System.out.println("📝 Step 5: Submitting registration...");
            page.submitRegistration();
            System.out.println("✅ Step 5: Registration submitted successfully");

            // Expected Result: Account created, redirected to homepage
            System.out.println("🔍 Verifying Expected Result: Account created, redirected to homepage");
            boolean isSuccessful = page.isRegistrationSuccessful();

            if (isSuccessful) {
                System.out.println("✅ ACTUAL RESULT: Account created successfully, redirected to homepage");
                System.out.println("🎉 TC_CA_1 PASSED: Test case executed successfully!");
            } else {
                System.out.println("❌ ACTUAL RESULT: Account creation failed or homepage not reached");
                System.out.println("💥 TC_CA_1 FAILED: Expected account creation and redirect to homepage");
            }

            // Assert the result
            Assert.assertTrue(isSuccessful,
                    "TC_CA_1 FAILED: Account was not created successfully. " +
                            "Expected: Account created and redirected to homepage. " +
                            "Actual: Registration failed or success page not reached.");

        } catch (Exception e) {
            System.out.println("💥 TC_CA_1 FAILED: Test execution failed with exception");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("TC_CA_1 execution failed: " + e.getMessage());
        }
    }
}