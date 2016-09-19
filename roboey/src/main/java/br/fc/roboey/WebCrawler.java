package br.fc.roboey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;



public class WebCrawler {

	public WebDriver driver = new FirefoxDriver();

	/**
	 * Open the test website.
	 */
	public void openTestSite() {
		//driver.navigate().to("http://testing-ground.scraping.pro/login");
		driver.navigate().to("https://www2.pgfn.fazenda.gov.br/ecac/contribuinte/devedores/listaDevedores.jsf");
	}

	/**
	 * 
	 * @param username
	 * @param Password
	 * 
	 *            Logins into the website, by entering provided username and
	 *            password
	 */
	public void login(String username, String Password) {

		WebElement userName_editbox = driver.findElement(By.id("usr"));
		WebElement password_editbox = driver.findElement(By.id("pwd"));
		WebElement submit_button = driver.findElement(By.xpath("//input[@value='Login']"));

		userName_editbox.sendKeys(username);
		password_editbox.sendKeys(Password);
		submit_button.click();

	}

	public List<String> input(String razaoSocial, String uf, String faixa){

		
		Select selectBox = null;
		String pattern = null;
		
		List <String> res = new ArrayList<String>();

		// times out after 60 seconds (devido ao tempo para digitar o captcha)
		WebDriverWait wait = new WebDriverWait(driver, 60);


		//seleciona o radio button de pesquisa por nome
		wait.until(presenceOfElementLocated(By.id("listaDevedoresForm:radioTipoConsulta:1")));
		driver.findElement(By.id("listaDevedoresForm:radioTipoConsulta:1")).click();


		//preenche a razao social
		wait.until(ExpectedConditions.elementToBeClickable(By.id("listaDevedoresForm:nomeInput")));
		driver.findElement(By.id("listaDevedoresForm:nomeInput")).sendKeys(razaoSocial);

		//preenche UF
		if(!uf.equals("**")){
			wait.until(presenceOfElementLocated(By.id("listaDevedoresForm:ufInput")));
			selectBox = new Select(driver.findElement(By.id("listaDevedoresForm:ufInput")));
			selectBox.selectByValue(uf);
		}

		//define a faixa de pesquisa
		wait.until(presenceOfElementLocated(By.id("listaDevedoresForm:faixasInput")));
		selectBox = new Select(driver.findElement(By.id("listaDevedoresForm:faixasInput")));
		selectBox.selectByValue(faixa);

		//Focus no campo captcha
		wait.until(presenceOfElementLocated(By.id("listaDevedoresForm:captcha")));
		driver.findElement(By.id("listaDevedoresForm:captcha")).sendKeys("");

		//aguarda o usuário preencher o captcha
		waitInsertInputCaptcha(driver.findElement(By.id("listaDevedoresForm:captcha")),wait);


		//envia página de requisição
		wait.until(presenceOfElementLocated(By.id("listaDevedoresForm:consultarButton")));
		driver.findElement(By.id("listaDevedoresForm:consultarButton")).click();

		/*
		//Busca a existencia da mensagem de "Registro não encontrado"
        boolean flag=false;
		pattern = "div[id='corpoPanel'] > form[id='listaDevedoresForm'] > span[id='listaDevedoresForm:j_id88'] > span[class='rich-message-label']";
		try{
			String notFoundMsg = (driver.findElement(By.cssSelector(pattern)).getText());
			if(notFoundMsg.trim().equalsIgnoreCase("Nenhum registro foi encontrado"))
			flag=true;

		}catch(NoSuchElementException e) {
			//Tabela resultado presente: nada a fazer;
		}
		 */

		try{

			//captura linha da tabela resultado com CNPJ, DESCRICAO e VALOR
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
			
			
			//pattern = "table[id='listaDevedoresForm:devedoresTable'] > tbody > tr > td[class='rich-table-cell SemQuebra Esquerda']";
			//String cnpj = (driver.findElement(By.cssSelector(pattern))).getText();
			
			//pattern = "table[id='listaDevedoresForm:devedoresTable'] > tbody > tr > td[class='rich-table-cell Direita LarguraMinima']";
			//String valor = (driver.findElement(By.cssSelector(pattern))).getText();
			
			//resultado = cnpj + ";" + valor;

		}catch(NoSuchElementException e) {
			//sem resultado
		}
		catch(Exception e) {
			//sem resultado
		}

		return res;



	}

	//metodo para aguardar o usuário inserir o CAPTCHA
	public void waitInsertInputCaptcha(final WebElement element, WebDriverWait wait)  
	{

		wait.pollingEvery(2, TimeUnit.SECONDS);

		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				String value = element.getAttribute("value");

				if(value.length() == 6) {

					return true;
				}

				return false;
			}
		});
	}


	/**
	 * grabs the status text and saves that into status.txt file
	 * 
	 * @throws IOException

	public void getText() throws IOException {
		String text = driver.findElement(By.xpath("//div[@id='case_login']/h3")).getText();
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("status.txt"), "utf-8"));
		writer.write(text);
		writer.close();

	}
	 */



	/**
	 * Saves the screenshot
	 * 
	 * @throws IOException
	 */
	public void saveScreenshot() throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File("screenshot.png"));
	}

	public void closeBrowser() {
		driver.close();
	}

	private static Function<WebDriver,WebElement> presenceOfElementLocated(final By locator) {
		return new Function<WebDriver, WebElement>() {
			//@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(locator);
			}
		};
	}



	//Carrega arquivo em StringBuilder
	public String getFile(String fileName) throws IOException {

		String fileContents = null;

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileName);
			fileContents = IOUtils.toString(inputStream);
			inputStream.close();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}finally {
			if(inputStream!=null) inputStream.close();
		}

		return fileContents;

	}



	public static void main(String[] args) throws IOException {
		WebCrawler webScrapper = new WebCrawler();

		String devedores[] = webScrapper.getFile("devedores.csv").split("\n");
		String processados = webScrapper.getFile("devedores_processados.csv");

		List<String> resultado;
		
		String field[];

		FileWriter file;


		for(String line : devedores){

			field = line.split(";");

			if(field[2]!=null && field[4]!=null){

				//Se a razão social já foi processado, não executa
				if(processados.indexOf(field[2]) < 0 ){

					try{
						File arquivo = new File("devedores_processados.csv");
						file = new FileWriter(arquivo,true);
						//buffw = new BufferedWriter(file);

						//abre o site
						webScrapper.openTestSite();

						System.out.println("Processando >>> " + field[2] +"...");

						//executa a pesquisa
						resultado = webScrapper.input(field[2], field[4],"DE_10_MILHOES_ATE_100_MILHOES");
						
						if(resultado.size() == 0 ){
							
							resultado.add("Nenhum resultado encontrado;");
						}
						
						for(String res : resultado){
							
							file.write(field[0]+"/"+field[1]+";"+field[2]+";"+res + "\n");
							
						}
						
						
						file.close();

					}catch(IOException e){
						e.printStackTrace();
					}

					catch(Exception e){
						e.printStackTrace();
					}
					finally{

					}

				}

			}

		}

		webScrapper.closeBrowser();
	}
}
