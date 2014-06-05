package com.popo.mr.touchanimationtest.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;


public class MainActivity extends Activity {

    public enum ContentFragmentState {
        CLOSED, OPEN, CLOSING, OPENING;
    }

    public ContentFragmentState currentContentFragmentState;
    View rightPanel;
    private VelocityTracker mVelocityTracker = null;
    private int xVelocityThreshold;
    private int xPositionThreshold;
    private final double X_POSITION_THRESHOLD_RATIO = 0.4;
    private final double X_VELOCITY_THRESHOLD_RATIO = 0.15;
    private float previousDownX;
    private int windowWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rightPanel = findViewById(R.id.rightpanel);
        rightPanel.setVisibility(View.GONE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.windowWidth = dm.widthPixels;
        rightPanel.setX(this.windowWidth);
        this.xPositionThreshold = (int) (this.windowWidth * X_POSITION_THRESHOLD_RATIO);
        this.xVelocityThreshold = (int) (this.windowWidth * X_VELOCITY_THRESHOLD_RATIO);
//        Log.d("POPO", this.windowWidth + " " + this.xPositionThreshold + " " + this.xVelocityThreshold);
        this.currentContentFragmentState = ContentFragmentState.CLOSED;

    }

    public void togglePanel(View view) {

        switch (this.currentContentFragmentState) {
            case CLOSED:
                animateOpen();
                break;
        }
    }

    // FIX ME: Smoother animations would be great
    private void animateOpen() {
        this.rightPanel.setVisibility(View.VISIBLE);
        rightPanel.animate().setDuration(1000).translationX(0);
        this.currentContentFragmentState = ContentFragmentState.OPEN;
    }

    private void animateClose() {
        rightPanel.animate().setDuration(1000).translationX(this.windowWidth);
        this.currentContentFragmentState = ContentFragmentState.CLOSED;
    }

    private void animateSwipeOpen() {
        this.rightPanel.setVisibility(View.VISIBLE);
        rightPanel.animate().setDuration(300).translationX(0);
        this.currentContentFragmentState = ContentFragmentState.OPEN;
    }

    private void animateSwipeClose() {
        rightPanel.animate().setDuration(300).translationX(this.windowWidth);
        this.currentContentFragmentState = ContentFragmentState.CLOSED;
    }

    @Override
    public void onBackPressed() {
        if (this.currentContentFragmentState == ContentFragmentState.OPEN) {
            this.animateClose();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentContentFragmentState == ContentFragmentState.OPEN) {
            int index = motionEvent.getActionIndex();
            int action = motionEvent.getActionMasked();
            int pointerId = motionEvent.getPointerId(index);

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(motionEvent);
                    this.previousDownX = motionEvent.getX(index);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mVelocityTracker.addMovement(motionEvent);
                    if (motionEvent.getX() > this.previousDownX) {
                        this.rightPanel.setX(motionEvent.getX(index) - this.previousDownX);
                    }


                    break;
                case MotionEvent.ACTION_UP:
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float xVelocity = mVelocityTracker.getXVelocity(pointerId);
                    if (xVelocity > this.xVelocityThreshold || (motionEvent.getX() - this.previousDownX > this.xPositionThreshold)) {
                        this.animateSwipeClose();
                    } else {
                        this.animateSwipeOpen();
                    }
                    break;
                default:
                    Log.d("LOGTAG", "Nothing we care about atm");
                    break;
            }

            return super.onTouchEvent(motionEvent);
        }

        return super.onTouchEvent(motionEvent);
    }

}
