#  Swag Labs Project

## 🧾 Description
Comprehensive testing framework for the [Swag Labs](https://www.saucedemo.com/) e-commerce platform, combining **manual and automated testing** with **200+ test cases** to validate core functionalities including authentication, product browsing, cart management, and checkout processes.

---

## 🎯 Project Scope

### 📋 Manual Testing
- User Stories & Test Plan
- Test Cases Creation & Execution
- Bug Reports & Test Summary

### 🤖 Automated Testing
Built with **Java**, **Selenium WebDriver**, **TestNG**, and **Maven** using a simple class-based structure suitable for beginners.

---

## 🧰 Tools & Technologies

| Category | Tools |
|----------|-------|
| **Manual Testing** | Google Sheets |
| **Automation** | Java, Selenium WebDriver, TestNG, Maven |
| **IDE** | IntelliJ IDEA |
| **Browser** | Chrome (ChromeDriver) |

---

## ⚙️ Prerequisites
- Java **17+**  
- Maven **3.9+**  
- Google Chrome browser  
- Internet connection (for WebDriverManager)

---

## 📥 Setup & Run

```bash
# Clone repository
git clone https://github.com/MRagab22/Swag_Labs_Project.git
cd Swag_Labs_Project

# Install dependencies
mvn clean install

# Run tests
mvn test
```

**Or** right-click any test class in IntelliJ → **Run**

---

## 📁 Project Structure
```
Swag_Labs/
├── .idea/
├── .mvn/
├── src/
│   ├── main/java/
│   └── test/java/tests/
│       ├── CartTests.java
│       ├── CheckoutTests.java
│       ├── EndToEndTests.java
│       ├── HomeTests.java
│       ├── IntegrationTests.java
│       ├── LoginTests.java
│       ├── ProductDetailTests.java
│       ├── ProductDetailTests_2.java
│       ├── SidebarTests.java
│       └── SystemTests.java
├── target/
├── .gitignore
└── pom.xml
```

---

## 🧪 Test Coverage

### Manual Testing
- **User Stories:** Feature requirements and acceptance criteria
- **Test Plan:** Testing strategy and scope
- **Test Cases:** Detailed test scenarios with expected results
- **Execution:** Systematic test runs and result documentation
- **Bug Reports:** Defect identification and tracking
- **Summary:** Quality metrics and recommendations

### Automated Testing
- **LoginTests:** Authentication validation
- **HomeTests:** Product listing and sorting
- **CartTests:** Cart operations and verification
- **CheckoutTests:** Complete checkout flow
- **ProductDetailTests:** Product information checks
- **SidebarTests:** Navigation and logout
- **Integration/EndToEnd:** Full user journeys
- **SystemTests:** Regression validation

---

## 🔮 Future Enhancements
- Page Object Model (POM) implementation
- Allure/Extent Reports for better visualization
- Data-driven testing (Excel/CSV/JSON)
- Cross-browser testing via Selenium Grid
- CI/CD integration (GitHub Actions/Jenkins)

---

## 👨‍💻 Author
**[MRagab22](https://github.com/MRagab22)**

---

> 🧠 *Demonstrates complete testing approach combining manual documentation with Selenium automation using Java, TestNG, and Maven.*
