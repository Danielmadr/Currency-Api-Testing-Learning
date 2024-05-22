package br.com.ada.currencyapi.controller;


import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class CurrencyControllerEns2EndTest {
  private WebDriver driver;

  @BeforeEach
  public void setUp() {
    System.setProperty("webdriver.chrome.driver", "D:/Users/danie/Desktop/Desenvolvedor/Ada/Lucas/chromedriver-win64/chromedriver.exe");
    driver = new ChromeDriver();
  }

  @AfterEach
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void testCreate() {
    driver.get("http://localhost:8080/new-currency");


    WebElement nameInput = driver.findElement(By.id("name"));
    WebElement descriptionInput = driver.findElement(By.id("description"));
    WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

    nameInput.sendKeys("BTC");
    descriptionInput.sendKeys("Bitcoin");
    submitButton.click();

    driver.get("http://localhost:8080/currencies");
    WebElement table = driver.findElement(By.tagName("table"));
    Assertions.assertTrue(table.getText().contains("BTC"));
  }
}