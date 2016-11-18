package edu.weber.Borrow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

public class ViewLendAnItem extends Activity {

	final Context context = this;
	private Spinner spinner1, spinner2;
	private Button btnSubmit;
	private DataSource db;
	List<String> itemList = new ArrayList<String>();
	List<String> personList = new ArrayList<String>();
	ArrayAdapter<String> itemDataAdapter;
	ArrayAdapter<String> personDataAdapter;
	String assignPerson;
	String assignItem;
	DatePicker dp;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_history_view);
		db = new DataSource(context);
		itemDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, itemList);
		personDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, personList);
		db.open();
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		itemList.clear();
		List<Item> items = db.getAllItems();
		for (int index = 0; index < items.size(); index++) {
			Item item = items.get(index);
			itemList.add(item.getItem());
		}
		itemDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(itemDataAdapter);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		personList.clear();
		List<Person> people = db.getAllPersons();
		for (int index = 0; index < people.size(); index++) {
			Person person = people.get(index);
			personList.add(person.getPerson());
		}
		spinner1.setAdapter(personDataAdapter);
		spinner1.setOnItemSelectedListener(new CustomOnPersonSelectedListener());
		dp = (DatePicker) findViewById(R.id.datePicker);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			dp.setCalendarViewShown(false);
		}
		db.close();
		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		btnSubmit.setOnClickListener(submitListener);
	}

	public class CustomOnPersonSelectedListener implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			assignPerson = parent.getItemAtPosition(pos).toString();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// Do nothing
		}
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			assignItem = parent.getItemAtPosition(pos).toString();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// Do Nothing
		}
	}

	OnClickListener submitListener = new OnClickListener() {

		public void onClick(View v) {
			if (validate()) {
				String strdate = "";
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				cal.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
				if (cal != null) {
					strdate = sdf.format(cal.getTime());
				}
				db.open();
				db.createHistory(assignPerson, assignItem, strdate, "false");
				db.close();

				Toast.makeText(context, "You have successfully lent an item",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}

		private boolean validate() {
			if (itemDataAdapter.isEmpty() || personDataAdapter.isEmpty()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewLendAnItem.this);
				builder.setTitle(R.string.missingText);
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage(R.string.noItemSelected);
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
				return false;
			} 
			db.open();
			for(History history : db.getAllHistory()){
				if(history.getItem().equals(assignItem) && history.getPerson().equals(assignPerson)){
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ViewLendAnItem.this);
					builder.setTitle(R.string.alreadyLent);
					builder.setPositiveButton(R.string.OK, null);
					builder.setMessage(R.string.alreadyLentMessage);
					AlertDialog errorDialog = builder.create();
					errorDialog.show();
					return false;
				}
			}
			return true;
		}
	};
}
