package br.fc.roboey.util;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpHostConnectException;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;



public class WebCrawler {

	public WebDriver driver = new FirefoxDriver();

	/**
	 * Open the test website.
	 */
	public void openTestSite() throws ConnectException, HttpHostConnectException, UnreachableBrowserException, Exception{
		//driver.navigate().to("http://testing-ground.scraping.pro/login");
		driver.navigate().to("https://www2.pgfn.fazenda.gov.br/ecac/contribuinte/devedores/listaDevedores.jsf");
	}


	/**
	 * Apply filter for search.
	 */
	public List<String> inputFilter(String razaoSocial, String uf, String faixa) throws ConnectException, HttpHostConnectException, UnreachableBrowserException, Exception{

		Select selectBox = null;
		String pattern = null;

		List <String> res = new ArrayList<String>();

		// times out after 60 seconds (devido ao tempo para digitar o captcha)
		WebDriverWait wait = new WebDriverWait(driver, 60);


		//seleciona o radio button de pesquisa por nome
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("listaDevedoresForm:radioTipoConsulta:1")));
		driver.findElement(By.id("listaDevedoresForm:radioTipoConsulta:1")).click();


		//preenche a razao social
		wait.until(ExpectedConditions.elementToBeClickable(By.id("listaDevedoresForm:nomeInput")));
		driver.findElement(By.id("listaDevedoresForm:nomeInput")).sendKeys(razaoSocial);

		//preenche UF
		if(!uf.equals("**")){
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("listaDevedoresForm:ufInput")));
			selectBox = new Select(driver.findElement(By.id("listaDevedoresForm:ufInput")));
			selectBox.selectByValue(uf);
		}

		//define a faixa de pesquisa
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("listaDevedoresForm:faixasInput")));
		selectBox = new Select(driver.findElement(By.id("listaDevedoresForm:faixasInput")));
		selectBox.selectByValue(faixa);

		//Focus no campo captcha
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("listaDevedoresForm:captcha")));
		driver.findElement(By.id("listaDevedoresForm:captcha")).sendKeys("");

		//aguarda o usuário preencher o captcha
		waitInsertInputCaptcha(driver.findElement(By.id("listaDevedoresForm:captcha")),wait);



		//envia página de requisição
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("listaDevedoresForm:consultarButton")));
		driver.findElement(By.id("listaDevedoresForm:consultarButton")).click();


		//captura linha da tabela resultado com CNPJ, DESCRICAO e VALOR
		try{

			String cnpj;
			String valor;

			pattern = "table[id='listaDevedoresForm:devedoresTable'] > tbody > tr";
			//wait.until(presenceOfElementLocated(By.cssSelector(pattern)));

			List<WebElement> we = driver.findElements(By.cssSelector(pattern));

			for(WebElement element : we){
				cnpj = element.findElement(By.cssSelector("td[class='rich-table-cell SemQuebra Esquerda']")).getText();
				valor = element.findElement(By.cssSelector("td[class='rich-table-cell Direita LarguraMinima']")).getText();		
				res.add(cnpj+ ";" + valor);
			}


		}catch(InvalidElementStateException e ){
			System.err.println("Nenhum resultado encontrado...");
		}
		catch(NoSuchElementException e) {
			System.err.println("Nenhum resultado encontrado...");
		}
		catch(Exception e) {
			System.err.println("Nenhum resultado encontrado...");
		}

		return res;


	}

	/**
	 * wait user enter the captcha
	 */
	public void waitInsertInputCaptcha(final WebElement element, WebDriverWait wait)  
	{

		wait.pollingEvery(2, TimeUnit.SECONDS);

		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				String value = element.getAttribute("value");

				//tamanho máximo do captcha
				if(value.length() == 6) {

					return true;
				}

				return false;
			}
		});
	}

	/**
	 * close the browser
	 */
	public void closeBrowser() {
		driver.close();
	}

	

}
