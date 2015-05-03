package coursera.vortex.dailyselfie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity {
	
	public enum Frequency {
		DAILY, HOURLY, TEST_1_MIN, OFF
	};
	
	private static final String TAG = "SettingsActivity";
	
	public static final String SETTINGS_FILE = "DailySelfieSettings.txt";
	
	public static final String NEW_FREQ = "newFrequency";
	public static final String SAVED_FREQ = "savedFrequency";
	
	private static final long INITIAL_ALARM_DELAY = 5 * 1000L;
	private static final long INTERVAL_ONE_MINUTE = 60 * 1000L;
	
	private RadioGroup mFrequencyRadioGroup;
	private RadioButton mDefaultFrequencyButton;
	
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent;
	
	private Frequency mSavedFrequency;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		loadSettings();
		setFrequency(mSavedFrequency);
		
		mFrequencyRadioGroup = (RadioGroup) findViewById(R.id.frequncyGroup);

		// OnClickListener for the Cancel Button,
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "Entered cancelButton.OnClickListener.onClick()");
				finish();
			}
		});

		// Set up OnClickListener for the Submit Button
		final Button submitButton = (Button) findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "Entered submitButton.OnClickListener.onClick()");
				Frequency freq = getFrequency();
				
				setSelfieRemainder(freq);
				saveSettings(freq);
				finish();
            }
		});
	}

	private void setFrequency(Frequency freq) {
		
		switch (freq) {
			case DAILY: {
				mDefaultFrequencyButton = (RadioButton) findViewById(R.id.freqDaily);
				break;
			}
			case HOURLY: {
				mDefaultFrequencyButton = (RadioButton) findViewById(R.id.freqHourly);
				break;
			}
			case TEST_1_MIN: {
				mDefaultFrequencyButton = (RadioButton) findViewById(R.id.freqMinute);
				break;
			}
			case OFF: {
				mDefaultFrequencyButton = (RadioButton) findViewById(R.id.freqOff);
				break;
			}
			default: {
				Log.e(TAG, "setFrequency() error: unrecognized frequency value.");
				return;
			}
		}
		mDefaultFrequencyButton.setChecked(true);
	}
	
	private Frequency getFrequency() {

		switch (mFrequencyRadioGroup.getCheckedRadioButtonId()) {
			case R.id.freqDaily: {
				return Frequency.DAILY;
			}
			case R.id.freqHourly: {
				return Frequency.HOURLY;
			}
			case R.id.freqMinute: {
				return Frequency.TEST_1_MIN;
			}
			case R.id.freqOff: {
				return Frequency.OFF;
			}
			default: {
				Log.e(TAG, "Unable to getFrequency(), return OFF");
				return Frequency.OFF;
			}
		}
	}
	
	// Load saved settings
	private void loadSettings() {
		
		BufferedReader reader = null;
		String freq = null;
		
		try {
			FileInputStream fis = openFileInput(SETTINGS_FILE);
			reader = new BufferedReader(new InputStreamReader(fis));

			if (null != (freq = reader.readLine())) {
				mSavedFrequency = Frequency.valueOf(freq);
			} else {
				mSavedFrequency = Frequency.OFF;
			}
			
		} catch (FileNotFoundException e) {
			
			mSavedFrequency = Frequency.OFF;
			Log.i(TAG, "loadSettings(), File not found, frequency set to OFF.");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
		
	// Save settings to file
	private void saveSettings(Frequency freq) {
		Log.i(TAG, "Saving settings: " + freq.toString());
		
		PrintWriter writer = null;
		try {
			FileOutputStream fos = openFileOutput(SETTINGS_FILE, MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));
			writer.println(freq.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}
	
	private void setSelfieRemainder(Frequency freq) {
		// Get the AlarmManager Service
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationReceiverIntent = new Intent(SettingsActivity.this, AlarmNotificationReceiver.class);
		
		// Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, mNotificationReceiverIntent, 0);
		
		switch (freq) {
			case DAILY: {

				mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
						SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
						AlarmManager.INTERVAL_DAY,
						mNotificationReceiverPendingIntent);
				break;
			}
			case HOURLY: {
				
				mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
						SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
						AlarmManager.INTERVAL_HOUR,
						mNotificationReceiverPendingIntent);
				break;
			}
			case TEST_1_MIN: {
				
				mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
						SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
						INTERVAL_ONE_MINUTE,
						mNotificationReceiverPendingIntent);
				break;
			}
			case OFF: {
				
				 // Cancel all alarms using mNotificationReceiverPendingIntent
		    	 mAlarmManager.cancel(mNotificationReceiverPendingIntent);
		    	 break;
			}
			default: {
				Log.e(TAG, "Invalid frequency value: " + freq);
				return;
			}
		}
	}
}
