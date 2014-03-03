SARA
====

general issue : be compatible with "talk back", the screen reader from android. 


1° in the main activity
	- change seek bar to number picker to improve blind people accessibility
	- add distance to the current waypoint in a textview
	- add bearing to the current waypoint in a textview  

2° create another activity and view, called auto settings, with checked box and number picker to set : 
	- the speed treshold, 
	- the heading treshold,
	- the bearing treshold to the current waypoint, 
	- the minimum time before repeating speed value,  
	- the minimum time before repeating heading value,
	- the minimum time before repeating distance to the waypoint,  
	- the minimum time before repeating bearing to the waypoint,


3° create another activity and view, called waypoint, to create waypoints :
	- waypoint list (sort by proximity) -> add a touchlistener to open a dialog box asking to activate, modify or delete this waypoint
	- button "new waypoint here" which open the following "new waypoint" activity and view.
	
4° create the activity and view "new waypoint" with : 
	- a text edit "name"
	- a text edit "latitude" filled with current latitude when clicking
	- a text edit "longitude" filled with current latitude when clicking
	- a save button

5° create the dialog box asking to activate, modify or delete a waypoint
	if activate, return to main activity and calculate bearing and distance
	if modify, go to the new waypoint activity with the fiel already filled and editable
	if delete, ask is it is sure and delete

