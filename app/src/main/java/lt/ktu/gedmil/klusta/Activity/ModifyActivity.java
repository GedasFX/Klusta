package lt.ktu.gedmil.klusta.Activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;
import java.util.Objects;

import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.Model.TreeElement;
import lt.ktu.gedmil.klusta.R;

public class ModifyActivity extends AppCompatActivity {

    private int mTreeId;
    private SparseArray<TreeElement> mTree;
    private DatabaseHelper mDb;
    private boolean mHasUnsavedChanges = false;
    private TreeElement mCurrent;

    private ImageButton btnMoveUp;
    private ImageButton btnMoveLeft;
    private ImageButton btnMoveRight;

    private EditText tbBigText;
    private EditText tbSmallText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        // If getExtras is null, there are bigger problems in the program.
        mTreeId = Objects.requireNonNull(getIntent().getExtras()).getInt("TreeId");
        mDb = new DatabaseHelper(this);
        List<TreeElement> treeList = mDb.getAllTreeElements(mTreeId);
        mCurrent = findHead(treeList); // Order matters. This adds to the list if empty.
        mTree = toSparseArray(treeList);
        //mDb.getWritableDatabase().execSQL("DELETE FROM treedata");
        // Event handlers
        findViewById(R.id.btnActivityModifyExit).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnActivityModifyCommit).setOnClickListener(v -> saveChanges());

        findViewById(R.id.btnActivityModifyMoveUp).setOnClickListener(v -> onMoveUp());
        findViewById(R.id.btnActivityModifyMoveRight).setOnClickListener(v -> onMoveRight());
        findViewById(R.id.btnActivityModifyMoveLeft).setOnClickListener(v -> onMoveLeft());

        btnMoveUp = findViewById(R.id.btnActivityModifyMoveUp);
        btnMoveLeft = findViewById(R.id.btnActivityModifyMoveLeft);
        btnMoveRight = findViewById(R.id.btnActivityModifyMoveRight);

        tbBigText = findViewById(R.id.editTextActivityModifyBigText);
        tbSmallText = findViewById(R.id.editTextActivityModifySmallText);

        onCurrentChanged();
    }

    private TreeElement findHead(List<TreeElement> tree) {
        for (TreeElement te : tree) {
            if (te.getParentId() == null) {
                return te;
            }
        }

        // Head not found - empty tree. Populate it.
        TreeElement te = new TreeElement(mTreeId);
        addToDatabase(te);
        tree.add(te);
        return te;
    }

    private SparseArray<TreeElement> toSparseArray(List<TreeElement> tree) {
        SparseArray<TreeElement> sa = new SparseArray<TreeElement>();
        for (TreeElement te : tree) {
            sa.append(te.getId(), te);
        }

        return sa;
    }

    /**
     * Adds an element to the database and returns the element with the generated id.
     * Changes the parameter te to have an id value. Returns the exact same object as input.
     *
     * @param te Tree element with id = null
     */
    private void addToDatabase(TreeElement te) {
        int id = mDb.addTreeElement(mTreeId, te);
        te.setId(id);

    }

    private TreeElement findById(int id, List<TreeElement> tree) {
        for (TreeElement te : tree) {
            if (te.getId() == id) {
                return te;
            }
        }

        return null;
    }

    private void onMoveLeft() {
        if (mCurrent.getLeftId() == null) { // If button is create (+)
            addNewNodePair();
        } else { // If button is an arrow
            mCurrent = mTree.get(mCurrent.getLeftId());
        }

        onCurrentChanged();
    }

    private void onMoveRight() {
        if (mCurrent.getRightId() == null) { // If button is create (+)
            addNewNodePair();
        } else { // If button is an arrow
            mCurrent = mTree.get(mCurrent.getRightId());
        }

        onCurrentChanged();
    }

    private void addNewNodePair() {
        // Left
        TreeElement nTe = new TreeElement(mTreeId);
        nTe.setParentId(mCurrent.getId());
        addToDatabase(nTe);
        mCurrent.setLeftId(nTe.getId());
        mTree.put(nTe.getId(), nTe);

        // Right
        nTe = new TreeElement(mTreeId);
        nTe.setParentId(mCurrent.getId());
        addToDatabase(nTe);
        mCurrent.setRightId(nTe.getId());
        mTree.put(nTe.getId(), nTe);

        // Update the current element in the database for leak prevention
        mDb.updateTreeElement(mCurrent);
    }

    /**
     * Handler for up arrow click. Only clickable when it is possible to traverse.
     */
    private void onMoveUp() {
        mCurrent = mTree.get(mCurrent.getParentId());
        onCurrentChanged();
    }

    /**
     * Updates the icons of the activity to represent tree better.
     */
    private void onCurrentChanged() {
        int accentColor = getResources().getColor(R.color.colorPrimaryDark);
        Drawable blockImg = getDrawable(R.drawable.ic_block_black_24dp);
        Drawable addImg = getDrawable(R.drawable.ic_add);
        Drawable diagonalArrowImg = getDrawable(R.drawable.ic_chunky_arrow_bottom_right);
        Drawable upArrowImg = getDrawable(R.drawable.ic_chunky_arrow_up);

        if (mCurrent.getParentId() == null) {
            btnMoveUp.setClickable(false);
            btnMoveUp.setBackgroundColor(Color.BLACK);
            btnMoveUp.setBackground(blockImg);
        } else {
            btnMoveUp.setClickable(true);
            btnMoveUp.setBackgroundColor(accentColor);
            btnMoveUp.setBackground(upArrowImg);
        }

        if (mCurrent.getLeftId() == null) {
            btnMoveLeft.setBackground(addImg);
        } else {
            btnMoveLeft.setBackground(diagonalArrowImg);
        }

        if (mCurrent.getRightId() == null) {
            btnMoveRight.setBackground(addImg);
        } else {
            btnMoveRight.setBackground(diagonalArrowImg);
        }
    }

    /**
     * Handles for on exit behavior. Happens when you click either exit button, or back button
     */
    @Override
    public void onBackPressed() {
        if (mHasUnsavedChanges) {
            // Create a dialog box for confirmation if you wish to save changes before exiting
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_confirm_exit_nochanges)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        saveChanges();
                        ModifyActivity.super.onBackPressed(); // Has to be in the dialog, otherwise will leak
                    }).setNegativeButton(R.string.no, (dialog, which) -> {
                // Continue without saving
                ModifyActivity.super.onBackPressed(); // Has to be in the dialog, otherwise will leak
            }).setNeutralButton(R.string.cancel, (dialog, which) -> {
                // Continue. Consume the onBackPressed event.
            }).show();
        } else {
            super.onBackPressed();
        }
    }

    private void saveChanges() {
        mDb.upsertTreeElements(mTree);
    }
}
