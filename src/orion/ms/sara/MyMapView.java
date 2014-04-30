package orion.ms.sara;

import java.util.Timer;
import java.util.TimerTask;

import org.mapsforge.android.maps.MapView;
import org.mapsforge.core.GeoPoint;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyMapView extends MapView {

	// Define the interface we will interact with from our Map
	public interface OnLongpressListener {
		public void onLongpress(MapView view, GeoPoint longpressLocation);
	}

	/**
	 * Time in ms before the OnLongpressListener is triggered.
	 */
	static final int LONGPRESS_THRESHOLD = 500;

	/**
	 * Keep a record of the center of the map, to know if the map has been
	 * panned.
	 */
	private GeoPoint lastMapCenter;

	private Timer longpressTimer = new Timer();
	private MyMapView.OnLongpressListener longpressListener;

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnLongpressListener(MyMapView.OnLongpressListener listener) {
		longpressListener = listener;
	}

	/**
	 * This method is called every time user touches the map, drags a finger on
	 * the map, or removes finger from the map.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		handleLongpress(event);

		return super.onTouchEvent(event);
	}

	/**
	 * This method takes MotionEvents and decides whether or not a longpress has
	 * been detected. This is the meat of the OnLongpressListener.
	 * 
	 * The Timer class executes a TimerTask after a given time, and we start the
	 * timer when a finger touches the screen.
	 * 
	 * We then listen for map movements or the finger being removed from the
	 * screen. If any of these events occur before the TimerTask is executed, it
	 * gets cancelled. Else the listener is fired.
	 * 
	 * @param event
	 */
	private void handleLongpress(final MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Finger has touched screen.
			longpressTimer = new Timer();
			longpressTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					GeoPoint longpressLocation = getProjection().fromPixels(
							(int) event.getX(), (int) event.getY());

					/*
					 * Fire the listener. We pass the map location of the
					 * longpress as well, in case it is needed by the caller.
					 */
					longpressListener.onLongpress(MyMapView.this,
							longpressLocation);
				}

			}, LONGPRESS_THRESHOLD);

			lastMapCenter = getMapPosition().getMapCenter();
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {

			if (!getMapPosition().getMapCenter().equals(lastMapCenter)) {
				// User is panning the map, this is no longpress
				longpressTimer.cancel();
			}

			lastMapCenter = getMapPosition().getMapCenter();
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			// User has removed finger from map.
			longpressTimer.cancel();
		}

		if (event.getPointerCount() > 1) {
			// This is a multitouch event, probably zooming.
			longpressTimer.cancel();
		}
	}
}
