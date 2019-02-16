package org.springframework.samples.petclinic.selenium;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by pgoultiaev on 24/07/16.
 */
public class SeleniumIT {

    private WebDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        String serverUrl = System.getProperty("grid.server.url");
        String gridServerUrl = "http://localhost:4444/wd/hub";
        if (serverUrl != null) {
            gridServerUrl = serverUrl;
        }
        DesiredCapabilities capability = DesiredCapabilities.chrome();
        URL gridUrl = new URL(gridServerUrl);
        driver = new RemoteWebDriver(gridUrl, capability);
    }

    @Test
    public void titleShouldBeCorrect() {
        driver.get("http://petclinic:9966/petclinic");

        assertEquals("PetClinic :: a Spring Framework demonstration", driver.getTitle());
    }

    @Test
    public void findOwnersShouldListMe() {
        driver.get("http://petclinic:9966/petclinic");

        driver.findElement(By.xpath("//*[@title=\"find owners\"]")).click();
        driver.findElement(By.linkText("Add Owner")).click();

        driver.findElement(By.id("firstName")).sendKeys("Rickard");
        driver.findElement(By.id("lastName")).sendKeys("von Essen");
        driver.findElement(By.id("address")).sendKeys("Industrigatan 9");
        driver.findElement(By.id("city")).sendKeys("Stockholm");
        driver.findElement(By.id("telephone")).sendKeys("070776655");
        driver.findElement(By.xpath("//button[@type=\"submit\"]")).click();

        driver.findElement(By.xpath("//*[@title=\"find owners\"]")).click();
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys("von Essen");
        driver.findElement(By.xpath("//button[@type=\"submit\"]")).click();
    }

    @After
    public void TearDown() {
        driver.quit();
    }

}
