package dao;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import entity.App;

public class AppDao {

	public static final int APP_STORE_TYPE = 1;
	public static final int GOOGLE_PLAY_TYPE = 2;
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
	
	public Map<String, App> getLocalAppStoreApps() {
		return getLocalDB(APP_STORE_DB_NAME);
	}
	
	public Map<String, App> getLocalGooglePlayApps() {
		return getLocalDB(GOOGLE_PLAY_DB_NAME);		
	}
	
	private Map<String, App> getLocalDB(String dbname) {
		
		Map<String, App> appData = new HashMap<String, App>();
		
		Mongo m = null;
		try {
			m = new Mongo();
			db = m.getDB(dbname);

			DBCollection collections = db.getCollection(COLLECTION_NAME);
			DBCursor cur = collections.find();

			while(cur.hasNext()) {
				App app = new App(cur.next());
				appData.put(app.getStoreId(), app);
	        }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (m != null) m.close();
		}
		
		return appData;

	}
	
	public Map<String, App> getAppStoreDB() {
		return getServerDB(APP_STORE_HOST, APP_STORE_PORT, APP_STORE_DB_NAME);
	}
	
	public Map<String, App> getGooglePlayDB() {
		return getServerDB(GOOGLE_PLAY_HOST, GOOGLE_PLAY_PORT, GOOGLE_PLAY_DB_NAME);
	}
	
	private Map<String, App> getServerDB(String hostname, int port, String dbname) {
		
		Map<String, App> appData = new HashMap<String, App>();
		
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(hostname, port);
	
			db = mongoClient.getDB(dbname);
			db.authenticate(ACCOUNT_NAME, ACCOUNT_PASS.toCharArray());

			DBCollection collections = db.getCollection(COLLECTION_NAME);
			DBCursor cur = collections.find();
			
			while (cur.hasNext()) { 
				App app = new App(cur.next());
				appData.put(app.getStoreId(), app);
			}
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			if (mongoClient != null) mongoClient.close();
		}

		return appData;
	}
}
