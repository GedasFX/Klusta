package lt.ktu.gedmil.klusta.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.Model.TreeElement;
import lt.ktu.gedmil.klusta.R;
import lt.ktu.gedmil.klusta.Model.Tree;

public class TreeEditActivity extends AppCompatActivity {

    private int mTreeId;
    private Tree mTree;
    private DatabaseHelper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_edit);

        // Attempts to read for edit purposes. On create TreeId tag will be null. thus mTreeId  will eq to -1.
        try {
            mTreeId = getIntent().getExtras().getInt("TreeId");
        } catch (Exception e) {
            mTreeId = -1;
        }

        mDb = new DatabaseHelper(this);

        // If tree exists in database (for edit)
        if (mTreeId >= 0) {
            mTree = mDb.getTree(mTreeId);
            mDb.updateTreeOpenTime(mTreeId);
        } else { // Make new
            mTree = new Tree(""); // Blank name tree
        }

        // Fill in the text boxes
        final TextView tw = findViewById(R.id.textEditTreeEditActivityTreeName);
        tw.setText(mTree.getName());

        Button btnCreate = findViewById(R.id.btnCommit);
        btnCreate.setOnClickListener(v -> {
            // Update tree
            updateTree(tw.getText().toString());
            finish();
        });

        Button btnEditContents = findViewById(R.id.btnTreeEditActivityEditTreeContents);
        btnEditContents.setOnClickListener(v -> {
            // If a tree id does not exist.
            if (mTreeId < 0) {
                mTreeId = mDb.addTree(mTree);
            }

            // Start activity with tree information
            Intent intent = new Intent(TreeEditActivity.this, ModifyActivity.class);
            intent.putExtra("TreeId", mTreeId);
            startActivity(intent);
        });
    }

    private void updateTree(String name) {
        mTree.setName(name);
        if (mTreeId < 0) {
            mTreeId = mDb.addTree(mTree);
            mDb.addTreeElement(mTreeId, new TreeElement(mTreeId));
        } else {
            mDb.editTree(mTreeId, mTree);
        }
    }
}
