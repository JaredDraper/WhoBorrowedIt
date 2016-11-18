package edu.weber.Borrow;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ViewPeople extends Activity {
	final Context context = this;
	private Button btnAddPerson;
	private DataSource db;
	private HashMap<String, String> personInfo = new HashMap<String, String>();
	private TableLayout tableLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_view);
		db = new DataSource(this);

		btnAddPerson = (Button) findViewById(R.id.btnAddPerson);
		btnAddPerson.setOnClickListener(addPersonListener);
		tableLayout = (TableLayout) findViewById(R.id.queryTableLayout);
		refreshTableLayout();
	}

	private void refreshTableLayout() {

		tableLayout.removeAllViews();
		db.open();
		List<Person> people = db.getAllPersons();
		db.close();
		personInfo.clear();
		for (int index = 0; index < people.size(); index++) {
			makeNewPersonGUI(people.get(index), index);
			Person person = people.get(index);
			personInfo.put(person.getPerson(), person.getPhone());
		}
	}

	private OnClickListener addPersonListener = new OnClickListener() {
		public void onClick(View v) {

			Intent i = new Intent(context, ViewNewPerson.class);
			startActivityForResult(i, 0);
		}
	};

	private void makeNewPersonGUI(Person person, int index) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View NewVideoView = inflater.inflate(R.layout.row_view, null);

		Button btnNewVideo = (Button) NewVideoView.findViewById(R.id.Entry);
		btnNewVideo.setText(person.getPerson());

		btnNewVideo.setOnClickListener(btnEditPersonListener);

		//Button btnNewEdit = (Button) NewVideoView.findViewById(R.id.btnNewEdit);
		//btnNewEdit.setOnClickListener(btnEditPersonListener);

		Button btnNewDelete = (Button) NewVideoView
				.findViewById(R.id.btnNewDelete);
		btnNewDelete.setOnClickListener(btnDeletePersonListener);
		tableLayout.addView(NewVideoView, index);
	}

	public OnClickListener btnEditPersonListener = new OnClickListener() {
		public void onClick(View v) {
			TableRow btnTableRow = (TableRow) v.getParent();
			Button btnSearch = (Button) btnTableRow.findViewById(R.id.Entry);

			String person = btnSearch.getText().toString();
			Intent i = new Intent(context, ViewNewPerson.class);
			i.putExtra("person", person);
			startActivityForResult(i, 0);

		}
	};

	public OnClickListener btnDeletePersonListener = new OnClickListener() {
		public void onClick(View v) {
			TableRow btnTableRow = (TableRow) v.getParent();
			Button btnSearch = (Button) btnTableRow.findViewById(R.id.Entry);
			String person = btnSearch.getText().toString();
			db.open();
			boolean used = db.checkIfUsedPerson(person);			
			if(used){
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewPeople.this);
				builder.setTitle("Cannot delete person");
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage("Person is attached to a item, you cannot delete person while it is being used.");
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			}else{
			db.deletePerson(person);			
			tableLayout.removeAllViews();
			refreshTableLayout();
			}
			db.close();
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		refreshTableLayout();
	}

}
