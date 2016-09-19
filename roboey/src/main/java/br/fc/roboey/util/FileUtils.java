package br.fc.roboey.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class FileUtils {

	/**
	 * Carrega arquivo em StringBuilder
	 */
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

	
}
