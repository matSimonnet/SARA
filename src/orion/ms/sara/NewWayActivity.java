package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewWayActivity extends Activity {
	//components
	//TextView
	private TextView wayNameText;
	private TextView wp1Text;
	private TextView wp1NameText;
	private TextView wp2Text;
	private TextView wp2NameText;
	
	//layout
	private RelativeLayout rl;
	
	//EditText
	private EditText wayNameBox;
	
	//button
	private Button saveButton = null;
	private Button saveActButton = null;
	private Button addMoreButton = null;
	
	//spinner
	private Spinner wp1List;
	private Spinner wp2List;
	private ArrayList<WP> tempList = new ArrayList<WP>();
	private ArrayList<WP> anotherList = new ArrayList<WP>();
	private int spin_id = 3;
	
	//iterator id number of the last item in the scroll view
	private int belowID = R.id.Spinner2;
	
	//way point
	private String name;
	private String latitude;
	private String longitude;
	private WP selecting = WayActivity.findWPfromName("No selected waypoint");
	private WP lastSelect = WayActivity.findWPfromName("No selected waypoint");
	
	//array adapter
	private ArrayAdapter<String> arrAd = null;
	
	private TextToSpeech tts = null;
	
	//Intent
	private Intent intentFromWay;
	private Intent intentToWay;
	
	//status for check if save and activate button is pressed (new way)
	public static boolean isAlsoActivateForNW = false;
	
	//way attributes
	private String wayName = "Way1";
	private Way temp = null;
	private String defaultName = "No selected waypoint";
	private String wpName = "No selected waypoint";
	private int way_Size = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_way);
		//set display in portrait only
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(NewWayActivity.this, onInitListener);

		//TextView
		wayNameText = (TextView) findViewById(R.id.textView1);
		wayNameText.setContentDescription("new way's name");
		wp1Text = (TextView) findViewById(R.id.textView2);
		wp1Text.setContentDescription("waypoint 1 name is"); 
		wp1NameText = (TextView) findViewById(R.id.textView3);
		wp2Text = (TextView) findViewById(R.id.TextView4);
		wp2Text.setContentDescription("waypoint 2 name is"); 
		wp2NameText = (TextView) findViewById(R.id.TextView5);
		
		//layout
		rl = (RelativeLayout) findViewById(R.id.relativelayout);
		
		//EditText
		wayNameBox = (EditText) findViewById(R.id.editText1);
		wayNameBox.setSelectAllOnFocus(true);
		
		//Intent creation
		intentFromWay = getIntent();
		intentToWay = new Intent(NewWayActivity.this,WayActivity.class);
		
		//set default name
		wayName = intentFromWay.getStringExtra("defaultNameFromWay");
		wayNameBox.setText(wayName);
		
		//Spinner
		wp1List = (Spinner) findViewById(R.id.spinner1);
		wp2List = (Spinner) findViewById(R.id.Spinner2);
		
		//temporary way creation
		temp = new Way(wayNameBox.getText().toString());
		Log.i("temp creation", "name "+temp.getName());
		Log.i("temp size", temp.getName()+" :"+temp.getSize());
		
		//get way list
		setUpArrayAdapter(setUpWPList(tempList), wp1List, wp1NameText);
		setUpArrayAdapter(setUpWPList(anotherList), wp2List, wp2NameText);		
	
		//add more button
		addMoreButton = (Button) findViewById(R.id.button1);
		addMoreButton.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View arg0) {
				moreWay(belowID+77);
				tts.speak("More waypoint selection shown", tts.QUEUE_FLUSH, null);
			}
		});
		
		//"save" button
		saveButton = (Button) findViewById(R.id.button2);
		//setOnClickedListener
		saveButton.setOnClickListener(new OnClickListener() {
			//OnClickedListener creation
			@SuppressWarnings("static-access")
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(v==saveButton){
					//get the new way's name EditText
					wayName = wayNameBox.getText().toString();
					temp.setName(wayName);
					Log.i("temp size", wayName+" :"+temp.getSize());
    				for(int j = 0;j<temp.getSize();j++){
    					Log.i("wp in new", "Way1 ----WP"+j+" : "+temp.getWP(j).getName());
    				}
					if(temp.getSize()<2){
						tts.speak("Cannot create a way with less than 2 waypoints", tts.QUEUE_FLUSH, null);
					}
					else{
						Log.i("temp size", wayName+" :"+temp.getSize());
	    				for(int j = 0;j<temp.getSize();j++){
	    					Log.i("wp in new", "Way1 ----WP"+j+" : "+temp.getWP(j).getName());
	    				}
						
						//check if the filled name or the way points are already recorded
						if(isRecorded(temp)){
							tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
						}
						else{
							if(wayName.isEmpty()){
								//prevent incomplete way's name
								tts.speak("Please fill the name before saving", tts.QUEUE_ADD, null);
							}
							else{
								//notification
								tts.speak("the new way already saved", tts.QUEUE_ADD, null);
								Toast.makeText(NewWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);
		
								//change back to the way activity
								//passing activate way name and way points
								intentToWay.putExtra("newWayName", temp.getName());
								intentToWay.putExtra("newWaySize", temp.getSize());
								for(int i = 0; i < temp.getSize(); i++) {
									intentToWay.putExtra("WP"+(i+1)+"Name", temp.getWP(i).getName());
								}
								isAlsoActivateForNW = false;//change status
		
								//back to WayPoint activity and send some parameters to the activity
								setResult(RESULT_OK, intentToWay);
								finish();
							}//end isEmpty
						}//end isRecord	
					}//end size<2
				}//end saveButton
			}//end onClick
		});//end setOnClick
		
		//save and activate button
		saveActButton = (Button) findViewById(R.id.button3);
		//setOnClickedListener
		saveActButton.setOnClickListener(new OnClickListener() {
			//OnClickedListener creation
			@SuppressWarnings("static-access")
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(v==saveActButton){
					//get the new way's name EditText
					wayName = wayNameBox.getText().toString();
					temp.setName(wayName);
					if(temp.getSize()<2){
						tts.speak("Cannot create a way with less than 2 waypoints", tts.QUEUE_FLUSH, null);
					}
					else{
						//check if the filled name or the way points are already recorded
						if(isRecorded(temp)){
							tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
						}
						else{
							if(wayName.isEmpty()){
								//prevent incomplete way's name
								tts.speak("Please fill the name before saving", tts.QUEUE_ADD, null);
							}
							else{
								//notification
								tts.speak("the new way already saved", tts.QUEUE_ADD, null);
								Toast.makeText(NewWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);
		
								//change back to the way activity
								//passing activate way name and way points
								intentToWay.putExtra("newWayName", temp.getName());
								intentToWay.putExtra("newWaySize", temp.getSize());
								for(int i = 0; i < temp.getSize(); i++) {
									intentToWay.putExtra("WP"+(i+1)+"Name", temp.getWP(i).getName());
								}
								isAlsoActivateForNW = true;//change status
		
								//back to WayPoint activity and send some parameters to the activity
								setResult(RESULT_OK, intentToWay);
								finish();
							}//end isEmpty
						}//end isRecord	
					}//end size<2
				}//end saveButton
			}//end onClick
		});//end setOnClick	
		
	}


	//change default item name
	private ArrayList<WP> setUpWPList(ArrayList<WP> tempList) {
		for(int i=0;i<WayPointActivity.getWayPointList().size();i++){
			String tmpname = WayPointActivity.getWayPointList().get(i).getName();
			if(!tmpname.equals("Please selected a waypoint")){
				name = WayPointActivity.getWayPointList().get(i).getName();
				latitude = WayPointActivity.getWayPointList().get(i).getLatitude();
				longitude = WayPointActivity.getWayPointList().get(i).getLongitude();
				tempList.add(new WP(name,latitude,longitude));
			}
		}
		tempList.add(0,new WP(defaultName,"",""));		
		return tempList;
	}
	
	//setting up way point list to spinner and textView when selecting way point 
	private void setUpArrayAdapter(final List<WP> wList, final Spinner spin,final TextView nameText) {
		//get spinner's id
		final int spinID = spin.getId();
		arrAd = new ArrayAdapter<String>(NewWayActivity.this,
						android.R.layout.simple_spinner_item, 
						WayPointActivity.toNameArrayList(wList));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spin.setAdapter(arrAd);
		spin.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
			//OnItemSelectedListener creation
			@SuppressWarnings("static-access")
			public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
      				try{
                		if(adapterView.getId()==spinID){
                  			wpName = wList.get(i).getName();
                			if(!wpName.equals("No selected waypoint")){
                				nameText.setText(wpName);
                				//add selecting way point in to way
                				selecting = WayActivity.findWPfromName(wpName);
                				//start checking after choosing the first way point
                				if(temp.getSize()>=1){
	                				lastSelect = temp.getWP(temp.getSize()-1);
	                				Log.i("select", temp.getName()+" item: "+selecting.getName());
	                				//check if the previous way point in the same as selecting way point
	                				if(sameChoosing(selecting, lastSelect)){
	                					tts.speak("Cannot selecting the same way point as previous way point", tts.QUEUE_FLUSH, null);
	                				}
                				}//end size>=1
                				//find the way point number from the spinner's id
	            				setWPtoWay(spinID, selecting);
                				spin.setFocusable(true);
                				spin.setFocusableInTouchMode(true);
                			}
                		}
      				}catch(Exception e){
	                        e.printStackTrace();
	                }//end try-catch
            }//end onItemSelected
            
			public void onNothingSelected(AdapterView<?> arg0) {
  				Toast.makeText(NewWayActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
			} 

	    });//end setOnSelected
	}//end setUpWPList
	
	/*
	 * Create more TextView and spinner for adding a way more in the view 
	 * then adding new way point into temporary way
	 */
	private void moreWay(int id) {
		//set up
        rl = (RelativeLayout) findViewById(R.id.rela);
        way_Size += 1;
        //textView iterating way point number
  		LayoutParams wpParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		wpParam.addRule(RelativeLayout.BELOW,belowID);
	    TextView way_WP = new TextView(this);
	    way_WP.setLayoutParams(wpParam);
	    way_WP.setText("Way point"+way_Size+" is  ");
	    way_WP.setTextSize(TypedValue.COMPLEX_UNIT_PX,wp1Text.getTextSize());
	    way_WP.setGravity(Gravity.CENTER_HORIZONTAL);
	    way_WP.setId(id);
	    rl.addView(way_WP);
	    
	    //textView to show selected way point's name
	    LayoutParams nameParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    nameParam.addRule(RelativeLayout.BELOW,way_WP.getId());
	    TextView wp_name = new TextView(this);
	    wp_name.setLayoutParams(nameParam);
	    wp_name.setText("");
	    wp_name.setTextSize(TypedValue.COMPLEX_UNIT_PX,wp1Text.getTextSize());
	    wp_name.setGravity(Gravity.CENTER_HORIZONTAL);
	    wp_name.setId(way_WP.getId()+88);
	    rl.addView(wp_name);
	    
	    //spinner showing way points list
	    LayoutParams listParam = new LayoutParams(LayoutParams.MATCH_PARENT, 200);
	    listParam.addRule(RelativeLayout.BELOW,wp_name.getId());
	    Spinner newWPList = new Spinner(this);
	    newWPList.setLayoutParams(listParam);
	    newWPList.setId(spin_id);
	    spin_id += 1;
	    ArrayList<WP> wList = new ArrayList<WP>();
	    //set up way list
		setUpArrayAdapter(setUpWPList(wList), newWPList, wp_name);
	    rl.addView(newWPList);
	    
	    //blank layout
	    LayoutParams blankParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    blankParam.addRule(RelativeLayout.BELOW,newWPList.getId());
	    RelativeLayout blank = new RelativeLayout(this);
	    blank.setScaleY(100);
	    blank.setLayoutParams(blankParam);
	    blank.setId(newWPList.getId()+88);
	    rl.addView(blank);
	    
	    //set up new id
	    belowID = blank.getId();
	}
	
	//set way point to way
	@SuppressWarnings("static-access")
	private void setWPtoWay(int spinnerID,WP wp){
		int order = 3;
		if(spinnerID==wp1List.getId()){
			//way point1
			if(temp.getWP(0)!=null){
				//replace old way point
				temp.setWP(0, wp);
			}
			else{
				//add new way point
				temp.addWPtoWay(selecting);
			}
			order = 1;
		}
		else if(spinnerID==wp2List.getId()){
			//way point2
			if(temp.getWP(1)!=null){
				//replace old way point
				temp.setWP(1, wp);
			}
			else{
				//add new way point
				temp.addWPtoWay(selecting);
			}
			order = 2;
		}
		else{
			//old way points
			for(int k=0;k<temp.getSize();k++){
				if(spinnerID==(k+1)){
					if(temp.getWP(k)!=null){
						//replace old way point
						temp.setWP(k, wp);
					}
					else{
						//add new way point
						temp.addWPtoWay(selecting);
					}
					order = k+1;
					
				}
			}
			if(order>temp.getSize()){
				temp.addWPtoWay(selecting);
			}
		}
		//tts announcement
		tts.speak("Waypoint"+order+" is "+selecting.getName(), tts.QUEUE_FLUSH, null);
	}
		
	//to check if the filled name or the position (latitude and longitude) are already recorded
	@SuppressWarnings("static-access")
	public boolean isRecorded(Way way){
		if(WayActivity.usedName(way.getName())){
			tts.speak("This name is already used", tts.QUEUE_FLUSH, null);
			return true;
		}
		if(WayActivity.usedWay(way)){
			tts.speak("This way is already used", tts.QUEUE_ADD, null);
			return true;
		}
		return false;
	}//end isRecored
	
	//check if choosing same way points
	public boolean sameChoosing(WP wp1,WP wp2){
		if(wp1.getName().equals(wp2.getName())) return true;
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		tts.shutdown();
	}
  
	@Override
	protected void onStop() {
		super.onStop();
		tts.shutdown();
	}
  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tts.shutdown();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_way, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.way_setting:
			//get the new way's name EditText
			wayName = wayNameBox.getText().toString();
			//check if some values change without saving
			//check if new way has name and its size is more than 1 way point
			if(temp.getSize()>=2 && !wayName.isEmpty()){
				//set up way name
				temp.setName(wayName);
				//chenk if the new way can save
				if(!isRecorded(temp)){
					final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setTitle("Some values change, do you want to save?");
					dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//pass the parameters
							intentToWay.putExtra("newWayName", temp.getName());
							intentToWay.putExtra("newWaySize", temp.getSize());
							for(int i = 0; i < temp.getSize(); i++) {
								intentToWay.putExtra("WP"+(i+1)+"Name", temp.getWP(i).getName());
							}
							isAlsoActivateForNW = false;//change status
	
							//back to Way activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
						}
					});
					
					dialog.setNeutralButton("No", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//don't save
							//pass the parameters
							intentToWay.putExtra("newWayName", "");
							intentToWay.putExtra("newWaySize", "");
							for(int i = 0; i < temp.getSize(); i++) {
								intentToWay.putExtra("WP"+(i+1)+"Name", "");
							}
							isAlsoActivateForNW = false;//change status
	
							//back to Way activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
						}
					});
					dialog.show();
				}//end isRecord
				else{
					//don't save
					//pass the parameters
					intentToWay.putExtra("newWayName", "");
					intentToWay.putExtra("newWaySize", "");
					for(int i = 0; i < temp.getSize(); i++) {
						intentToWay.putExtra("WP"+(i+1)+"Name", "");
					}
					isAlsoActivateForNW = false;//change status	
					//back to Way activity and send some parameters to the activity
					setResult(RESULT_OK, intentToWay);
					finish();
					break;
				}//end isRecord
		}//end if value change
		else{
			//don't save
			//pass the parameters
			intentToWay.putExtra("newWayName", "");
			intentToWay.putExtra("newWaySize", "");
			for(int i = 0; i < temp.getSize(); i++) {
				intentToWay.putExtra("WP"+(i+1)+"Name", "");
			}
			isAlsoActivateForNW = false;//change status
	
			//back to Way activity and send some parameters to the activity
			setResult(RESULT_OK, intentToWay);
			finish();
			break;
		}
		default:
			break;
		}
		return false;
	}

}