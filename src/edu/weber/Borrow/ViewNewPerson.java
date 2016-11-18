package edu.weber.Borrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

public class ViewNewPerson extends Activity {
	final Context context = this;
	private static final int CONTACT_PICKER_RESULT = 1001;
	private static final String DEBUG_TAG = null;
	private DataSource db;
	List<String> personList = new ArrayList<String>();
	HashMap<String, String> personInfo = new HashMap<String, String>();
	TableLayout tableLayout;
	boolean editMode = false;
	EditText editPerson;
	EditText editPhone;
	String oldPerson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_person_view);
		db = new DataSource(context);

		editPerson = (EditText) findViewById(R.id.txtPerson);
		editPhone = (EditText) findViewById(R.id.txtPhone);
		Button getPersonFromContacts = (Button) findViewById(R.id.btnContacts);
		getPersonFromContacts.setOnClickListener(btnGetFromContactsListener);
		String personName = getIntent().getStringExtra("person");
		if (personName != null) {
			db.open();
			Person person = db.getPerson(personName);
			db.close();
			editMode = true;
			oldPerson = personName;
			editPerson.setText(person.getPerson());
			editPhone.setText(person.getPhone());
		}

		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(btnSavePersonListener);

	}

	public OnClickListener btnSavePersonListener = new OnClickListener() {
		public void onClick(View v) {
			String personName = editPerson.getText().toString().trim();
			String phone = editPhone.getText().toString().trim();
			if (personName.isEmpty()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewNewPerson.this);
				builder.setTitle(R.string.missingText);
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage(R.string.missingMessage);
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			} else {
				db.open();
				if (editMode) {
					db.updatePerson(oldPerson.trim(), personName, phone);
				} else {
					Person person = db.getPerson(personName);
					if (person != null) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								ViewNewPerson.this);
						builder.setTitle("no duplication allowed");
						builder.setPositiveButton(R.string.OK, null);
						builder.setMessage("This person already exists");
						AlertDialog errorDialog = builder.create();
						errorDialog.show();
						db.close();
					} else {
						db.createPerson(personName, phone);
						db.close();
						finish();
					}
				}			
			}			
		}
	};

	private OnClickListener btnGetFromContactsListener = new OnClickListener() {

		public void onClick(View v) {
			doLaunchContactPicker(v);

		}
	};

	public void doLaunchContactPicker(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				Cursor cursorPhone = null;
				String phone = "";
				String name = "";
				try {
					Uri result = data.getData();

					// get the contact id from the Uri
					String id = result.getLastPathSegment();

					// query for phone and name				
					 cursorPhone = getContentResolver().query(  
		                      Phone.CONTENT_URI, null,  
		                      Phone._ID + "=?",  
		                      new String[]{id}, null);

					int phoneIdx = cursorPhone.getColumnIndex(Phone.DATA);
					int nameIdx = cursorPhone.getColumnIndex(Phone.DISPLAY_NAME);

					// get the phone and name
					if (cursorPhone.moveToFirst()) {
						phone = cursorPhone.getString(phoneIdx);
						name = cursorPhone.getString(nameIdx);
						Log.v(DEBUG_TAG, "Got phone: " + phone);
					} else {
						Log.w(DEBUG_TAG, "No results");
					}
				} catch (Exception e) {
					Log.e(DEBUG_TAG, "Failed to get data", e);
				} finally {
					if (cursorPhone != null) {
						cursorPhone.close();
					}
					EditText phoneEntry = (EditText) findViewById(R.id.txtPhone);
					EditText nameEntry = (EditText) findViewById(R.id.txtPerson);
					phoneEntry.setText(phone);
					nameEntry.setText(name);
					if (phone.length() == 0) {
						Toast.makeText(this, "No phone found for contact.",
								Toast.LENGTH_LONG).show();
					}
				}
				break;
			}
		}
	}

}
