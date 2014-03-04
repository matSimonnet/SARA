SARA
====

general issue : be compatible with "talk back", the screen reader from android, to ensure blind people accessibility. In general, add setContentDescription("") to each component is enough. 


1° in the main activity
	- add distance to the current waypoint in a big textview
	- add bearing to the current waypoint in a big textview 
	- remove seek bars and replace them with number pickers in the following "auto settings" activity and view.
	- create an action bar menu with items of the following activity. 
	
2° create a new activity and view, called "auto settings", with checked box and number pickers to set the minimum treshold before announcing a changed value : 
	- the speed treshold, 
	- the heading treshold,
	- the bearing treshold to the current waypoint, 
	- the minimum time before repeating speed value,  
	- the minimum time before repeating heading value,
	- the minimum time before repeating distance to the waypoint,  
	- the minimum time before repeating bearing to the waypoint,
	* remarque : the distance treshold to the waypoint is predefined -> announce every : (distance to the waypoint) / 10 . 
	* remarque : TextViews should be editable to allow to enter value with keyboard if users do not like number pickers.

3° create another activity and view, called "waypoint", to create waypoints :
	- waypoint list (sort by proximity) -> add a touchlistener to open a dialog box asking to activate, modify or delete this waypoint
	- button "new waypoint here" which open the following "new waypoint" activity and view.
	
4° create the activity and view "new waypoint" with : 
	- a text edit "name"
	- a text edit "latitude" filled with current latitude when clicking
	- a text edit "longitude" filled with current latitude when clicking
	- a save button

5° create the dialog box "asking to activate, modify or delete a waypoint" from the "waypoint" activity when touching one waypoint item of the list.
	if activate, return to main activity and calculate bearing and distance
	if modify, go to the new waypoint activity with the fiel already filled and editable
	if delete, ask is it is sure and delete

