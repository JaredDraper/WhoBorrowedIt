package edu.weber.Borrow;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {
	  /** The view to show the ad. */
	  private AdView adView;	  

	  /* Your ad unit id. Replace with your actual ad unit id. */
	  private static final String AD_UNIT_ID = "ca-app-pub-9127182138174550/1309960025";
	/** Called when the activity is first created. */
	private final Context context = this;
	private DataSource db;
	private TableLayout tableLayout;
	private HashMap<String, String> historyInfo = new HashMap<String, String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);
		// Create an ad.
	    adView = new AdView(this);
	    adView.setAdSize(AdSize.BANNER);
	    adView.setAdUnitId(AD_UNIT_ID);

	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    final TelephonyManager tm =(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
	    AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)	        
	        .addTestDevice(tm.getDeviceId())
	        .build();

	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);

		// database stuff
		db = new DataSource(this);
		tableLayout = (TableLayout) findViewById(R.id.queryTableLayout);
		  // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    LinearLayout layout = (LinearLayout) findViewById(R.id.advertisement);
	    layout.addView(adView);

		Button btnAddAssignment = (Button) findViewById(R.id.btnLendItem);
		btnAddAssignment.setOnClickListener(btnMakeAssignmentListener);
		Button btnViewPeople = (Button) findViewById(R.id.btnViewPeople);
		btnViewPeople.setOnClickListener(viewPeopleListener);
		Button btnViewItems = (Button) findViewById(R.id.btnViewItems);
		btnViewItems.setOnClickListener(viewItemsListener);

		refresh();

	}

	private void refresh() {
		db.open();
		tableLayout.removeAllViews();
		List<History> historyList = db.getAllHistory();
		db.close();
		historyInfo.clear();
		for (int index = 0; index < historyList.size(); index++) {
			History history = historyList.get(index);
			String temp = history.getPerson() + " - " + history.getItem();
			makeNewHistoryGUI(temp, index);
			historyInfo.put(temp, String.valueOf(index));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null) {
		      adView.resume();
		    }
	}

	@Override
	protected void onPause() {
		if (adView != null) {
		      adView.pause();
		    }
		super.onPause();
	}
	
	  /** Called before the activity is destroyed. */
	  @Override
	  public void onDestroy() {
	    // Destroy the AdView.
	    if (adView != null) {
	      adView.destroy();
	    }
	    super.onDestroy();
	  }

	private void makeNewHistoryGUI(String history, int index) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View HistoryView = inflater.inflate(R.layout.history_item_view, null);

		Button btnHistory = (Button) HistoryView.findViewById(R.id.historyItem);
		btnHistory.setText(history);
		btnHistory.setOnClickListener(viewBorrowInfoListener);

		Button btnNewDelete = (Button) HistoryView.findViewById(R.id.btnDelete);
		btnNewDelete.setOnClickListener(btnDeleteHistoryListener);
		tableLayout.addView(HistoryView, index);
	}

	public OnClickListener viewPeopleListener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(context, ViewPeople.class);
			startActivity(i);
		}
	};

	public OnClickListener viewItemsListener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(context, ViewItems.class);
			startActivity(i);
		}
	};

	public OnClickListener viewBorrowInfoListener = new OnClickListener() {

		public void onClick(View v) {
			Button editPerson = (Button) v.findViewById(R.id.historyItem);
			String historyInfo = editPerson.getText().toString();
			String[] list = historyInfo.split("-");

			Intent i = new Intent(context, ViewBorrowInfo.class);
			i.putExtra("name", list[0].trim());
			i.putExtra("item", list[1].trim());
			startActivity(i);
		}
	};

	public OnClickListener btnDeleteHistoryListener = new OnClickListener() {

		public void onClick(View v) {
			TableRow btnTableRow = (TableRow) v.getParent();
			Button btnSearch = (Button) btnTableRow
					.findViewById(R.id.historyItem);
			String txtHistory = btnSearch.getText().toString();
			db.open();
			db.deleteHistory(txtHistory);			
			tableLayout.removeAllViews();
			List<History> historyList = db.getAllHistory();
			db.close();
			for (int index = 0; index < historyList.size(); index++) {
				History history = historyList.get(index);
				String temp = history.getPerson() + " - " + history.getItem();
				makeNewHistoryGUI(temp, index);
				historyInfo.put(temp, String.valueOf(index));
			}
		}
	};

	private OnClickListener btnMakeAssignmentListener = new OnClickListener() {

		public void onClick(View v) {
			Intent i = new Intent(context, ViewLendAnItem.class);
			startActivityForResult(i, 1);		
		}

	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		refresh();
	}

}