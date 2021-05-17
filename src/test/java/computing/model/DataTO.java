package computing.model;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import computing.annotation.Entity;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity(alias="record")
public class DataTO implements Serializable {
	private static final long serialVersionUID = 2357113637981299253L;
	
	public static final Gson gson = new Gson();
	public static final Type mapType = new TypeToken<Map<String, String>>(){}.getType();

	@SerializedName("_id") 
	private String id;
	
	@SerializedName("lock") 
	private Boolean lock = false;

	@SerializedName("owner") 
	private String owner;

	@SerializedName("data") 
	private Map<String, String> data;
	
	public DataTO() {
		this.data = new LinkedHashMap<>();
		this.owner = "";
	}
	
	public DataTO(String id, String owner) {
		this.data = new LinkedHashMap<>();
		this.owner = owner;
		this.id = id;
	}
	
	public DataTO(String id, String owner, Boolean locked) {
		this.data = new LinkedHashMap<>();
		this.owner = owner;
		this.lock = locked;
		this.id = id;
	}
	
	public DataTO(String id, String owner, Boolean locked, String data) {
		this.owner = owner;
		this.lock = locked;
		this.id = id;
		setDataFromJson(data);
	}



	public String get(String refKey) {
		return this.data.getOrDefault(refKey, null);
	}
	
	public Boolean contains(String refKey) {
		return this.data.containsKey(refKey);
	}
	
	public String add(String refKey, String value) {
		return this.data.put(refKey, value);
	}
	
	@Override
	public String toString() {
		return gson.toJson(this);
	}
	
	public static DataTO fromJson(String json) {
		return gson.fromJson(json, DataTO.class);
	}

	public Map<String, String> getData() {
		return this.data;
	}
	
	public String dataAsJson() {
		return gson.toJson(this.data);
	}
	
	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public void setDataFromJson(String json) {
		this.data =  gson.fromJson(json, mapType);
	}

	public String getId() {
		return id;
	}

	public void setId(String key) {
		this.id = key;
	}


	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void unlock() {
		this.lock = false;
		this.owner = "";
	}

	public synchronized Boolean lock(String owner) {
		if (!this.lock) {
			this.lock = true;
			this.owner = owner;
			return true;
		}
		return false;
	}

	public Boolean getLock() {
		return lock;
	}

	public void setLock(Boolean locked) {
		this.lock = locked;
	}

}
