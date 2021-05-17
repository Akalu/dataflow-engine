package computing.model;

import com.google.gson.Gson;

import computing.annotation.Entity;

import java.io.Serializable;


/**
 * Model class to be mapped on database table
 * Must be annotated by @Entity annotation
 *
 * NOTE name conventions: name of database table must be a plural alias in this
 * example:
 * 
 * ---------------------
 * 
 * ! alias ! tablename !
 * 
 * ---------------------
 * 
 * ! fruit ! fruits !
 * 
 * ---------------------
 * 
 */
@Entity(alias = "fruit")
public class Fruit implements Serializable {
	public static final Gson gson = new Gson();

	private static final long serialVersionUID = 3064305676199978641L;

	private String name;
	private int amount;
	private String taste;

	public Fruit() {
	}

	public Fruit(String name, int amount, String taste) {
		this.name = name;
		this.amount = amount;
		this.taste = taste;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getTaste() {
		return taste;
	}

	public void setTaste(String taste) {
		this.taste = taste;
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}
