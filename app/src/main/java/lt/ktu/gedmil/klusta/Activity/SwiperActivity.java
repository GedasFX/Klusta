package lt.ktu.gedmil.klusta.Activity;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Objects;

import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.Model.TreeContainer;
import lt.ktu.gedmil.klusta.R;

public class SwiperActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final float FLING_VELOCITY_THRESHOLD = 100;
    private int mTreeId;
    private DatabaseHelper mDb;
    private TreeContainer mTree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiper);

        mTreeId = Objects.requireNonNull(getIntent().getExtras()).getInt("TreeId");
        mDb = new DatabaseHelper(this);
        mDb.updateTreeOpenTime(mTreeId);
        mTree = new TreeContainer(mTreeId, mDb.getAllTreeElements(mTreeId));
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean consumeEvent = false;

        float deltaX = moveEvent.getX() - downEvent.getX();
        float deltaY = moveEvent.getY() - downEvent.getY();

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Is left or right fling
            if (Math.abs(velocityX) > FLING_VELOCITY_THRESHOLD) {
                if (deltaX > 0) {
                    // Swipe Right
                    onSwipeRight();
                } else {
                    // Swipe Left
                    onSwipeLeft();
                }
                consumeEvent = true;
            }
        } else {
            // Is up or down fling
            if (Math.abs(velocityY) > FLING_VELOCITY_THRESHOLD) {
                if (deltaY < 0) {
                    // Swipe Up
                    onSwipeDown();
                    consumeEvent = true;
                }
            }
        }

        return consumeEvent;
    }

    private void onSwipeDown() {
        // On swipe down move UP the tree
    }

    private void onSwipeRight() {
        // On swipe right move down the LEFT part of the tree
    }

    private void onSwipeLeft() {
        // On swipe left move down the RIGHT part of the tree
    }
}
