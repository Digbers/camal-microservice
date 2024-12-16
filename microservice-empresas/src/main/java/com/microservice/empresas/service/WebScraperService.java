package com.microservice.empresas.service;
import com.microservice.empresas.controller.dto.PadronSunatDTO;
import com.microservice.empresas.persistence.repository.IDocumentosTiposRepository;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.text.Normalizer;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebScraperService {
    private WebDriver driver;
    private IDocumentosTiposRepository documentosTiposRepository;

    @PostConstruct
    public void setup() {
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-gpu");
            options.addArguments("--headless");  // Mantener el modo headless
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            options.addArguments("--remote-debugging-port=9222");
            driver = new ChromeDriver(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized PadronSunatDTO navigateAndExtract(String url, String searchInputId, String searchButtonId, String inputValue, String tipoDoc) {
        try {
            driver.get(url);
            // Esperar a que el campo de búsqueda esté presente
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(searchInputId)));
            searchInput.sendKeys(inputValue);
            // Hacer clic en el botón de búsqueda
            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(searchButtonId)));
            searchButton.click();

            WebElement listGroup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-group")));
            System.out.println("listGroup: " + listGroup);
            if (!tipoDoc.equals("RUC")) {

            }

            Map<String, String> dataMap = new HashMap<>();
            List<WebElement> listItems = listGroup.findElements(By.className("list-group-item"));
            System.out.println("listItems: " + listItems);
            listItems.forEach(System.out::println);


            // Lista de nombres que se deben buscar (ignorando mayúsculas y minúsculas)
            List<String> targetNames = Arrays.asList(
                    "Número de RUC:",
                    "Estado del Contribuyente:",
                    "Condición del Contribuyente:",
                    "Domicilio Fiscal:"
            );
            for (int i = 0; i < listItems.size() && i < 8; i++) {
                WebElement item = listItems.get(i);
                WebElement rowDiv = item.findElement(By.className("row"));
                // Obtener la lista de divs hijos dentro de la fila
                List<WebElement> divColumns = rowDiv.findElements(By.tagName("div"));
                // Validar que hay al menos dos columnas dentro de la fila
                if (divColumns.size() > 1) {
                    String headerText = divColumns.get(0).getText().trim().toLowerCase();
                    for (String target : targetNames) {
                        if (headerText.equalsIgnoreCase(target.toLowerCase())) {
                            // Si coincide, obtener el valor del segundo div (hermano) y almacenarlo en el HashMap
                            String value = divColumns.get(1).getText().trim();

                            // Mapear con un nombre clave más limpio para el HashMap usando la función cleanKey
                            String key = cleanKey(target.replace(":", "").trim());
                            dataMap.put(key, value);
                            break;
                        }
                    }
                }
            }
            // Imprimir el HashMap con los valores extraídos
            StringBuilder resultText = new StringBuilder();
            String numeroDoc = "";
            String razonSocial = "";
            PadronSunatDTO padronSunatDTO = new PadronSunatDTO();

            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
                resultText.append(entry.getKey() + ": " + entry.getValue() + "\n");

                // Verificar si la clave es "numero-de-ruc" para separar RUC y Razón Social
                if (entry.getKey().equals("numero-de-ruc")) {
                    String[] parts = entry.getValue().split(" - ");
                    // Almacenar el primer valor (RUC) sin espacios en blanco
                    numeroDoc = parts[0].replaceAll("\\s", "");
                    // Almacenar el segundo valor (Razón Social) eliminando espacios adicionales
                    razonSocial = parts[1].trim();
                }
                // Llenar los datos correspondientes en el DTO dependiendo de la clave
                switch (entry.getKey()) {
                    case "numero-de-ruc":
                        padronSunatDTO.setNumeroDoc(numeroDoc);
                        padronSunatDTO.setRazonSocial(razonSocial);
                        break;
                    case "estado-del-contribuyente":
                        padronSunatDTO.setEstado(entry.getValue());
                        break;
                    case "condicion-del-contribuyente":
                        padronSunatDTO.setCondicion(entry.getValue());
                        break;
                    case "domicilio-fiscal":
                        padronSunatDTO.setDomiciloFiscal(entry.getValue());
                        break;
                }
            }
            System.out.println(padronSunatDTO);

            return padronSunatDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error durante el scraping");
        }
    }
    private static String cleanKey(String input) {
        // Eliminar acentos y normalizar la cadena
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");

        // Reemplazar espacios con guiones y convertir a minúsculas
        return withoutAccents.replace(" ", "-").toLowerCase();
    }

    @PreDestroy
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    //WebElement buttonDocumetno = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnPorDocumento")));
    //WebElement selectDocumento = wait.until(ExpectedConditions.elementToBeClickable(By.id("cmbTipoDoc")));

    // Crear un objeto de tipo Select
    //Select dropdown = new Select(selectDocumento);
    //String codigoSunat = documentosTiposRepository.findDistinctCodigoSunatByDocCodigo(tipoDoc).get(0);
    // Opciones para seleccionar el valor
    //dropdown.selectByVisibleText("DNI");         // Seleccionar por el texto visible de la opción
    //dropdown.selectByValue(codigoSunat);            // Seleccionar por el atributo `value` de la opción

    //WebElement searchInput3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("txtNumeroDocumento")));
    //searchInput3.sendKeys(inputValue);

    //WebElement searchButton3 = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnAceptar")));
    //searchButton3.click();
}

