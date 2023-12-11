import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class AmazonLaptopShoppingTest {

    public static void main(String[] args) {

        // Initialize ChromeDriver
        WebDriver driver = new ChromeDriver();


        // Step 1: Enter amazon.com and check the homepage
        driver.get("https://www.amazon.com");
        System.out.println("Amazon homepage has been opened.");

        // Step 2: Search by word “laptop”"
        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys("laptop");
        searchBox.submit();

        /* Step 3: Add the non-discounted products
        in stock on the first page of the
        search results to the cart */

        // Find and click the links with link text containing the word 'Laptop'
        List<WebElement> laptopLinks = driver.findElements(By.partialLinkText("Laptop"));

        // Counter for the non-discounted laptops which are added to cart
        int counter = 0;

        try {
            for (int i = 0; i < laptopLinks.size(); i++) {

                // Re-find the laptop link on each iteration
                laptopLinks = driver.findElements(By.partialLinkText("Laptop"));
                WebElement laptopLink = laptopLinks.get(i);

                // Click the laptop link
                laptopLink.click();

                // Check if the "Deal" badge is present
                if (hasDealBadge(driver)) {
                    System.out.println("Deal badge found. Going back.");
                    // Go back to the search results page
                    driver.navigate().back();
                } else if (!isAddToCartButtonPresent(driver)) {
                    System.out.println("'Add to Cart' button not found. Going back.");
                    // Go back to the search results page
                    driver.navigate().back();
                } else {
                    System.out.println("Adding to cart and going back.");
                    // Add to cart
                    WebElement button = driver.findElement(By.id("add-to-cart-button"));
                    button.click();
                    // Go back to the search results page
                    driver.navigate().back();
                    driver.navigate().back();
                    counter++;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Caught IndexOutOfBoundsException. Continuing with the program.");
        }

        // Step 4: Go to cart and check if the products are the right amount

        // Click on the cart icon using JavaScript
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        WebElement cartIcon = driver.findElement(By.cssSelector("span.nav-cart-icon.nav-sprite"));
        executor.executeScript("arguments[0].click();", cartIcon);

        // Retrieve the Subtotal value
        WebElement subtotalElement = driver.findElement(By.id("nav-cart-count"));
        String subtotalText = subtotalElement.getText().trim();
        // Extract the numeric value from the Subtotal text
        int subtotalValue = Integer.parseInt(subtotalText.replaceAll("\\D+", ""));
        // Print the Subtotal value
        if (subtotalValue == counter) {
            System.out.println("Subtotal Value is " + subtotalValue +
                    ". The non-discounted products are the right amount.");
        } else {
            System.out.println("The non-discounted products added to cart are " + subtotalValue +
                    ", which is not expected. The expected amount was: " + counter);
        }

        // Close the browser window
        driver.quit();
    }


    private static boolean hasDealBadge(WebDriver driver) {
        // Check if the "Deal" badge is present
        try {
            WebElement dealBadge = driver.findElement(By.xpath("//span[@id='dealBadgeSupportingText' and @class='a-size-small dealBadgeTextColor a-text-bold']/span[text()='Deal']"));
            return dealBadge.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false; // "Deal" badge not found
        }
    }

    private static boolean isAddToCartButtonPresent(WebDriver driver) {
        // Check if the "Add to Cart" button is present
        try {
            WebElement addToCartButton = driver.findElement(By.id("add-to-cart-button"));
            return addToCartButton.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false; // "Add to Cart" button not found
        }
    }
}
