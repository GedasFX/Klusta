package lt.ktu.gedmil.klusta.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.R;
import lt.ktu.gedmil.klusta.Model.Tree;

public class TreeEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_edit);

        Button btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(TreeEditActivity.this);
                db.addTree(new Tree(((TextView) findViewById(R.id.textEditTreeName)).getText().toString()));
                finish();
            }
        });
    }
}
