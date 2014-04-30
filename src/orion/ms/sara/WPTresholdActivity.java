package orion.ms.sara;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.app.AlertDialog;

public class WPTresholdActivity extends Activity {

	private Intent intentGaneralSetting;

	private TextToSpeech tts = null;

	private TextView textViewWPTreshold = null;

	private Button SaveSettingButton = null;

	// declare change distance time treshold buttons
	private Button IncreaseWPTresholdButton = null;
	private Button DecreaseWPTresholdButton = null;

	private int WPTreshold = 1;
	private int step = 1;

	private AlertDialog.Builder alertDialog;

	public SharedPreferences settings;
	public SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wp_treshold);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Restore preferences
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		this.editor = settings.edit();
		LoadPref();

		// save setting button
		SaveSettingButton = (Button) findViewById(R.id.SaveSettingButton);
		SaveSettingButton.setContentDescription(getResources().getString(
				R.string.savebutton));

		// intent creation
		intentGaneralSetting = new Intent(WPTresholdActivity.this, GeneralSettingActivity.class);

		// distance time treshold view
		textViewWPTreshold = (TextView) findViewById(R.id.WPTresholdTextView);
		textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
		textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));

		// increase&decrease distance time treshold button
		IncreaseWPTresholdButton = (Button) findViewById(R.id.IncreaseWPTresholdButton);
		IncreaseWPTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.wptreshold));
		DecreaseWPTresholdButton = (Button) findViewById(R.id.DecreaseWPTresholdButton);
		DecreaseWPTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.wptreshold));

		// OnClickListener creation
		View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == IncreaseWPTresholdButton) {
					if (WPTreshold >= 0 && WPTreshold < 10) {
						step = 1;
						WPTreshold = WPTreshold + step;
						textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
						textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));
						tts.speak(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres), TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase wp treshold 1");
					} else if (WPTreshold >= 10 && WPTreshold < 100) {
						step = 10;
						WPTreshold = WPTreshold + step;
						textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
						textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));
						tts.speak(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres), TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase wp treshold 10");
					} else if (WPTreshold >= 100 && WPTreshold < 1000) {
						step = 100;
						WPTreshold = WPTreshold + step;
						textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
						textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));
						tts.speak(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres), TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase wp treshold 100");
					} else if (WPTreshold == 1000) {
						tts.speak(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres) + " " + getResources().getString(R.string.cant_increase), TextToSpeech.QUEUE_FLUSH, null);
					}

				}
				if (v == DecreaseWPTresholdButton) {
					if (WPTreshold > 1 && WPTreshold <= 10) {
						step = 1;
						WPTreshold = WPTreshold - step;
						textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
						textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));
						tts.speak(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres), TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease wp treshold 1");
					} else if (WPTreshold > 10 && WPTreshold <= 100) {
						step = 10;
						WPTreshold = WPTreshold - step;
						textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
						textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));
						tts.speak(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres), TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease wp treshold 10");
					} else if (WPTreshold > 100 && WPTreshold <= 1000) {
						step = 100;
						WPTreshold = WPTreshold - step;
						textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
						textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));
						tts.speak(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres), TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease wp treshold 100");
					} else if (WPTreshold == 1) {
						tts.speak(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres) + " " + getResources().getString(R.string.cant_decrease), TextToSpeech.QUEUE_FLUSH, null);
					}
				}
				if (v == SaveSettingButton) {
					editor.putInt("WPTreshold", WPTreshold);
					editor.commit();

					intentGaneralSetting.putExtra("WPTreshold", WPTreshold);
					setResult(RESULT_OK, intentGaneralSetting);
					finish();
				}
			}// end of onclick
		}; // end of new View.LocationListener

		IncreaseWPTresholdButton.setOnClickListener(onclickListener);
		DecreaseWPTresholdButton.setOnClickListener(onclickListener);
		SaveSettingButton.setOnClickListener(onclickListener);

		// OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};

		// tts creation
		tts = new TextToSpeech(this, onInitListener);

	} // end of onCreate

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tts.shutdown();
	}

	public void LoadPref() {
		this.WPTreshold = settings.getInt("WPTreshold", 1);
	}

	// action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_wp_treshold, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem Item) {
		switch (Item.getItemId()) {
		case R.id.backtogeneralsetting:

			int LastWPTreshold = settings.getInt("WPTreshold", 5);

			if (LastWPTreshold != WPTreshold) {
				alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getResources().getString(R.string.title_alertdialog_wptreshold));
				alertDialog.setNegativeButton("YES", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor.putInt("WPTreshold", WPTreshold);
						editor.commit();

						intentGaneralSetting.putExtra("WPTreshold", WPTreshold);
						setResult(RESULT_OK, intentGaneralSetting);
						finish();
					}
				});
				alertDialog.setPositiveButton("No", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				alertDialog.setIcon(android.R.drawable.presence_busy);
				alertDialog.show();
			} else {
				finish();
			}
			break;
		default:
			break;
		}
		return false;
	}

}
