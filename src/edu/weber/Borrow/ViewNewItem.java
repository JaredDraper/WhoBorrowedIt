package edu.weber.Borrow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ViewNewItem extends Activity {
	private final Context context = this;
	private DataSource db;
	private boolean editMode = false;
	private EditText editItem;
	private String oldItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_item_view);
		db = new DataSource(context);

		editItem = (EditText) findViewById(R.id.txtItem);
		String itemName = getIntent().getStringExtra("item");
		if (itemName
				!= null) {
			editMode = true;
			oldItem = itemName;
			editItem.setText(itemName);
		}

		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(btnSaveItemListener);
	}

	public OnClickListener btnSaveItemListener = new OnClickListener() {
		public void onClick(View v) {
			String itemName = editItem.getText().toString().trim();
			if (itemName.isEmpty()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewNewItem.this);
				builder.setTitle(R.string.missingText);
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage(R.string.missingMessage);
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			}else if(itemName.length() > 40){
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewNewItem.this);
				builder.setTitle("Name Too Long");
				System.out.println();
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage(R.string.itemTooLong);
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			}else {
				db.open();
				if (editMode) {
					db.updateItem(oldItem.trim(), itemName);
				} else {
					Item item = db.getItem(itemName);
					if (item != null) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								ViewNewItem.this);
						builder.setTitle("no duplication allowed");
						builder.setPositiveButton(R.string.OK, null);
						builder.setMessage("This item already exists");
						AlertDialog errorDialog = builder.create();
						errorDialog.show();
						db.close();
					} else {
						db.createItem(itemName);
						db.close();
						finish();
					}

				}

			}

		}
	};

}
