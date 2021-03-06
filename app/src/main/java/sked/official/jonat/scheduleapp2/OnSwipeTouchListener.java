/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package sked.official.jonat.scheduleapp2;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/*
 * OnSwipeTouchListener is a simple swipe listener for the application.
 * This allows for toggling between different dates throughout the
 * application.
 */
public class OnSwipeTouchListener implements OnTouchListener {

	private final GestureDetector gestureDetector;

	public OnSwipeTouchListener (Context con){
		gestureDetector = new GestureDetector(con, new GestureListener());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	private final class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				if(Math.abs(diffX) > Math.abs(diffY)) {
					if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if(diffX > 0) {
							onSwipeRight();
						}
						else {
							onSwipeLeft();
						}
						result = true;
					}
				}
				else if(Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
					if(diffY > 0) {
						onSwipeBottom();
					}
					else {
						onSwipeTop();
					}
					result = true;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}

	public void onSwipeRight() {}
	public void onSwipeLeft() {}
	public void onSwipeTop() {}
	public void onSwipeBottom() {}

}
