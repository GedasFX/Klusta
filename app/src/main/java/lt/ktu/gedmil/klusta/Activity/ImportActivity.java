package lt.ktu.gedmil.klusta.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.Model.Serial.SerialTree;
import lt.ktu.gedmil.klusta.Model.Serial.SerialTreeElement;
import lt.ktu.gedmil.klusta.Model.Tree;
import lt.ktu.gedmil.klusta.Model.TreeElement;
import lt.ktu.gedmil.klusta.R;

public class ImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        findViewById(R.id.btnImportActivitySubmit).setOnClickListener(v -> {
            SerialTree tree = SerialTree.deserialize(((EditText) findViewById(R.id.editTextImportActivityJson)).getText().toString());
            importToDatabase(tree);
            Toast.makeText(this, "Imported", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Imports elements to the database.
     *
     * @param tree Tree parsed from the JSON serial
     */
    private void importToDatabase(SerialTree tree) {
        DatabaseHelper db = new DatabaseHelper(this);
        int treeId = db.addTree(new Tree(tree.getName()));
        populateDatabase(tree.getElementTree(), treeId, db, null);
    }

    /**
     * Recursive function to populate the database from a JSON serialized tree.
     *
     * @param tree     Tree to serialize. Initialize with head node.
     * @param treeId   Tree id in database
     * @param db       DatabaseHelper object.
     * @param parentId Parent id. Initialize with null.
     * @return Node's id in the database. Final result is the head node's id in database.
     */
    private Integer populateDatabase(SerialTreeElement tree, int treeId, DatabaseHelper db, Integer parentId) {
        // Reached a leaf of the tree
        if (tree == null)
            return null;

        // First create the entry in database
        int elId = db.addTreeElement(treeId, new TreeElement(treeId, tree.getBigText(), tree.getSmallText(), null, parentId, null, null));

        // Traverse the tree going left, then right
        Integer leftId = populateDatabase(tree.getLeft(), treeId, db, elId);
        Integer rightId = populateDatabase(tree.getRight(), treeId, db, elId);

        // Update the original tree element with more children data
        db.updateTreeElement(new TreeElement(elId, treeId, tree.getBigText(), tree.getSmallText(), null, null /* Irrelevant, parent is immutable */, leftId, rightId));

        return elId;
    }
}
