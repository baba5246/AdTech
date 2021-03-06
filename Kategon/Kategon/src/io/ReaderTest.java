package io;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class ReaderTest {

	@Test
	public void readCSVはconst_gpから15個のカテゴリを取得できる() {
		
		Reader reader = new Reader();
		ArrayList<String[]> categories = null;
		try {
			categories = reader.readCSV("consts/const_gp.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int actual = 0;
		if (categories != null) actual = categories.get(0).length;
		int matcher = 15;
		assertThat(actual, is(matcher));
	}

	@Test
	public void readCSVはconst_gpから15個のファイルパスを取得できる() {
		
		Reader reader = new Reader();
		ArrayList<String[]> categories = null;
		try {
			categories = reader.readCSV("consts/const_gp.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int actual = 0;
		if (categories != null) actual = categories.get(1).length;
		int matcher = 15;
		
		assertThat(actual, is(matcher));
	}
	
	@Test(expected=IOException.class)
	public void readCSVは読み込みエラーでIOExceptionを投げる() throws IOException {
		Reader reader = new Reader();
		reader.readCSV("aaaa.csv");
	}
	
	@Test
	public void readJSONはconst_jsonから330個のアプリ情報を取得できる() {
		
		Reader reader = new Reader();
		Map<String, Map<String, Object>> jsonData = null;
		try {
			jsonData =  reader.readJSON("consts/const_json.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int actual = 0;
		if (jsonData != null) actual = jsonData.size();
		int matcher = 330;
		assertThat(actual, is(matcher));
	}

	@Test
	public void readJSONはconst_json内の1つのアプリ情報から5つのkeyを取得できる() {

		String[] matcher = { "category", "title", "company", "description",
				"images" };

		Reader reader = new Reader();
		Map<String, Map<String, Object>> jsonData = null;
		try {
			jsonData = reader.readJSON("consts/const_json.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 最初のkeyを取得して、そのアプリの情報を取得
		String[] actual = null;
		if (jsonData != null) {
			String firstKey = jsonData.keySet().iterator().next();
			Map<String, Object> app = jsonData.get(firstKey);
			actual = app.keySet().toArray(new String[app.size()]);
		}
		assertThat(actual, is(matcher));
	}

	@Test(expected = IOException.class)
	public void readJSONは読み込みエラーでIOExceptionを投げる() throws IOException {
		Reader reader = new Reader();
		reader.readJSON("aaaa.json");
	}
	
	
}
