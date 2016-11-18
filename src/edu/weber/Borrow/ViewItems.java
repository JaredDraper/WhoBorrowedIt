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

public class ViewItems extends Activity {
	final Context context = this;
	private Button btnAddItem;
	private DataSource db;
	private HashMap<String, String> itemInfo = new HashMap<String, String>();
	private TableLayout tableLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.items_view);
		db = new DataSource(this);

		btnAddItem = (Button) findViewById(R.id.btnAddItem);
		btnAddItem.setOnClickListener(addItemListener);
		tableLayout = (TableLayout) findViewById(R.id.queryTableLayout);
		refreshTableLayout();
	}

	private void refreshTableLayout() {

		tableLayout.removeAllViews();
		db.open();
		List<Item> items = db.getAllItems();
		db.close();
		itemInfo.clear();
		for (int index = 0; index < items.size(); index++) {
			makeNewItemGUI(items.get(index), index);
		}
	}

	private OnClickListener addItemListener = new OnClickListener() {
		public void onClick(View v) {

			Intent i = new Intent(context, ViewNewItem.class);
			startActivityForResult(i, 0);
		}
	};

	private void makeNewItemGUI(Item item, int index) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View NewVideoView = inflater.inflate(R.layout.row_view, null);

		Button btnItem = (Button) NewVideoView.findViewById(R.id.Entry);
		btnItem.setText(item.getItem());
		btnItem.setOnClickListener(btnEditItemListener);

		Button btnNewDelete = (Button) NewVideoView
				.findViewById(R.id.btnNewDelete);
		btnNewDelete.setOnClickListener(btnDeleteItemListener);
		tableLayout.addView(NewVideoView, index);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		refreshTableLayout();
	}

	public OnClickListener btnEditItemListener = new OnClickListener() {
		public void onClick(View v) {
			TableRow btnTableRow = (TableRow) v.getParent();
			Button btnSearch = (Button) btnTableRow.findViewById(R.id.Entry);
			String item = btnSearch.getText().toString();

			Intent i = new Intent(context, ViewNewItem.class);
			i.putExtra("item", item);
			startActivityForResult(i, 0);
		}
	};

	public OnClickListener btnDeleteItemListener = new OnClickListener() {
		public void onClick(View v) {
			TableRow btnTableRow = (TableRow) v.getParent();
			Button btnSearch = (Button) btnTableRow.findViewById(R.id.Entry);
			String item = btnSearch.getText().toString();
			db.open();
			boolean used = db.checkIfUsedItem(item);
			if(used){
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewItems.this);
				builder.setTitle("Cannot Delete Item");
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage("Item is attached to a person, you cannot delete item while it is being used.");
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			} else{
				db.deleteItem(item);			
				tableLayout.removeAllViews();
				refreshTableLayout();
			}
			db.close();
		}
	};

}
