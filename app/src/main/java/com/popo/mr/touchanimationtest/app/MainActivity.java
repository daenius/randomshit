package com.popo.mr.touchanimationtest.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;


public class MainActivity extends Activity {


    RightPanel rightPanel;

    private int windowWidth;

    private VelocityTracker mVelocityTracker = null;
    private int xVelocityThreshold;
    private int xPositionThreshold;
    private final double X_POSITION_THRESHOLD_RATIO = 0.4;
    private final double X_VELOCITY_THRESHOLD_RATIO = 0.20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rightPanel = (RightPanel) findViewById(R.id.rightpanel);
        rightPanel.setVisibility(View.GONE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.windowWidth = dm.widthPixels;
        rightPanel.setX(this.windowWidth);
        rightPanel.setWindowWidth(this.windowWidth);
        this.xPositionThreshold = (int) (this.windowWidth * X_POSITION_THRESHOLD_RATIO);
        this.xVelocityThreshold = (int) (this.windowWidth * X_VELOCITY_THRESHOLD_RATIO);

    }

    public void togglePanel(View view) {

        this.rightPanel.togglePanel();
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (rightPanel.getCurrentContentFragmentState() == RightPanel.ContentFragmentState.OPEN) {
            int index = motionEvent.getActionIndex();
            int action = motionEvent.getActionMasked();
            int pointerId = motionEvent.getPointerId(index);

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    mVelocityTracker.addMovement(motionEvent);
                    if (motionEvent.getX() > rightPanel.getPreviousDownX()) {
                        rightPanel.setX(motionEvent.getX(index) - rightPanel.getPreviousDownX());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float xVelocity = mVelocityTracker.getXVelocity(pointerId);
                    if (xVelocity > this.xVelocityThreshold || (motionEvent.getX() - rightPanel.getPreviousDownX() > this.xPositionThreshold)) {
                        rightPanel.animateSwipeClose();
                    } else {
                        rightPanel.animateSwipeOpen();
                    }
                    mVelocityTracker.clear();
                    break;
                default:
                    Log.d("LOGTAG", "Nothing we care about atm");
                    break;
            }

            return true;
        }

        return true;
    }

}
