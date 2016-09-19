package br.fc.roboey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Properties;

import org.apache.http.conn.HttpHostConnectException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import br.fc.roboey.util.Config;
import br.fc.roboey.util.FileUtils;
import br.fc.roboey.util.WebCrawler;

/**
 * Hello world!
 *
 */
public class Robodev 
{

	/**
	 * Aplicação principal
	 */
	public static void main(String[] args) throws IOException {

		WebCrawler webScrapper = new WebCrawler();
		FileUtils fileUtils = new FileUtils();
		Properties conf = Config.getProp();

		//define arquivos de entrada de dados e saída de dados
		String devedores[] = fileUtils.getFile(conf.getProperty("arquivo.origem")).split("\n");
		String processados = fileUtils.getFile(conf.getProperty("arquivo.destino"));

		List<String> resultado;

		String field[];

		FileWriter file;

		//percorre a lista de devedores
		for(String line : devedores){

			field = line.split(";");
			/*
			 * field[0] + field[1]: CNPJ
			 * field[2]           : Razão Social
			 * field[4]           : UF
			 */

			if(field[2]!=null && field[4]!=null){

				//Se a razão social já foi processado, não executa
				if(processados.indexOf(field[2]) < 0 ){


					try{
						File arquivo = new File(conf.getProperty("arquivo.destino"));
						file = new FileWriter(arquivo,true);
						//buffw = new BufferedWriter(file);


						try{
							//abre o site
							webScrapper.openTestSite();
							System.out.println("Processando >>> " + field[2] +"...");
							//executa a pesquisa
							resultado = webScrapper.inputFilter(field[2], field[4],conf.getProperty("filtro.faixa"));

							if(resultado.size() == 0 ){

								resultado.add("Nenhum resultado encontrado;");
							}

							for(String res : resultado){
								file.write(field[0]+"/"+field[1]+";"+field[2]+";"+res + "\n");
							}


						}catch(UnreachableBrowserException e){
							System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado...");
							System.exit(1);
						}catch(HttpHostConnectException e ){
							System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado...");
							System.exit(1);
						}catch(ConnectException e){
							System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado...");
							System.exit(1);	
						}catch(Exception e ){
							System.out.println("Erro ao acessar o Browser: verifique se o mesmo nao foi fechado...");
							System.exit(1);
						}

						file.close();

					}catch(IOException e){
						System.out.println("Erro ao abrir arquivos");
						System.exit(1);
					}

					catch(Exception e){
						System.out.println("Erro ao abrir arquivos");
						System.exit(1);
					}
					finally{

					}

				}

			}

		}

		webScrapper.closeBrowser();
	}


}
