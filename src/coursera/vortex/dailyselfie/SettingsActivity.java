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
import android.content.Intent;
import android.os.Bundle;
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
	
	private RadioGroup mFrequencyRadioGroup;
	private RadioButton mDefaultFrequencyButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setFrequency(getIntent().getStringExtra("freq"));
		
		mFrequencyRadioGroup = (RadioGroup) findViewById(R.id.frequncyGroup);

		// OnClickListener for the Cancel Button,
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "Entered cancelButton.OnClickListener.onClick()");

				setResult(RESULT_CANCELED);
				finish();
			}
		});

		// Set up OnClickListener for the Submit Button
		final Button submitButton = (Button) findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Entered submitButton.OnClickListener.onClick()");

				String frequency = getFrequency().toString();
				
				// Send the frequency back in the intent
				Intent data = new Intent();
				data.putExtra("freq", frequency);

				setResult(RESULT_OK, data);
				finish();
            }
		});
	}

	private void setFrequency(String freq) {
		
		switch (Frequency.valueOf(freq)) {
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
}
