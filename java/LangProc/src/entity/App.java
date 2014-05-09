package entity;

import java.util.List;
import java.util.Set;

import com.mongodb.DBObject;

public class App {
	
	private static final String MONGO_ID = "_id";
	private static final String STORE_ID = "id";
	private static final String CATEGORY = "category";
	private static final String TITLE = "title";
	private static final String COMPANY = "company";
	private static final String DESCRIPTION = "description";
	private static final String ICON = "icon";
	private static final String LINK = "link";
	private static final String RELEASE_DATE = "releaseDate";
	private static final String IMAGES = "images";
	
	private Object mongoId = null;
	private String storeId = null;
	private String category = null;
	private String title = null;
	private String company = null;
	private String description = null;
	private String icon = null;
	private String link = null;
	private String releaseDate = null;
	private List<String> images = null;
	
	@SuppressWarnings("unchecked")
	public App(DBObject dbObject) {
		Set<String> keys = dbObject.keySet();
		this.mongoId = keys.contains(MONGO_ID) ? dbObject.get(MONGO_ID) : null;
		this.storeId = keys.contains(STORE_ID) ? (String) dbObject.get(STORE_ID) : null;
		this.category = keys.contains(CATEGORY) ? (String) dbObject.get(CATEGORY) : null;
		this.title = keys.contains(TITLE) ? (String) dbObject.get(TITLE) : null;
		this.company = keys.contains(COMPANY) ? (String) dbObject.get(COMPANY) : null;
		this.description = keys.contains(DESCRIPTION) ? (String) dbObject.get(DESCRIPTION) : null;
		this.icon = keys.contains(ICON) ? (String) dbObject.get(ICON) : null;
		this.link = keys.contains(LINK) ? (String) dbObject.get(LINK) : null;
		this.releaseDate = keys.contains(RELEASE_DATE) ? (String) dbObject.get(RELEASE_DATE) : null;
		this.images = keys.contains(IMAGES) ? (List<String>) dbObject.get(IMAGES) : null;
	}
	
	public Object getMongoId() { return mongoId; }
	public String getStoreId() { return storeId; }
	public String getCategory() { return category; }
	public String getTitle() { return title; }
	public String getCompany() { return company; }
	public String getDescription() { return description; }
	public String getIcon() { return icon; }
	public String getLink() { return link; }
	public String getReleaseDate() { return releaseDate; }
	public List<String> getImages() {	return images; }
	
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer("{ ");
		b.append(MONGO_ID + ":\"" + this.mongoId + "\", ");
		b.append(STORE_ID + ":\"" + this.storeId + "\", ");
		b.append(CATEGORY + ":\"" + this.category + "\", ");
		b.append(TITLE + ":\"" + this.title + "\", ");
		b.append(COMPANY + ":\"" + this.company + "\", ");
		b.append(DESCRIPTION + ":\"" + this.description + "\", ");
		b.append(ICON + ":\"" + this.icon + "\", ");
		b.append(LINK + ":\"" + this.link + "\", ");
		b.append(RELEASE_DATE + ":\"" + this.releaseDate + "\", ");
		b.append(IMAGES + ":" + this.images );
		b.append(" }");
		return new String(b);
	}

}
