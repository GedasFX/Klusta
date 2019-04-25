package lt.ktu.gedmil.klusta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lt.ktu.gedmil.klusta.Model.Tree;
import lt.ktu.gedmil.klusta.Model.TreeElement;

/**
 * Provides an interface to the database for TreeData and Trees tables
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Klusta";

    // Table name
    private static final String TABLE_TREEDATA = "treedata";
    private static final String TABLE_TREES = "trees";


    /// Table TABLE_TREE column key names
    // Keys (primary & foreign)
    private static final String KEY_ID = "id";                   // Identification of generic tables
    private static final String KEY_ID_TREE = "id_tree";         // There can be many trees stored in a table
    private static final String KEY_ID_REDIRECT = "id_redirect"; // A jump in the tree for loops/code reuse.
    private static final String KEY_ID_PARENT = "id_parent";     // Id of the parent node in the tree
    private static final String KEY_ID_LEFT = "id_left";         // Id of the swipe left node (no)
    private static final String KEY_ID_RIGHT = "id_right";       // Id of the swipe right node (yes)
    /*
        Tree is stored as a binary tree, so element indexation will be arithmetic. e.x. Right of 0 (head) is 2*id + 2 (id 2). Left is 2*id+1 (id 1)
     */

    // Columns
    private static final String KEY_BIGTEXT = "bigtext";         // Header text column
    private static final String KEY_SMALLTEXT = "smalltext";     // Main body text column
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST_MODIFIED = "lastmodified";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TREEDATA_TABLE = "CREATE TABLE " + TABLE_TREEDATA + " (" +
                KEY_ID + " INTEGER PRIMARY KEY NOT NULL, " + // Element id
                KEY_ID_TREE + " INTEGER NOT NULL, " +        // Tree id
                KEY_ID_REDIRECT + " INTEGER, " +    // Foreign keys for redirect and traversal.
                KEY_ID_PARENT + " INTEGER, " +
                KEY_ID_LEFT + " INTEGER, " +
                KEY_ID_RIGHT + " INTEGER, " +
                KEY_BIGTEXT + " TEXT, " +   // Text
                KEY_SMALLTEXT + " TEXT, " +
                "FOREIGN KEY(" + KEY_ID_TREE + ") REFERENCES " + TABLE_TREES + "(" + KEY_ID + "), " +
                "FOREIGN KEY(" + KEY_ID_REDIRECT + ") REFERENCES " + TABLE_TREEDATA + "(" + KEY_ID + "), " +
                "FOREIGN KEY(" + KEY_ID_PARENT + ") REFERENCES " + TABLE_TREEDATA + "(" + KEY_ID + "), " +
                "FOREIGN KEY(" + KEY_ID_LEFT + ") REFERENCES " + TABLE_TREEDATA + "(" + KEY_ID + "), " +
                "FOREIGN KEY(" + KEY_ID_RIGHT + ") REFERENCES " + TABLE_TREEDATA + "(" + KEY_ID + "))";

        final String CREATE_TREES_TABLE = "CREATE TABLE " + TABLE_TREES + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                KEY_NAME + " TEXT NOT NULL, " +
                KEY_LAST_MODIFIED + " INTEGER NOT NULL)";


        db.execSQL(CREATE_TREEDATA_TABLE);
        db.execSQL(CREATE_TREES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREEDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Adds an element to a particular tree
     *
     * @param tree_id Tree Id
     * @param tree    Tree Data
     */
    public int addTreeElement(int tree_id, TreeElement tree) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_TREE, tree_id);
        values.put(KEY_BIGTEXT, tree.getBigText());
        values.put(KEY_SMALLTEXT, tree.getSmallText());
        values.put(KEY_ID_REDIRECT, tree.getRedirect());
        values.put(KEY_ID_PARENT, tree.getParentId());
        values.put(KEY_ID_LEFT, tree.getLeftId());
        values.put(KEY_ID_RIGHT, tree.getRightId());

        int id = (int) db.insert(TABLE_TREEDATA, null, values);
        db.close();

        return id;
    }

    /**
     * Adds a tree to trees table
     *
     * @param tree Tree to add to the table
     * @return Added tree's id
     */
    public int addTree(Tree tree) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tree.getName());
        values.put(KEY_LAST_MODIFIED, System.currentTimeMillis());

        int treeId = (int) db.insert(TABLE_TREES, null, values);
        db.close();

        return treeId;
    }

    /**
     * Fetches a particular element of a particular tree
     *
     * @param id Element Id
     * @return Tree element with given element and tree ids.
     */
    public TreeElement getTreeElement(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(TABLE_TREEDATA,
                new String[]{KEY_ID, KEY_ID_TREE, KEY_BIGTEXT, KEY_SMALLTEXT, KEY_ID_REDIRECT, KEY_ID_PARENT, KEY_ID_LEFT, KEY_ID_RIGHT},
                KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;
        TreeElement el = new TreeElement(id,
                Integer.parseInt(cursor.getString(1)), // KEY_ID_TREE
                cursor.getString(2), // KEY_BIGTEXT
                cursor.getString(3), // KEY_SMALLTEXT
                Integer.getInteger(cursor.getString(4)), // KEY_ID_REDIRECT
                Integer.getInteger(cursor.getString(5)), // KEY_ID_PARENT
                Integer.getInteger(cursor.getString(6)), // KEY_ID_LEFT
                Integer.getInteger(cursor.getString(7))); // KEY_ID_RIGHT

        cursor.close();
        db.close();
        return el;
    }

    public Tree getTree(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(TABLE_TREES, new String[]{KEY_ID, KEY_NAME, KEY_LAST_MODIFIED},
                KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;
        Tree el = new Tree(id, cursor.getString(1),
                Long.parseLong(cursor.getString(2)));

        cursor.close();
        db.close();
        return el;
    }

    /**
     * Fetches all elements of a given tree
     *
     * @param tree_id Tree Id
     * @return All of the nodes of a particular tree.
     */
    public List<TreeElement> getAllTreeElements(int tree_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final String selectQuery = "SELECT * FROM " + TABLE_TREEDATA +
                " WHERE " + KEY_ID_TREE + " = " + tree_id;

        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TreeElement> tree = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                TreeElement treeElement = new TreeElement(Integer.parseInt(cursor.getString(1)), tree_id); // Element id, tree id
                treeElement.setRedirect(Integer.getInteger(cursor.getString(2)));
                treeElement.setBigText(cursor.getString(3));
                treeElement.setSmallText(cursor.getString(4));

                tree.add(treeElement);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tree;
    }

    public List<Tree> getAllTrees() {
        SQLiteDatabase db = this.getReadableDatabase();

        final String selectQuery = "SELECT * FROM " + TABLE_TREES + "" +
                " ORDER BY " + KEY_LAST_MODIFIED + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Tree> trees = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Tree tree = new Tree(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Long.parseLong(cursor.getString(2))); // Element id, tree id, last opened
                trees.add(tree);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return trees;
    }

    /**
     * Removes a node of a given id of a tree of a given id
     *
     * @param id Element Id
     */
    public void deleteTreeElement(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //final String delete = "DELETE FROM " + TABLE_TREEDATA + " WHERE " + KEY_ID + "=" + id;
        db.delete(TABLE_TREEDATA, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Removes the tree of a given id and all of the associate data
     *
     * @param id Tree id no.
     */
    public void deleteTree(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        final String deleteTreeData = "DELETE FROM " + TABLE_TREEDATA + " WHERE " + KEY_ID_TREE + "=" + id;
        final String deleteTree = "DELETE FROM " + TABLE_TREES + " WHERE " + KEY_ID + "=" + id;
        db.execSQL(deleteTreeData);
        db.execSQL(deleteTree);
        db.close();
    }

    /**
     * Updates the tree name.
     *
     * @param treeId Id of the tree to update
     * @param tree   Tree with name element of the name
     */
    public void editTree(int treeId, Tree tree) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tree.getName());
        values.put(KEY_LAST_MODIFIED, System.currentTimeMillis());

        db.update(TABLE_TREES, values, KEY_ID + "=?",
                new String[]{String.valueOf(treeId)});

        db.close();
    }

    /**
     * Updates tree last opened field for most recently opened purposes.
     *
     * @param treeId Tree's id.
     */
    public void updateTreeOpenTime(int treeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LAST_MODIFIED, System.currentTimeMillis());

        db.update(TABLE_TREES, values, KEY_ID + "=?",
                new String[]{String.valueOf(treeId)});

        db.close();
    }
}
