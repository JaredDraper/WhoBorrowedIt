package edu.weber.Borrow;

public class History {
	private long id;
	private String person;
	private String item;
	private String dateBorrowed;
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the person
	 */
	public String getPerson() {
		return person;
	}
	/**
	 * @param person the person to set
	 */
	public void setPerson(String person) {
		this.person = person;
	}
	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		this.item = item;
	}
	/**
	 * @return the dateBorrowed
	 */
	public String getDateBorrowed() {
		return dateBorrowed;
	}
	/**
	 * @param dateBorrowed the dateBorrowed to set
	 */
	public void setDateBorrowed(String dateBorrowed) {
		this.dateBorrowed = dateBorrowed;
	}
	/**
	 * @return the returned
	 */
	public String getReturned() {
		return returned;
	}
	/**
	 * @param returned the returned to set
	 */
	public void setReturned(String returned) {
		this.returned = returned;
	}
	private String returned;
}
