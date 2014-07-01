package orion.ms.sara;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class NewWayPointActivity extends Activity {
		//variables declaration
		//TextView
		private TextView newNameText =null;
		private TextView newLatitudeText =null;
		private TextView newLongitudeText =null;
	
		//string for each attribute of the new way point
		private String name = "";
		private String latitude = "";
		private String longitude = "";
		
		//EditText
		private EditText nameBox = null;
		private EditText latitudeBox = null;
		private EditText longitudeBox = null;
		
		//button
		private Button saveButton = null;
		private Button saveActButton = null;
		private Button currentLoButton = null;
		private Button mapLoButton = null;
		
		private TextToSpeech tts = null;
		private LocationManager lm = null;
		
		//default name and current position
		private String defaultName = "";
		private String currentLatitude = "";
		private String currentLongitude = "";
	
		//Intent
		private Intent intentToWayPoint;
		private Intent intentFromWayPointAct;
		private Intent intentToWPMap;
		
		//request code
		private final int WP_MAP = 1;

		//status for check if save and activate button is pressed (new way point)
		public static boolean isAlsoActivateForNWP = false;
		
		private TextView textViewWPTreshold = null;

		// declare change distance time treshold buttons
		private Button IncreaseWPTresholdButton = null;
		private Button DecreaseWPTresholdButton = null;
		
		private int WPTreshold = 50;
		private int step = 1;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_way_point);
		//set display in portrait only
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
				//OnInitListener Creation
				OnInitListener onInitListener = new OnInitListener() {
					@Override
					public void onInit(int status) {
					}
				};
				
			    // textToSpeech creation
				if(tts == null) {
					tts = new TextToSpeech(this, onInitListener);
				}
				
				//location manager creation
		        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				
				//TextView
				newNameText = (TextView) findViewById(R.id.textView1);
				newNameText.setContentDescription(getResources().getString(R.string.new_waypoint_name_is));
				newLatitudeText = (TextView) findViewById(R.id.textView2);
				newLatitudeText.setContentDescription(getResources().getString(R.string.new_waypoint_latitude_is));
				newLongitudeText = (TextView) findViewById(R.id.textView3);
				newLongitudeText.setContentDescription(getResources().getString(R.string.new_waypoint_longitude_is));
				
				//EditText and description
				nameBox = (EditText) findViewById(R.id.editText1);
				latitudeBox = (EditText) findViewById(R.id.editText2);
				longitudeBox = (EditText) findViewById(R.id.editText3);
				
				//intent creation
				intentFromWayPointAct = getIntent();
				intentToWayPoint = new Intent(NewWayPointActivity.this,WayPointActivity.class);
				intentToWPMap = new Intent(NewWayPointActivity.this,WaypointMapActivity.class);
				
				//receiving default name
				defaultName = intentFromWayPointAct.getStringExtra("defaultNameFromWP");
				
				//nameBox
				//set default name
				nameBox.setText(defaultName);
				nameBox.setSelectAllOnFocus(true);
				//latitudeBox
				latitudeBox.setSelectAllOnFocus(true);	
				//latitudeBox
				longitudeBox.setSelectAllOnFocus(true);
				
				//button
				//current location button
				currentLoButton = (Button) findViewById(R.id.button2);
				//currentLoButton.setTextSize(30);
				currentLoButton.setOnClickListener(new OnClickListener() {
					//OnClick creation
					@Override
					public void onClick(View v) {
						//update location
				        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
				        currentLatitude = MyLocationListener.currentLatitude;
						currentLongitude = MyLocationListener.currentLongitude;
						if(currentLatitude.equals("")){
							//GPS unavailable
							AlertDialog.Builder dialog = new AlertDialog.Builder(NewWayPointActivity.this);
							dialog.setTitle(" " + getResources().getString(R.string.no_satellite));
							dialog.setNeutralButton("OK", null);
							dialog.show();
						}
						else{
							//GPS available
							if(!latitudeBox.getText().equals(currentLatitude) || !longitudeBox.getText().equals(currentLongitude)){
								//if the location change
								AlertDialog.Builder dialog = new AlertDialog.Builder(NewWayPointActivity.this);
								dialog.setTitle( " " + getResources().getString(R.string.Are_you_sure_changing_to_the_current_position ) );
								dialog.setNegativeButton(" " + getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										//if the user want to change
										//set each EditText with current position
										latitudeBox.setText(currentLatitude);
										longitudeBox.setText(currentLongitude);
									}
								});//end onClick
								dialog.setPositiveButton(" " + getResources().getString(R.string.cancel), null);//don't want to change
								dialog.show();
							}//end if
						}//end else
					}//end onClick
				});//end setOnClick
				
				//map location button
				mapLoButton = (Button) findViewById(R.id.button3);
				//mapLoButton.setTextSize(30);
				mapLoButton.setOnClickListener(new OnClickListener() {
					//OnClick creation
					@Override
					public void onClick(View v) {
						//send the location in the editText and start the new activity
						String latitude = latitudeBox.getText().toString();
						String longitude = longitudeBox.getText().toString();
						if(!latitude.equals("") || !longitude.equals("")){
							intentToWPMap.putExtra("ifMod", true);
							intentToWPMap.putExtra("oldLatitude", latitudeBox.getText().toString());
							intentToWPMap.putExtra("oldLongitude", longitudeBox.getText().toString());
							startActivityForResult(intentToWPMap, WP_MAP);
						}
						else {
							intentToWPMap.putExtra("ifMod", false);
							startActivityForResult(intentToWPMap, WP_MAP);
						}
					}
				});
				
				//"save" button
				saveButton = (Button) findViewById(R.id.button1);
				saveButton.setTextSize(30);
				//setOnClickedListener
				saveButton.setOnClickListener(new OnClickListener() {
					//OnClickedListener creation
					@SuppressWarnings("static-access")
					@Override
					public void onClick(View v) {
						if(v==saveButton){
							//get the new waypoint's name, latitude and longitude from the EditText
							name = nameBox.getText().toString();
							latitude = latitudeBox.getText().toString();
							longitude = longitudeBox.getText().toString();
							
							//check if the filled name or the position (latitude and longitude) are already recorded
							if(isRecorded(name, latitude, longitude)){
								tts.speak( " " + getResources().getString(R.string.Please_fill_the_new_information), tts.QUEUE_ADD, null);
							}
							else{
								if(latitude.isEmpty() || longitude.isEmpty() || name.isEmpty()){
									//prevent unfilled text box(es)
									tts.speak(" " + getResources().getString(R.string.Please_fill_the_name_before_saving) , tts.QUEUE_ADD, null);
								}
								else{
									//sent the new waypoint information back to waypoint activity
									
									//notification
									tts.speak(" " + getResources().getString(R.string.new_waypoint), tts.QUEUE_ADD, null);
									
									//change back to the waypoint activity
									//pass the parameters including name,latitude,longitude
									intentToWayPoint.putExtra("newName",name);//name
									intentToWayPoint.putExtra("newLatitude", latitude);//latitude
									intentToWayPoint.putExtra("newLongitude", longitude);//longitude
									intentToWayPoint.putExtra("treshold", WPTreshold);//treshold

									isAlsoActivateForNWP = false;//change status

									//back to WayPoint activity and send some parameters to the activity
									setResult(RESULT_OK, intentToWayPoint);
									finish();
									
								}//end else in if-else
								
							}//end else	
						}	
					}//end onClick
				});
				
				//save and activate button
				saveActButton = (Button) findViewById(R.id.button4);
				saveActButton.setTextSize(30);
				saveActButton.setOnClickListener(new OnClickListener() {
					//onClick creation
					@SuppressWarnings("static-access")
					@Override
					public void onClick(View v) {
						if(v==saveActButton){
							//get the new waypoint's name, latitude and longitude from the EditText
							name = nameBox.getText().toString();
							latitude = latitudeBox.getText().toString();
							longitude = longitudeBox.getText().toString();
							
							//check if the filled name or the position (latitude and longitude) are already recorded
							if(isRecorded(name, latitude, longitude)){
								tts.speak( " " + getResources().getString(R.string.Please_fill_the_new_information), tts.QUEUE_ADD, null);
							}
							else{
								if(latitude.isEmpty() || longitude.isEmpty() || name.isEmpty()){
									//prevent unfilled text box(es)
									tts.speak(" " + getResources().getString(R.string.Please_fill_the_name_before_saving), tts.QUEUE_ADD, null);
								}
								else{
									//sent the new waypoint information back to waypoint activity
									//intent to way point
									//pass the parameters including name,latitude,longitude
									intentToWayPoint.putExtra("newName",name);//name
									intentToWayPoint.putExtra("newLatitude", latitude);//latitude
									intentToWayPoint.putExtra("newLongitude", longitude);//longitude
									intentToWayPoint.putExtra("treshold", WPTreshold);//treshold

									isAlsoActivateForNWP = true;//change the status
									
									//back to WayPoint activity and send some parameters to the activity
									setResult(RESULT_OK, intentToWayPoint);
									finish();
								}//end else in if-else
							}//end else						
						}//end if
					}//end onClick
				});//end setOnClick
				// increase&decrease distance time treshold button
				IncreaseWPTresholdButton = (Button) findViewById(R.id.IncreaseWPTresholdButton);
				IncreaseWPTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.wptreshold));
				DecreaseWPTresholdButton = (Button) findViewById(R.id.DecreaseWPTresholdButton);
				DecreaseWPTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.wptreshold));
				
				// distance time treshold view
				textViewWPTreshold = (TextView) findViewById(R.id.WPTresholdTextView);
				textViewWPTreshold.setText(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres));
				textViewWPTreshold.setContentDescription(getResources().getString(R.string.wptreshold) + WPTreshold + " " + getResources().getString(R.string.metres));

				
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
								tts.speak(getResources().getString(R.string.wptreshold) + " " + WPTreshold + " " + getResources().getString(R.string.metres)
										+ " " + getResources().getString(R.string.cant_increase), TextToSpeech.QUEUE_FLUSH, null);
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
					}// end of onclick
				}; // end of new View.LocationListener
				IncreaseWPTresholdButton.setOnClickListener(onclickListener);
				DecreaseWPTresholdButton.setOnClickListener(onclickListener);
				
	}//end of OnCreate

	//to check if the filled name or the position (latitude and longitude) are already recorded
	@SuppressWarnings("static-access")
	@SuppressLint("ShowToast")
	
	public boolean isRecorded(String n, String la, String lo){
		List<WP> wList = WayPointActivity.getWayPointList();
		for(int i = 1;i<wList.size();i++){
			if(wList.get(i).getName().equalsIgnoreCase(n)){
				// same name
				tts.speak(" " + getResources().getString(R.string.This_name_is_already_recorded), tts.QUEUE_FLUSH, null);
				return true;
			}//end if
			else if(wList.get(i).getLatitude().equalsIgnoreCase(la) && wList.get(i).getLongitude().equalsIgnoreCase(lo)){
				//same position
				tts.speak("This position is already recorded.", tts.QUEUE_FLUSH, null);
				return true;
			}//end if
		}//end for
		return false;
	}//end isRecored
	
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
	
	//Intent to handle receive parameters from NewWayPoint and Modify
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==WP_MAP && resultCode == RESULT_OK){
        	//receive latitude and longitude from the map
        	String latitudeFromMap = data.getStringExtra("newLatitude");
        	String longitudeFromMap = data.getStringExtra("newLongitude");
        	Log.i(latitudeFromMap, longitudeFromMap);
        	//set the receiving location to the editText
        	latitudeBox.setText(latitudeFromMap);
        	longitudeBox.setText(longitudeFromMap);
        }
        
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_way_point, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.waypoint_setting:
			//get the waypoint's new name, latitude or longitude from the EditText
			name = nameBox.getText().toString();
			latitude = latitudeBox.getText().toString();
			longitude = longitudeBox.getText().toString();
			
			//check if some values change without saving
			if((!latitude.equals("") || !longitude.equals("")) && !isRecorded(name, latitude, longitude)){
				final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle(" " + getResources().getString(R.string.Some_values_change_do_you_want_to_save));
				dialog.setNegativeButton(" " + getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("newName",name);//name
						intentToWayPoint.putExtra("newLatitude", latitude);//latitude
						intentToWayPoint.putExtra("newLongitude", longitude);//longitude
						intentToWayPoint.putExtra("treshold", WPTreshold);//treshold

						isAlsoActivateForNWP = false;//change status

						//back to WayPoint activity and send some parameters to the activity
						setResult(RESULT_OK, intentToWayPoint);
						finish();
					}
				});
				
				dialog.setNeutralButton(" " + getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//don't save use the old name and position
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("newName","");//name
						intentToWayPoint.putExtra("newLatitude", "");//latitude
						intentToWayPoint.putExtra("newLongitude", "");//longitude
						setResult(RESULT_OK, intentToWayPoint);
						finish();
					}
				});
				dialog.show();
			}
			else{
				//pass the parameters including name,latitude,longitude
				intentToWayPoint.putExtra("newName","");//name
				intentToWayPoint.putExtra("newLatitude", "");//latitude
				intentToWayPoint.putExtra("newLongitude", "");//longitude
				setResult(RESULT_OK, intentToWayPoint);
				finish();
				break;
			}
		default:
			break;
		}
		return false;
	}

}

