package br.fc.roboey.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config{

	public static Properties getProp() throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream("conf.properties");
		props.load(file);
		return props;

	}
}