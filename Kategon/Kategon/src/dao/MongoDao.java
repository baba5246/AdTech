package dao;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDao {
	
	private static final String acName = "baba";
	private static final String acPass = "jun";
	

	public void getLocalMongo() {
		
		Mongo m;
		try {
			m = new Mongo();
			DB db = m.getDB("test");

			DBCollection coll = db.getCollection("articles");
			DBCursor cur = coll.find();

			while(cur.hasNext()) {
	            System.out.println(cur.next());
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void getServerMongo() {

		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("dbh23.mongolab.com", 27237);
	
			DB db = mongoClient.getDB("heroku_app24506827");
			db.authenticate(acName, acPass.toCharArray());
			
			DBCollection apps = db.getCollection("apps");
			DBCursor cur = apps.find();
			while (cur.hasNext()) { 
				System.out.println(cur.next());
			}
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			if (mongoClient != null) mongoClient.close();
		}
		
	}
}
