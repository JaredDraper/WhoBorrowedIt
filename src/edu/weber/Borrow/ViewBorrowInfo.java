package edu.weber.Borrow;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ViewBorrowInfo extends Activity {
	private final Context context = this;
	private DataSource db;
	private String name;
	private String item;
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historyinfo);
		db = new DataSource(context);

		TextView borrow = (TextView) findViewById(R.id.borrowInfo);
		TextView txtDate = (TextView) findViewById(R.id.txtDateBorrowed);
		TextView txtPhone = (TextView) findViewById(R.id.phone);
		CheckBox returned = (CheckBox) findViewById(R.id.checkBox1);
		returned.setOnClickListener(returnedListener);
		Button btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
		btnSendSMS.setOnClickListener(sendSMSListener);

		name = getIntent().getStringExtra("name");
		item = getIntent().getStringExtra("item");

		db.open();
		List<History> historyList = db.getAllHistory();
		for (History hist : historyList) {
			if (hist.getPerson().equals(name) && hist.getItem().equals(item)) {
				Person personInfo = db.getPerson(name);
				borrow.setText(name + " borrowed: " + item +".");
				txtDate.setText("The item was borrowed on " + hist.getDateBorrowed());
				txtPhone.setText("Phone #: " + personInfo.getPhone());
				returned.setChecked(hist.getReturned().equals("true"));
				phone = personInfo.getPhone();
				db.close();
				return;
			}
		}
		db.close();
	}

	private OnClickListener sendSMSListener = new OnClickListener() {

		public void onClick(View v) {
			if(phone != null && phone.length() > 9){
			sendSMS(phone, "Hello " + name + " could you please return my "
					+ item + ".");
			}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewBorrowInfo.this);
				builder.setTitle("Text was not sent");
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage("The phone number was not valid.");
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			}
		}
	};

	private OnClickListener returnedListener = new OnClickListener() {

		public void onClick(View v) {
			CheckBox returned = (CheckBox) v.findViewById(R.id.checkBox1);
			String checked = returned.isChecked() ? "true" : "false";
			db.open();
			db.updateHistory(name + ":" + item, checked);
			db.close();
		}

	};

	// ---sends an SMS message to another device---
	private void sendSMS(String phoneNumber, String message) {
		String DELIVERED = "SMS_DELIVERED";
		String SENT = "SMS_SENT";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);
		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				Toast toast;
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					toast = Toast.makeText(getBaseContext(),
							"reminder message was sent.", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					toast = Toast.makeText(getBaseContext(),
							"reminder message failed to send.",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					toast = Toast.makeText(getBaseContext(),
							"reminder message failed to send.",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					toast = Toast.makeText(getBaseContext(),
							"reminder message failed to send.",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					toast = Toast.makeText(getBaseContext(),
							"reminder message failed to send.",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				}
			}
		}, new IntentFilter(SENT));
		// ---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				Toast toast;
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					toast = Toast.makeText(getBaseContext(),
							"reminder message was delivered.",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				case Activity.RESULT_CANCELED:
					toast = Toast.makeText(getBaseContext(),
							"reminder message was not delivered.",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}

}
