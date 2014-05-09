package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDao {

	private static final String APP_STORE_HOST = "dbh23.mongolab.com";
	private static final int APP_STORE_PORT = 27237;
	private static final String GOOGLE_PLAY_HOST = "ds053708.mongolab.com";
	private static final int GOOGLE_PLAY_PORT = 53708;
	private static final String APP_STORE_DB_NAME = "heroku_app24506827";
	private static final String GOOGLE_PLAY_DB_NAME = "heroku_app24368391";
	private static final String ACCOUNT_NAME = "baba";
	private static final String ACCOUNT_PASS = "jun";
	private static final String COLLECTION_NAME = "apps";
	
	private DB db = null;

	public void getLocalMongo() {
		
		Mongo m = null;
		try {
			m = new Mongo();
			db = m.getDB("test");

			DBCollection coll = db.getCollection("articles");
			DBCursor cur = coll.find();

			while(cur.hasNext()) {
	            System.out.println(cur.next());
	        }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (m != null) m.close();
		}

	}
	
	public List<Map<String, Object>> getAppStoreDB() {
		return getServerDB(APP_STORE_HOST, APP_STORE_PORT, APP_STORE_DB_NAME);
	}
	
	public List<Map<String, Object>> getGooglePlayDB() {
		return getServerDB(GOOGLE_PLAY_HOST, GOOGLE_PLAY_PORT, GOOGLE_PLAY_DB_NAME);
		
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getServerDB(String hostname, int port, String dbname) {
		
		List<Map<String, Object>> appData = new ArrayList<Map<String, Object>>();
		
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(hostname, port);
	
			db = mongoClient.getDB(dbname);
			db.authenticate(ACCOUNT_NAME, ACCOUNT_PASS.toCharArray());

			DBCollection apps = db.getCollection(COLLECTION_NAME);
			DBCursor cur = apps.find();
			while (cur.hasNext()) { 
				DBObject obj = cur.next();
				Map<String, Object> app = (Map<String, Object>) obj;
				appData.add(app);
			}
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			if (mongoClient != null) mongoClient.close();
		}

		return appData;
	}
}
