package br.fc.roboey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Properties;

import org.apache.http.conn.HttpHostConnectException;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.UnreachableBrowserException;

import br.fc.roboey.util.Config;
import br.fc.roboey.util.WebCrawler;

/**
 * Automação parcial de captura de informações da fazenda nacional
 *
 */
public class Robodev 
{

	final private static Logger logger = Logger.getLogger(Robodev.class);


	/**
	 * Aplicação principal
	 */
	public static void main(String[] args) throws IOException {

		WebCrawler webScrapper = new WebCrawler();
		Properties conf = Config.getProp();

		String faixas[]={"DE_10_MILHOES_ATE_100_MILHOES"};


		List<String> resultado;


		FileWriter file;

		//percorre a lista de faixas
		for(String faixa : faixas){


			try{
				File arquivo = new File(conf.getProperty("arquivo.destino"));
				file = new FileWriter(arquivo,true);



				try{
					//abre o site
					webScrapper.openTestSite();
					System.out.println("Processando >>> " + faixa +"...");
					//executa a pesquisa
					resultado = webScrapper.inputFilter(faixa);

					if(resultado.size() == 0 ){

						resultado.add("Nenhum resultado encontrado;");
					}

					for(String res : resultado){
						file.write(faixa + ";" + res + "\n");
					}


				}catch(UnreachableBrowserException e){
					System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado ou se o tempo para digitação não excedeu 60s...");
					logger.error(e.toString());
					webScrapper.closeBrowser();
					System.exit(1);
				}catch(HttpHostConnectException e ){
					System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado ou se o tempo para digitação não excedeu 60s...");
					logger.error(e.toString());
					webScrapper.closeBrowser();
					System.exit(1);
				}catch(ConnectException e){
					System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado ou se o tempo para digitação não excedeu 60s...");
					logger.error(e.toString());
					webScrapper.closeBrowser();
					System.exit(1);	
				}catch(Exception e ){
					System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado ou se o tempo para digitação não excedeu 60s...");
					logger.error(e.toString());
					webScrapper.closeBrowser();
					System.exit(1);
				}

				file.close();

			}catch(IOException e){
				System.out.println("Erro ao abrir arquivos");
				logger.error(e.toString());
				System.exit(1);
			}

			catch(Exception e){
				System.out.println("Erro ao abrir arquivos");
				logger.error(e.toString());
				System.exit(1);
			}
			finally{

			}

		}





		webScrapper.closeBrowser();
	}


}
