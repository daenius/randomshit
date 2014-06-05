package com.popo.mr.touchanimationtest.app;

import android.app.Activity;
import android.os.Bundle;
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
    private final int X_VELOCITY_THRESHOLD = 1000;
    private final int X_POSITION_THRESHOLD = 300;
    private final int X_LEFT_THRESHOLD = 100;
    private float previousDownX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rightPanel = findViewById(R.id.rightpanel);
        rightPanel.setVisibility(View.GONE);
        rightPanel.setX(1200f);
        Log.v("WTF", "WTF IS THE LEFT " + rightPanel.getLeft());
        this.currentContentFragmentState = ContentFragmentState.CLOSED;

    }

    public void togglePanel(View view) {

        switch (this.currentContentFragmentState) {
            case CLOSED:

                animateOpen();

                break;
            case OPEN:
                animateClose();
                break;
        }
    }

    // FIX ME: Smoother animations would be great
    private void animateOpen() {
        this.rightPanel.setVisibility(View.VISIBLE);
        rightPanel.animate().setDuration(1500).translationX(0);
        this.currentContentFragmentState = ContentFragmentState.OPEN;
    }

    private void animateClose() {
        rightPanel.animate().setDuration(1500).translationX(1200);
        this.currentContentFragmentState = ContentFragmentState.CLOSED;
    }

    private void animateSwipeOpen(){
        this.rightPanel.setVisibility(View.VISIBLE);
        rightPanel.animate().setDuration(300).translationX(0);
        this.currentContentFragmentState = ContentFragmentState.OPEN;
    }
    private void animateSwipeClose(){
        rightPanel.animate().setDuration(300).translationX(1200);
        this.currentContentFragmentState = ContentFragmentState.CLOSED;
    }

//    @Override
//    public boolean dispatchTouchEvent( MotionEvent motionEvent ){
//        if ( shouldInterceptTouchEvent(motionEvent)){
//            return true;
//        }
//        return super.dispatchTouchEvent(motionEvent);
//    }

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
                    mVelocityTracker.computeCurrentVelocity(1000);
                    if (motionEvent.getX() > this.previousDownX) {
                        this.rightPanel.setX(motionEvent.getX(index) - this.previousDownX);
                    }


                    break;
                case MotionEvent.ACTION_UP:
                    float xVelocity = mVelocityTracker.getXVelocity(pointerId);
                    Log.d("POPO", "X velocity: " + xVelocity);
                    if (xVelocity > this.X_VELOCITY_THRESHOLD || (motionEvent.getX() - this.previousDownX > this.X_POSITION_THRESHOLD)) {
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

    private boolean shouldInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.currentContentFragmentState == ContentFragmentState.OPEN) {
            return true;
        }
        return false;
    }
}
