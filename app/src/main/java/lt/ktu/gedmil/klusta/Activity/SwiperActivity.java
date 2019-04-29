package lt.ktu.gedmil.klusta.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.Model.TreeContainer;
import lt.ktu.gedmil.klusta.Model.TreeElement;
import lt.ktu.gedmil.klusta.R;

public class SwiperActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final float FLING_VELOCITY_THRESHOLD = 5;
    private TreeContainer mTree;
    private TreeElement mCurrent;

    private TextView mBigText;
    private TextView mSmallText;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiper);

        int mTreeId = Objects.requireNonNull(getIntent().getExtras()).getInt("TreeId");
        DatabaseHelper db = new DatabaseHelper(this);
        db.updateTreeOpenTime(mTreeId);
        mTree = new TreeContainer(mTreeId, db.getAllTreeElements(mTreeId));
        mCurrent = mTree.getHead();

        mBigText = findViewById(R.id.tvSwiperActivityBigText);
        mSmallText = findViewById(R.id.tvSwiperActivitySmallText);

        gestureDetector = new GestureDetector(this);

        updateText();
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

        Log.d("SwiperActivity", String.valueOf(deltaX));
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
                if (deltaY > 0) {
                    // Swipe Up
                    onSwipeDown();
                    consumeEvent = true;
                }
            }
        }

        return consumeEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void onSwipeDown() {
        // On swipe down move UP the tree
        Integer parentId = mCurrent.getParentId();
        if (parentId == null) {
            Toast.makeText(this, R.string.at_root_of_tree, Toast.LENGTH_SHORT).show();
        } else {
            mCurrent = mTree.get(parentId);
            updateText();
        }
    }

    private void onSwipeRight() {
        // On swipe right move down the LEFT part of the tree
        Integer leftId = mCurrent.getLeftId();
        if (leftId == null) {
            Toast.makeText(this, R.string.at_end_of_tree, Toast.LENGTH_SHORT).show();
        } else {
            mCurrent = mTree.get(leftId);
            updateText();
        }
    }

    private void onSwipeLeft() {
        // On swipe left move down the RIGHT part of the tree
        Integer rightId = mCurrent.getRightId();
        if (rightId == null) {
            Toast.makeText(this, R.string.at_end_of_tree, Toast.LENGTH_SHORT).show();
        } else {
            mCurrent = mTree.get(rightId);
            updateText();
        }
    }

    private void updateText() {
        mBigText.setText(mCurrent.getBigText());
        mSmallText.setText(mCurrent.getSmallText());
    }
}
