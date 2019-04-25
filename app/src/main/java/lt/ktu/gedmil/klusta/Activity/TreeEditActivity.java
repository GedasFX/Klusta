package lt.ktu.gedmil.klusta.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.R;
import lt.ktu.gedmil.klusta.Model.Tree;

public class TreeEditActivity extends AppCompatActivity {

    int mTreeId;
    Tree mTree;
    DatabaseHelper mDb;

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
        } else { // Make new
            mTree = new Tree(""); // Blank name tree
        }

        // Fill in the text boxes
        final TextView tw = findViewById(R.id.textEditTreeName);
        tw.setText(mTree.getName());

        Button btnCreate = findViewById(R.id.btnCommit);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update tree
                mTree.setName(tw.getText().toString());
                if (mTree.getName().equals("")) mTree.setName("New Tree");
                if (mTreeId < 0) {
                    mTreeId = mDb.addTree(mTree);
                } else {
                    mDb.editTree(mTreeId, mTree);
                }
                finish();
            }
        });

        Button btnEditContents = findViewById(R.id.btnEditTreeContents);
        btnEditContents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTreeId < 0) {
                    mTreeId = mDb.addTree(mTree);
                }

                Intent intent = new Intent(TreeEditActivity.this, ModifyActivity.class);
                intent.putExtra("TreeId", mTreeId);
                startActivity(intent);
            }
        });
    }
}
