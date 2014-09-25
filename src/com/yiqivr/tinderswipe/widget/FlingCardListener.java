package com.yiqivr.tinderswipe.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class FlingCardListener implements View.OnTouchListener {

	private final float originalX;
	private final float originalY;
	private final int originalHeight;
	private final int originalWidth;
	private final int halfWidth, halfHeight;
	private final int parentWidth, parentHeight;
	private final HelperFlingWithProportionListener helperFlingListener;
	private final Object dataObject;
	private float BASE_ROTATION_DEGREES = 15.f;

	private float aPosX;
	private float aPosY;
	private float aDownTouchX;
	private float aDownTouchY;
	private static final int INVALID_POINTER_ID = -1;

	private int mActivePointerId = INVALID_POINTER_ID;
	private View frame = null;
	private View nextFrame = null;

	private final int TOUCH_ABOVE = 0;
	private final int TOUCH_BELOW = 1;
	private final int TOUCH_LEFT = 2;
	private final int TOUCH_RIGHT = 3;
	private int touchPosition;

	public enum SWIPEMODE {
		LEFT_RIGHT, UP_DOWN
	}

	private SWIPEMODE swipeMode;

	public FlingCardListener(SWIPEMODE swipeMode, View frame, View nextFrame, int parentWidth, int parentHeight,
			float originalX, float originalY, int originalHeight, int originalWidth, Object itemAtPosition,
			HelperFlingWithProportionListener helperFlingListener) {
		super();
		this.swipeMode = swipeMode;
		this.frame = frame;
		this.nextFrame = nextFrame;
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.originalX = originalX;
		this.originalY = originalY;
		this.originalHeight = originalHeight;
		this.originalWidth = originalWidth;
		this.halfWidth = this.originalWidth / 2;
		this.halfHeight = this.originalHeight / 2;
		this.dataObject = itemAtPosition;
		this.helperFlingListener = helperFlingListener;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			mActivePointerId = event.getPointerId(0);
			final float x = event.getX(mActivePointerId);
			final float y = event.getY(mActivePointerId);

			aDownTouchX = x;
			aDownTouchY = y;
			if (aPosX == 0) {
				aPosX = frame.getX();
			}
			if (aPosY == 0) {
				aPosY = frame.getY();
			}

			if (swipeMode == SWIPEMODE.LEFT_RIGHT) {
				if (y < originalHeight / 2) {
					touchPosition = TOUCH_ABOVE;
				} else {
					touchPosition = TOUCH_BELOW;
				}
			} else {
				if (x < originalWidth / 2) {
					touchPosition = TOUCH_LEFT;
				} else {
					touchPosition = TOUCH_RIGHT;
				}
			}

			break;

		case MotionEvent.ACTION_UP:
			mActivePointerId = INVALID_POINTER_ID;
			resetCardViewOnStack();
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			break;

		case MotionEvent.ACTION_POINTER_UP:
			// Extract the index of the pointer that left the touch sensor
			final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = event.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mActivePointerId = event.getPointerId(newPointerIndex);
			}
			break;
		case MotionEvent.ACTION_MOVE:

			// Find the index of the active pointer and fetch its position
			final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
			final float xMove = event.getX(pointerIndexMove);
			final float yMove = event.getY(pointerIndexMove);

			// Calculate the distance moved
			final float dx = xMove - aDownTouchX;
			final float dy = yMove - aDownTouchY;

			// Move the frame
			aPosX += dx;
			aPosY += dy;

			// calculate the rotation degrees
			float distOriginalX = aPosX - originalX;
			float distOriginalY = aPosY - originalY;
//			float rotation = BASE_ROTATION_DEGREES * 2.f * distOriginalX / parentWidth;
//			if (touchPosition == TOUCH_BELOW) {
//				rotation = -rotation;
//			}

//			Log.v("", "distOriginalX = " + distOriginalX);
//			Log.e("", "distOriginalY = " + distOriginalY);

			double moveDis = Math.sqrt(Math.pow(distOriginalX, 2) + Math.pow(distOriginalY, 2));

			float scalePercent = (float) (moveDis / (originalHeight / 2.5));
//			Log.d("", "scalePercent = " + scalePercent);
			if (scalePercent > 1)
				scalePercent = 1;
			setUpNextFrame(scalePercent, false);

			// no rotation for a while.
//			float degree = (float) (Math.atan((aPosY - originalY) / (aPosX - originalX)) / Math.PI * 180);
//			Log.d("", "degree = " + degree + ", distOriginalX = " + distOriginalX);
//			float result;
//			if (degree >= -90 && degree < -45) {
//				result = degree + 90;
//			} else if (degree >= 45 && degree <= 90) {
//				result = 90 - degree;
//			} else {
//				result = degree;
//			}

			frame.setX(aPosX);
			frame.setY(aPosY);

			helperFlingListener.flingOccurPercent(frame, getMoveDisPercent(), distOriginalY < 0);

			break;

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}
		}

		return true;
	}

	protected void setUpNextFrame(float scalePercent, boolean withAnim) {
		if (nextFrame == null)
			return;
		if (!withAnim) {
			nextFrame.setScaleX(scalePercent);
			nextFrame.setScaleY(scalePercent);
			nextFrame.setAlpha(scalePercent);
		} else {
			nextFrame.animate().scaleX(scalePercent).scaleY(scalePercent).alpha(scalePercent).setDuration(50).start();
		}
	}

	private void resetCardViewOnStack() {
		float distOriginalY = aPosY - originalY;
		if (swipeMode == SWIPEMODE.LEFT_RIGHT) {
			if (aPosX + halfWidth > rightBorder()) {
				setUpNextFrame(1f, true);
				onRightSelected();
			} else if (aPosX + halfWidth < leftBorder()) {
				setUpNextFrame(1f, true);
				onLeftSelected();
			} else {
				resetOrigin();
			}
		} else {
			if (distOriginalY > originalHeight / 2) {
				setUpNextFrame(1f, true);
				onBottomSelected();
			} else if (distOriginalY < 0 && Math.abs(distOriginalY) > originalHeight / 2) {
				setUpNextFrame(1f, true);
				onTopSelected();
			} else {
				resetOrigin();
			}
		}
	}

	public int getMoveDisPercent() {
		return (int) (Math.min(1f, Math.abs((aPosY - originalY)) / (originalHeight / 2)) * 100);
	}

	protected void resetOrigin() {
		aPosX = 0;
		aPosY = 0;
		aDownTouchX = 0;
		aDownTouchY = 0;
		frame.animate().setDuration(100).setInterpolator(new OvershootInterpolator()).x(originalX).y(originalY)
				.rotation(0);
		helperFlingListener.flingResetOrigin(frame);
	}

	public float leftBorder() {
		return parentWidth / 4.f;
	}

	public float rightBorder() {
		return 3 * parentWidth / 4.f;
	}

	public void setRotationDegrees(float degrees) {
		this.BASE_ROTATION_DEGREES = degrees;
	}

	public void onLeftSelected() {
		this.frame.animate().setDuration(100).setInterpolator(new AccelerateInterpolator()).x(-originalWidth)
				.y(getExitPoint(-originalWidth)).setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						helperFlingListener.onCardExited();
						helperFlingListener.leftExit(dataObject);
					}
				}).rotation(-getExitRotation());
	}

	public void onTopSelected() {
		this.frame.animate().setDuration(100).setInterpolator(new AccelerateInterpolator()).x(originalX)
				.y(-originalHeight).setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						helperFlingListener.onCardExited();
						helperFlingListener.topExit(dataObject);
					}
				});
	}

	public void onRightSelected() {
		this.frame.animate().setDuration(100).setInterpolator(new AccelerateInterpolator()).x(parentWidth)
				.y(getExitPoint(parentWidth)).setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						helperFlingListener.onCardExited();
						helperFlingListener.rightExit(dataObject);
					}
				}).rotation(getExitRotation());
	}

	public void onBottomSelected() {
		this.frame.animate().setDuration(100).setInterpolator(new AccelerateInterpolator()).x(originalX)
				.y(3 * originalHeight).setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {

						helperFlingListener.onCardExited();
						helperFlingListener.bottomExit(dataObject);
					}
				});
	}

	private float getExitPoint(int exitXPoint) {
		float[] x = new float[2];
		x[0] = originalX;
		x[1] = aPosX;

		float[] y = new float[2];
		y[0] = originalY;
		y[1] = aPosY;

		LinearRegression regression = new LinearRegression(x, y);

		float result = (float) regression.slope() * exitXPoint + (float) regression.intercept();
		Log.e("", "getExitPoint = " + result);

		return result;
	}

	private float getExitRotation() {
		float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - originalX) / parentWidth;
		if (touchPosition == TOUCH_BELOW) {
			rotation = -rotation;
		}
		return rotation;
	}

}
