package io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
	
	private static final String path = "save/"; 
	private static final String ext = ".ser"; 
	
	/**
	 * オブジェクトをファイルに書き込んで保存する
	 * @param obj 保存したいオブジェクト
	 * @param filename 保存したいファイル名（"."使用不可, 拡張子不要）
	 * @throws IOException 書き込みエラーで投げられる例外
	 */
	public static void wirteObject(Object obj, String filename) throws BadFileNameException, IOException {

		// ファイル名チェック
		if (filename.contains(".") == true) throw new BadFileNameException("ファイル名に\".\"は使用できません。");
		
		// オブジェクトを書込むファイルパス
		String filepath = path + filename + ext;

		ObjectOutputStream oos = null;
		
		try {
			// オブジェクトをファイルへ書き込み
			oos = new ObjectOutputStream(new FileOutputStream(filepath));
			oos.writeObject(obj);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}
	
	/**
	 * 保存したオブジェクトを取得する
	 * @param filename 保存したファイル名（"."使用不可, 拡張子不要）
	 * @return 保存したオブジェクト
	 * @throws ClassNotFoundException クラスが見つからないときに投げられる例外
	 * @throws IOException 読み込みエラーで投げられる例外
	 */
	public static Object readObject(String filename) throws BadFileNameException, ClassNotFoundException, IOException {

		// ファイル名チェック
		if (filename.contains(".") == true) throw new BadFileNameException("ファイル名に\".\"は使用できません。");
		
		// オブジェクトを書込むファイルパス
		String filepath = path + filename + ext;

		ObjectInputStream ois = null;
		Object readObj = null;

		try {
			// 書き込んだファイルからオブジェクトを読み込み
			ois = new ObjectInputStream(new FileInputStream(filepath));
			readObj = ois.readObject();
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				throw e;
			}
		}
		
		return readObj;
	}

}
