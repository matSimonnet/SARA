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

public class ModifyWayActivity extends Activity {
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
	private int order = 1;
	
	//iterator id number of the last item in the scroll view
	private int belowID = R.id.Spinner2;
	
	//way point
	private String name;
	private String latitude;
	private String longitude;
	private WP selecting = WayActivity.findWPfromName("No selected waypoint");
	private WP deleteWP = new WP("Delete this waypoint","","");
	//private WP selecting = WayActivity.findWPfromName(getResources().getString(R.string.no_selected_waypoint));
	//private WP deleteWP = new WP(getResources().getString(R.string.no_selected_waypoint),"","");
	private WP del = null;

	//array adapter
	private ArrayAdapter<String> arrAd = null;
	
	private TextToSpeech tts = null;
	
	//Intent
	private Intent intentFromWay;
	private Intent intentToWay;
	
	//status for check if save and activate button is pressed (modify way)
	public static boolean isAlsoActivateForMW = false;
	
	//way attributes
	private String modWayName = "Way1";
	private String oldWayName = "";
	private String oldWP = "";
	private int oldSize = 0;
	private Way temp = null;
	private Way oldWay = null;
	private String wpName = "No selected waypoint";
	private int way_Size = 2;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_way);
		//set display in portrait only
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(ModifyWayActivity.this, onInitListener);

		//TextView
		wayNameText = (TextView) findViewById(R.id.textView1);
		//wayNameText.setContentDescription("modify way's name");
		wp1Text = (TextView) findViewById(R.id.textView2);
		//wp1Text.setContentDescription("waypoint 1 name is"); 
		wp1NameText = (TextView) findViewById(R.id.textView3);
		wp2Text = (TextView) findViewById(R.id.TextView4);
		//wp2Text.setContentDescription("waypoint 2 name is"); 
		wp2NameText = (TextView) findViewById(R.id.TextView5);
		
		//layout
		rl = (RelativeLayout) findViewById(R.id.relativelayout);
		
		//EditText
		wayNameBox = (EditText) findViewById(R.id.editText1);
		wayNameBox.setSelectAllOnFocus(true);
		
		//Spinner
		wp1List = (Spinner) findViewById(R.id.spinner1);
		wp2List = (Spinner) findViewById(R.id.Spinner2);
		
		//Intent creation
		intentFromWay = getIntent();
		intentToWay = new Intent(ModifyWayActivity.this,WayActivity.class);	
		
		//get old way name and way points and set them up
		setUpOldDetails();
	
		//add more button
		addMoreButton = (Button) findViewById(R.id.button1);
		addMoreButton.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View arg0) {
				moreWay(belowID+77,"");
				tts.speak(" " + getResources().getString(R.string.More_waypoint_selection_shown), tts.QUEUE_FLUSH, null);
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
					modWayName = wayNameBox.getText().toString();
					temp.setName(modWayName);
					if(modWayName.isEmpty()){
						//prevent incomplete way's name
						tts.speak(" " + getResources().getString(R.string.More_waypoint_selection_shown), tts.QUEUE_ADD, null);
					}
					else{
						//remove delete way points from way
						for(int i=0; i<temp.getSize();i++){
							if(temp.getWP(i).getName().equals("Delete this waypoint")){
								//delete this way point from way
								del = temp.getWP(i);
								temp.deleteWPfromWay(del);
							}
						}
						if(temp.getSize()<2){
							//prevent incomplete way
							tts.speak(" " + getResources().getString(R.string.Cannot_create_a_way_with_less_than_2_waypoints), tts.QUEUE_FLUSH, null);
						}
						else{
							//check if the filled name or the way points are already recorded
							if(!isRecorded(temp)&&!modifiable(temp)){
								tts.speak(" " + getResources().getString(R.string.Please_fill_the_new_information), tts.QUEUE_ADD, null);
							}
							else{
								//notification
								tts.speak(" " + getResources().getString(R.string.the_new_way_already_saved), tts.QUEUE_ADD, null);
								//change back to the way activity
								//passing activate way name and way points
								intentToWay.putExtra("modWayName", temp.getName());
								intentToWay.putExtra("modWaySize", temp.getSize());
								for(int i = 0; i < temp.getSize(); i++) {
									intentToWay.putExtra("modWP"+(i+1)+"Name", temp.getWP(i).getName());
								}
								isAlsoActivateForMW = false;//change status
								//back to Way activity and send some parameters to the activity
								setResult(RESULT_OK, intentToWay);
								finish();
							}//end isRecord
						}//end size<2	
					}//end isEmpty
				}//end saveButton
			}//end onClick
		});//end setOnClick
		
		//save and activate button
		saveActButton = (Button) findViewById(R.id.button3);
		saveActButton.setOnClickListener(new OnClickListener() {
			//OnClickedListener creation
			@SuppressWarnings("static-access")
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(v==saveActButton){
					//get the new way's name EditText
					modWayName = wayNameBox.getText().toString();
					temp.setName(modWayName);
					if(modWayName.isEmpty()){
						//prevent incomplete way's name
						tts.speak(" " + getResources().getString(R.string.Please_fill_the_name_before_saving), tts.QUEUE_ADD, null);
					}
					else{
						//remove delete way points from way
						for(int i=0; i<temp.getSize();i++){
							if(temp.getWP(i).getName().equals("Delete this waypoint")){
								//delete this way point from way
								del = temp.getWP(i);
								temp.deleteWPfromWay(del);
							}
						}
						if(temp.getSize()<2){
							//prevent incomplete way
							tts.speak("" + R.string.Cannot_create_a_way_with_less_than_2_waypoints, tts.QUEUE_FLUSH, null);
						}
						else{
							//check if the filled name or the way points are already recorded
							if(!isRecorded(temp)&&!modifiable(temp)){
								tts.speak(" " + getResources().getString(R.string.Please_fill_the_new_information), tts.QUEUE_ADD, null);
							}
							else{
								//notification
								tts.speak(" " + getResources().getString(R.string.the_new_way_already_saved), tts.QUEUE_ADD, null);
								//change back to the way activity
								//passing activate way name and way points
								intentToWay.putExtra("modWayName", temp.getName());
								intentToWay.putExtra("modWaySize", temp.getSize());
								for(int i = 0; i < temp.getSize(); i++) {
									intentToWay.putExtra("modWP"+(i+1)+"Name", temp.getWP(i).getName());
								}
								isAlsoActivateForMW = true;//change status
								//back to Way activity and send some parameters to the activity
								setResult(RESULT_OK, intentToWay);
								finish();
							}//end isRecord
						}//end size<2	
					}//end isEmpty
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
		//default item
		tempList.add(0,new WP("No selected waypoint","",""));
		//delete item
		tempList.add(1,new WP("Delete this waypoint","",""));
		return tempList;
	}
			
	//setting up way point list to spinner and textView when selecting way point 
	private void setUpArrayAdapter(final List<WP> wList, final Spinner spin,final TextView nameText,final String oldName) {
		//get spinner's id
		final int spinID = spin.getId();
		arrAd = new ArrayAdapter<String>(ModifyWayActivity.this,
						android.R.layout.simple_spinner_item, 
						WayPointActivity.toNameArrayList(wList));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spin.setAdapter(arrAd);
		spin.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
			//OnItemSelectedListener creation
			public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
	  				try{
	            		if(adapterView.getId()==spinID){
	              			wpName = wList.get(i).getName();
	              			if(!oldName.equals("")){
	              				//set old way point's name
	              				nameText.setText(oldName);
	              				//add selecting way point in to way
	            				selecting = WayActivity.findWPfromName(wpName);
	              			}
	            			if(!wpName.equals("No selected waypoint")){
	            				if(!wpName.equals("Delete this waypoint")){
	            					//add selecting way point in to way
		            				selecting = WayActivity.findWPfromName(wpName);
	            				}
	            				else{
	            					selecting = deleteWP;
	            				}
	            				nameText.setText(wpName);              					
	            				//find the way point number from the spinner's id
	            				setWPtoWay(spinID, selecting);
	            				spin.setFocusable(true);
	            				spin.setFocusableInTouchMode(true);	            				
	            			}//neither default nor delete item
	            		}
	  				}catch(Exception e){
	                        e.printStackTrace();
	                }//end try-catch
	        }//end onItemSelected
	        
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(ModifyWayActivity.this, " " + getResources().getString(R.string.you_selected) + " " + getResources().getString(R.string.nothing),Toast.LENGTH_SHORT).show();
			} 
	
	    });//end setOnSelected
	}//end setUpWPList
	
	//set way point to way
	@SuppressWarnings("static-access")
	private void setWPtoWay(int spinnerID,WP wp){
		if(spinnerID==wp1List.getId()){
			//way point1
			//check if selecting "Delete this way point"
			if(!wp.getName().equals("Delete this waypoint")){
				//check if a selecting way point is the same as the previous way point and the next way point 
				if(sameChoosing(wp, temp.getWP(1))){
					//way point1 and way point2 are the same
					tts.speak(" " + getResources().getString(R.string.Cannot_selecting_the_same_way_point_as_next_way_point), tts.QUEUE_FLUSH, null);
				}
				else{
					//replace old way point
					temp.setWP(0, wp);
					order = 1;
				}
			}//end not delete
			else{
				temp.setWP(0, deleteWP);
				order = 1;
			}//end delete
		}//end spinner1
		else if(spinnerID==wp2List.getId()){
			//way point2
			//check if selecting "Delete this way point"
			if(!wp.getName().equals("Delete this waypoint")){
				//check if there are only two way points
				if(temp.getSize()==2){
					if(sameChoosing(wp, temp.getWP(0))){
						//way point1 and way point2 are the same
						tts.speak(" " + getResources().getString(R.string.Cannot_selecting_the_same_way_point_as_next_way_point), tts.QUEUE_FLUSH, null);
					}
					else{
						//replace old way point
						temp.setWP(1, wp);
					}//end not the same choosing
				}//end size==2
				else{
					//size > 2
					if(sameChoosing(wp, temp.getWP(0)) || sameChoosing(wp, temp.getWP(2)) ){
						//way point1 and way point2 are the same or way point 2 and way point 3 are the same
						tts.speak(" " + getResources().getString(R.string.Cannot_selecting_the_same_way_point_as_next_way_point), tts.QUEUE_FLUSH, null);
					}
					else{
						//replace old way point
						temp.setWP(1, wp);
					}//end not the same choosing
				}//end size > 2
				order = 2;
			}//end not delete
			else{
				temp.setWP(1, deleteWP);
				order = 2;
			}
		}//end spinner2
		else{
			//check if selecting "Delete this way point"
			if(!wp.getName().equals("Delete this waypoint")){
				//check if it is the last way point in the way now
				if(spinnerID==temp.getSize()){
					if(sameChoosing(wp, temp.getWP(spinnerID-2))){
						//selecting way point and the previous way point are the same
						tts.speak(" " + getResources().getString(R.string.Cannot_selecting_the_same_way_point_as_next_way_point), tts.QUEUE_FLUSH, null);
					}
					else{
						if(temp.getWP(spinnerID-1)!=null){
							//replace old way point
							temp.setWP(spinnerID-1, wp);
						}
						else{
							//add new way point
							temp.addWPtoWay(wp);
						}
					}//end not the same choosing
				}//end it is the last way point in the way now
				else if(spinnerID>temp.getSize()){
					if(sameChoosing(wp, temp.getWP(spinnerID-2))){
						//selecting way point and the previous way point are the same
						tts.speak(" " + getResources().getString(R.string.Cannot_selecting_the_same_way_point_as_next_way_point), tts.QUEUE_FLUSH, null);
					}
					else{
						//add new way point
						temp.addWPtoWay(wp);
					}
				}
				else{
					if( sameChoosing(wp, temp.getWP(spinnerID-2)) || sameChoosing(wp, temp.getWP(spinnerID))){
						//selecting way point and the previous way point are the same or selecting way point and the next way point are the same
						tts.speak(" " + getResources().getString(R.string.Cannot_selecting_the_same_way_point_as_next_way_point), tts.QUEUE_FLUSH, null);
					}
					else{
						if(temp.getWP(spinnerID-1)!=null){
							//replace old way point
							temp.setWP(spinnerID-1, wp);
						}
						else{
							//add new way point
							temp.addWPtoWay(wp);
						}
					}//end not the same choosing
				}//end not the last
				order = spinnerID;
			}//end not delete
			else{
				if(spinnerID>temp.getSize()){
					//add new way point
					temp.addWPtoWay(deleteWP);
				}
				else{
					//replace old way point
					temp.setWP(spinnerID-1, deleteWP);
				}
				order = spinnerID;
			}//end delete
		}//end else
		//tts announcement
		tts.speak(" " + getResources().getString(R.string.waypoint) + order + " " + wp.getName(), tts.QUEUE_ADD, null);
	}


	/*
	 * setting up all old details about modifying way
	 */
	private void setUpOldDetails(){
		//name
		oldWayName = intentFromWay.getStringExtra("modName");
		wayNameBox.setText(oldWayName);
		//old way creation
		temp = new Way(oldWayName);
		oldWay = new Way(oldWayName);
		//way points
		oldSize = intentFromWay.getIntExtra("modSize", 0);
		for(int i = 0; i < oldSize;i++) {
			oldWP = intentFromWay.getStringExtra("WP"+(i+1)+"Name");
			if(i==0){
				//way point1 set up
				setUpArrayAdapter(setUpWPList(tempList), wp1List, wp1NameText,oldWP);
			}
			else if(i==1){
				//way point2 set up
				setUpArrayAdapter(setUpWPList(anotherList), wp2List, wp2NameText,oldWP);
			}
			else{
				//way point3 and more set up
				moreWay(belowID+77,oldWP);
			}
			//add the way
			temp.addWPtoWay(WayActivity.findWPfromName(oldWP));
			oldWay.addWPtoWay(WayActivity.findWPfromName(oldWP));
		}
		
	}
			
	/*
	 * Create more TextView and spinner for adding a way more in the view 
	 * then adding new way point into temporary way
	 */
	private void moreWay(int id,String oldName) {
		//set up
	    rl = (RelativeLayout) findViewById(R.id.rela);
	    way_Size += 1;
	    //textView iterating way point number
		LayoutParams wpParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		wpParam.addRule(RelativeLayout.BELOW,belowID);
	    TextView way_WP = new TextView(this);
	    way_WP.setLayoutParams(wpParam);
	    way_WP.setText(getResources().getString(R.string.waypoint) + way_Size + " ");
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
		setUpArrayAdapter(setUpWPList(wList), newWPList, wp_name,oldName);
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
		
	//to check if the filled name or way points are already recorded
	private boolean isRecorded(Way way){
		if(WayActivity.usedName(way.getName())){
			return true;
		}
		if(WayActivity.usedWay(way)){
			return true;
		}
		return false;
	}//end isRecored
	
	//check if choosing same way points
	public boolean sameChoosing(WP wp1,WP wp2){
		if(wp1.getName().equals(wp2.getName())) return true;
		return false;
	}
	
	//check if this way allow to have same name with different way points
	//or same way points with different name
	private boolean modifiable(Way way){
		if(WayActivity.usedName(way.getName())&&!WayActivity.usedWay(way)) return true;
		else if(!WayActivity.usedName(way.getName())&&WayActivity.usedWay(way)) return true;
		return false;
	}//end isAllow
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify_way, menu);
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
			modWayName = wayNameBox.getText().toString();
			if(!modWayName.isEmpty()&&(!WayActivity.sameWay(temp, oldWay) || !oldWay.getName().equals(modWayName))){
				temp.setName(modWayName);
				//remove delete way points from way
				for(int i=0; i<temp.getSize();i++){
					if(temp.getWP(i).getName().equals("Delete this waypoint")){
						//delete this way point from way
						del = temp.getWP(i);
						temp.deleteWPfromWay(del);
					}
				}
				//check if can create a way
				if(temp.getSize()>=2){
					final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setTitle(" " + getResources().getString(R.string.Some_values_change_do_you_want_to_save));
					dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//passing activate way name and way points
							intentToWay.putExtra("modWayName", temp.getName());
							intentToWay.putExtra("modWaySize", temp.getSize());
							for(int j = 0; j < temp.getSize(); j++) {
								intentToWay.putExtra("modWP"+(j+1)+"Name", temp.getWP(j).getName());
							}
							isAlsoActivateForMW = false;//change status
	
							//back to Way activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
						}
					});
					
					dialog.setNeutralButton("No", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//don't save
							//passing activate way name and way points
							intentToWay.putExtra("modWayName", "");
							intentToWay.putExtra("modWaySize", oldWay.getSize());
							for(int j = 0; j < temp.getSize(); j++) {
								intentToWay.putExtra("modWP"+(j+1)+"Name", "");
							}
							isAlsoActivateForMW = false;//change status
							//back to Way activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
						}
					});
					dialog.show();
				}//end size>=2
				else{
					//passing activate way name and way points
					intentToWay.putExtra("modWayName", "");
					intentToWay.putExtra("modWaySize", oldWay.getSize());
					for(int j = 0; j < temp.getSize(); j++) {
						intentToWay.putExtra("modWP"+(j+1)+"Name", "");
					}
					isAlsoActivateForMW = false;//change status
		
					//back to Way activity and send some parameters to the activity
					setResult(RESULT_OK, intentToWay);
					finish();
					break;
				}
			}
			else{
				//don't save
				//passing activate way name and way points
				intentToWay.putExtra("modWayName", "");
				intentToWay.putExtra("modWaySize", oldWay.getSize());
				for(int j = 0; j < temp.getSize(); j++) {
					intentToWay.putExtra("modWP"+(j+1)+"Name", "");
				}
				isAlsoActivateForMW = false;//change status
	
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