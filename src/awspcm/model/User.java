package awspcm.model;

import java.io.Serializable;

public class User implements Serializable{
	
	private static final long serialVersionUID = -6407735726472785843L;
	private String name;
	private String password;
	private int level;
	
	public User() {
	}

	public User(String name, String password) {
		this.name = name;
		this.password = password;		
	}
	
	public User(String name, String password, int level) {
		this.name = name;
		this.password = password;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
