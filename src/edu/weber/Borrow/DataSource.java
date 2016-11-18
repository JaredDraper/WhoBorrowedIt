package edu.weber.Borrow;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allPersonColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_PERSON, MySQLiteHelper.COLUMN_PHONE };
	
	private String[] allItemColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_ITEM };
	
	private String[] allHistoryColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_PERSONID,
			MySQLiteHelper.COLUMN_ITEMID, MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_RETURNED };

	public DataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createPerson(String person, String phone) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PERSON, person);
		values.put(MySQLiteHelper.COLUMN_PHONE, phone);
		database.insert(MySQLiteHelper.TABLE_PERSONS, null,
				values);
	}
	
	public void createItem(String item) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ITEM, item);
		database.insert(MySQLiteHelper.TABLE_ITEMS, null,
				values);
	}
	
	public void createHistory(String person, String item, String date, String returned) {
		ContentValues personValues = new ContentValues();
		personValues.put(MySQLiteHelper.COLUMN_PERSON, person);
		Cursor prepCursor = database.query(MySQLiteHelper.TABLE_PERSONS, allPersonColumns,
				MySQLiteHelper.COLUMN_PERSON +" = '"+ person +"'", null, null, null, null);
		prepCursor.moveToFirst();
		String personId = prepCursor.getString(0);
		
		ContentValues itemValues = new ContentValues();
		itemValues.put(MySQLiteHelper.COLUMN_ITEM, item);
		Cursor itemCursor = database.query(MySQLiteHelper.TABLE_ITEMS, allItemColumns,
				MySQLiteHelper.COLUMN_ITEM +" = '"+ item +"'", null, null, null, null);
		itemCursor.moveToFirst();
		String itemId = itemCursor.getString(0);
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PERSONID, personId);
		values.put(MySQLiteHelper.COLUMN_ITEMID, itemId);
		values.put(MySQLiteHelper.COLUMN_DATE, date);
		values.put(MySQLiteHelper.COLUMN_RETURNED, returned);
	    database.insert(MySQLiteHelper.TABLE_HISTORY, null, values);
	}

	public void deletePerson(String oldPerson) {
		ContentValues prepValues = new ContentValues();
		prepValues.put(MySQLiteHelper.COLUMN_PERSON, oldPerson);
		Cursor prepCursor = database.query(MySQLiteHelper.TABLE_PERSONS, allPersonColumns,
				MySQLiteHelper.COLUMN_PERSON +" = '"+ oldPerson+"'", null, null, null, null);
		prepCursor.moveToFirst();
		String id = prepCursor.getString(0);
		database.delete(MySQLiteHelper.TABLE_PERSONS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}
	
	public void deleteItem(String oldTitle) {
		ContentValues prepValues = new ContentValues();
		prepValues.put(MySQLiteHelper.COLUMN_ITEM, oldTitle);
		Cursor prepCursor = database.query(MySQLiteHelper.TABLE_ITEMS, allItemColumns,
				MySQLiteHelper.COLUMN_ITEM +" = '"+ oldTitle+"'", null, null, null, null);
		prepCursor.moveToFirst();
		String id = prepCursor.getString(0);
		
		database.delete(MySQLiteHelper.TABLE_ITEMS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}
	
	public void deleteHistory(String values) {	
		String[] list = values.split("-");
		Cursor prepCursor = database.query(MySQLiteHelper.TABLE_PERSONS, allPersonColumns,
				MySQLiteHelper.COLUMN_PERSON +" = '"+ list[0].trim()+"'", null, null, null, null);
		prepCursor.moveToFirst();
		String personId = prepCursor.getString(0);

		Cursor itemCursor = database.query(MySQLiteHelper.TABLE_ITEMS, allItemColumns,
				MySQLiteHelper.COLUMN_ITEM +" = '"+ list[1].trim()+"'", null, null, null, null);
		itemCursor.moveToFirst();
		String itemId = itemCursor.getString(0);

		database.delete(MySQLiteHelper.TABLE_HISTORY,MySQLiteHelper.COLUMN_PERSONID + " = '"+ personId + "' AND " + MySQLiteHelper.COLUMN_ITEMID + " = '" + itemId + "'", null);
	}
	
	public void updateHistory(String values, String checked) {	
		String[] list = values.split(":");
		Cursor prepCursor = database.query(MySQLiteHelper.TABLE_PERSONS, allPersonColumns,
				MySQLiteHelper.COLUMN_PERSON +" = '"+ list[0].trim()+"'", null, null, null, null);
		prepCursor.moveToFirst();
		String personId = prepCursor.getString(0);

		Cursor itemCursor = database.query(MySQLiteHelper.TABLE_ITEMS, allItemColumns,
				MySQLiteHelper.COLUMN_ITEM +" = '"+ list[1].trim()+"'", null, null, null, null);
		itemCursor.moveToFirst();
		String itemId = itemCursor.getString(0);
		ContentValues prepValues = new ContentValues();
		prepValues.put(MySQLiteHelper.COLUMN_RETURNED, checked);
		database.update(MySQLiteHelper.TABLE_HISTORY, prepValues, MySQLiteHelper.COLUMN_PERSONID + " = '"+ personId + "' AND " + MySQLiteHelper.COLUMN_ITEMID + " = '" + itemId + "'", null);
	}

	public List<Person> getAllPersons() {
		List<Person> persons = new ArrayList<Person>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_PERSONS,
				allPersonColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Person person = cursorToPerson(cursor);
			persons.add(person);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return persons;
	}

	public List<Item> getAllItems() {
		List<Item> items = new ArrayList<Item>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
				allItemColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return items;
	}
	
	public List<History> getAllHistory() {
		List<History> historyList = new ArrayList<History>();
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_HISTORY,
				allHistoryColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {	
			History history = new History();
			history.setId(cursor.getLong(0));
			history.setReturned(cursor.getString(4));
			history.setDateBorrowed(cursor.getString(3));
			String personId = cursor.getString(1);
			String itemId = cursor.getString(2); 
			ContentValues itemsValues = new ContentValues();
			itemsValues.put(MySQLiteHelper.COLUMN_ITEM, itemId);
			Cursor prepCursor = database.query(MySQLiteHelper.TABLE_ITEMS, allItemColumns,
					MySQLiteHelper.COLUMN_ID +" = '"+ itemId +"'", null, null, null, null);
			prepCursor.moveToFirst();
			history.setItem(prepCursor.getString(1));
			ContentValues personValues = new ContentValues();
			personValues.put(MySQLiteHelper.COLUMN_PERSON, personId);
			Cursor cursorId = database.query(MySQLiteHelper.TABLE_PERSONS, allPersonColumns,
					MySQLiteHelper.COLUMN_ID +" = '"+ personId +"'", null, null, null, null);
			cursorId.moveToFirst();
			history.setPerson(cursorId.getString(1));
			//history.add(person + ": " + item);
			cursor.moveToNext();
			historyList.add(history);
		}
		// Make sure to close the cursor
		cursor.close();
		return historyList;
	}
	
	private Person cursorToPerson(Cursor cursor) {
		Person person = new Person();
		person.setId(cursor.getLong(0));
		person.setPerson(cursor.getString(1));
		person.setPhone(cursor.getString(2));
		return person;
	}

	private Item cursorToItem(Cursor cursor) {
		Item item = new Item();
		item.setId(cursor.getLong(0));
		item.setItem(cursor.getString(1));
		return item;
	}
	
/*	private History cursorToHistory(Cursor cursor) {
		History history = new History();
		history.setId(cursor.getLong(0));
		history.setPerson(cursor.getString(1));
		history.setItem(cursor.getString(2));
		history.setReturned(cursor.getString(3));
		return history;
	}*/

	public void updateItem(String oldTitle, String item) {
		ContentValues prepValues = new ContentValues();
		prepValues.put(MySQLiteHelper.COLUMN_ITEM, oldTitle);
		Cursor prepCursor = database.query(MySQLiteHelper.TABLE_ITEMS, allItemColumns,
				MySQLiteHelper.COLUMN_ITEM +" = '"+ oldTitle+"'", null, null, null, null);
		prepCursor.moveToFirst();
		String id = prepCursor.getString(0);
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ITEM, item);
		String whereClause = MySQLiteHelper.COLUMN_ID + " = ?";
		String[] whereArgs = new String[]{String.valueOf(id)};
		database.update(MySQLiteHelper.TABLE_ITEMS, values, whereClause, whereArgs);		
	}
	
	public void updatePerson(String oldPerson, String person, String phone) {
		ContentValues prepValues = new ContentValues();
		prepValues.put(MySQLiteHelper.COLUMN_PERSON, oldPerson);
		Cursor prepCursor = database.query(MySQLiteHelper.TABLE_PERSONS, allPersonColumns,
				MySQLiteHelper.COLUMN_PERSON +" = '"+ oldPerson+"'", null, null, null, null);
		prepCursor.moveToFirst();
		String id = prepCursor.getString(0);
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PERSON, person);
		values.put(MySQLiteHelper.COLUMN_PHONE, phone);
		String whereClause = MySQLiteHelper.COLUMN_ID + " = ?";
		String[] whereArgs = new String[]{String.valueOf(id)};
		database.update(MySQLiteHelper.TABLE_PERSONS, values, whereClause, whereArgs);		
	}

	public Person getPerson(String personName) {
		List<Person> people = getAllPersons();
		for(Person person: people){
			if(person.getPerson().trim().toLowerCase().equals(personName.trim().toLowerCase())){
				return person;
			}
		}
		return null;
	}

	public Item getItem(String itemName) {
		List<Item>items = getAllItems();
		for(Item item: items){
			if(item.getItem().trim().toLowerCase().equals(itemName.trim().toLowerCase())){
				return item;
			}
		}
		return null;
	}

	public boolean checkIfUsedItem(String item) {
		List<History> histories = getAllHistory();
		for(History history: histories){
			if(history.getItem().trim().equals(item.trim())){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIfUsedPerson(String person) {
		List<History> histories = getAllHistory();
		for(History history: histories){
			if(history.getPerson().trim().equals(person.trim())){
				return true;
			}
		}
		return false;
	}
}
