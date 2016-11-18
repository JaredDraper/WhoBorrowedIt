package edu.weber.Borrow;

public class Person {
	private long id;
	private String person;
	private String phone;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return person;
	}
}