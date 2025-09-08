// File: tests/LoginTest.java
package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.BaseTest;

public class LoginTest extends BaseTest {

    @Test(description = "TC_LI_1: Simple Login Test - Enter Username, Password and Click Login")
    public void TC_LI_1_SimpleLogin() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 Starting Simple Login Test");
        System.out.println("=".repeat(60));

        try {
            LoginPage page = loginPage();

            String username = "mohamed15";
            String password = "4T5555aa";

            System.out.println("📋 Test Data:");
            System.out.println("   • Username: " + username);
            System.out.println("   • Password: " + password);
            System.out.println();

            page.navigateToWebsite();
            page.openLoginForm();
            System.out.println("✅ Ready for login\n");

            System.out.println("📝 Step 1: Enter Username");
            page.enterUsername(username);
            System.out.println("✅ Step 1: Username entered successfully\n");

            System.out.println("📝 Step 2: Enter Password");
            page.enterPassword(password);
            System.out.println("✅ Step 2: Password entered successfully\n");

            System.out.println("📝 Step 3: Click Login Button");
            page.clickLoginButton();
            System.out.println("✅ Step 3: Login button clicked successfully\n");

            System.out.println("=".repeat(60));
            System.out.println("🎉 Simple Login Test Completed!");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("💥 Test Failed: Exception occurred");
            System.out.println("Exception: " + e.getMessage());
            System.out.println("=".repeat(60));
            e.printStackTrace();
            Assert.fail("Test execution failed: " + e.getMessage());
        }
    }
}
