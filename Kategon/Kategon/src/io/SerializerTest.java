package io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SerializerTest {
	
	private static final String filename = "junit_test";
	
	@Test
	public void wirteObjectはSerializableなオブジェクトを保存できる() {
		
		List<String> objects = new ArrayList<String>();
		for (int oi = 1; oi <= 3; oi++) objects.add("object" + oi);
		
		try {
			Serializer.wirteObject(objects, filename);
		} catch (BadFileNameException | IOException e) {
			e.printStackTrace();
		}
		
		File file = new File(Serializer.getFilePath(filename));
		boolean fileExists = file.exists();
		assertEquals(fileExists, true);
	}
	
	// TODO: 読み込める
	// TODO: 上書き
	// TODO: ファイル名にドットが入ってたら書き込みアウト（BadFileNameException）
	// TODO: ファイル名にドットが入ってたら読み込みアウト（BadFileNameException）
	// TODO: ファイル名に〜が入ってたらアウト（BadFileNameException）
	// TODO: 誤ったファイル名で読み込みはムリ（IOException）

}
