package lt.ktu.gedmil.klusta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Klusta";

    // Table name
    private static final String TABLE_TREEDATA = "treedata";
    private static final String TABLE_TREES = "trees";


    /// Table TABLE_TREE column key names
    // Keys (primary & foreign)
    private static final String KEY_ID = "id";                   // Identification of generic tables
    private static final String KEY_ID_TREE = "id_tree";         // There can be many trees stored in a table
    private static final String KEY_ID_ELEMENT = "id_element";   // There can be many elements stored in a singular tree.
    private static final String KEY_ID_REDIRECT = "id_redirect"; // A jump in the tree for loops/code reuse.
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
                KEY_ID_TREE + " INTEGER PRIMARY KEY NOT NULL, " +
                KEY_ID_ELEMENT + " INTEGER PRIMARY KEY NOT NULL, " +
                KEY_ID_REDIRECT + " INTEGER, " +
                KEY_BIGTEXT + " TEXT, " +
                KEY_SMALLTEXT + " TEXT, " +
                "FOREIGN KEY(" + KEY_ID_REDIRECT + ") REFERENCES "+ TABLE_TREEDATA + "(" + KEY_ID_ELEMENT + "))";

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
     * @param id
     * @param tree
     */
    public void addTreeElement(int id, int tree_id, TreeElement tree) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_ELEMENT, id);
        values.put(KEY_ID_TREE, tree_id);
        values.put(KEY_BIGTEXT, tree.getBigText());
        values.put(KEY_SMALLTEXT, tree.getSmallText());
        values.put(KEY_ID_REDIRECT, tree.getRedirect());

        db.insert(TABLE_TREEDATA, null, values);
        db.close();
    }

    public int addTree(Tree tree) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tree.getName());

        int treeId = (int) db.insert(TABLE_TREES, null, values);
        db.close();

        return treeId;
    }

    /**
     * Fetches a particular element of a particular tree
     * @param tree_id
     * @param id
     * @return
     */
    public TreeElement getTreeElement(int tree_id, int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(TABLE_TREEDATA, new String[]{KEY_ID_ELEMENT, KEY_ID_TREE, KEY_BIGTEXT, KEY_SMALLTEXT, KEY_ID_REDIRECT},
                KEY_ID_ELEMENT + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        TreeElement el = new TreeElement(id, tree_id, cursor.getString(2), cursor.getString(3), Integer.getInteger(cursor.getString(4)));

        cursor.close();
        db.close();
        return el;
    }

    /**
     * Fetches all elements of a given tree
     * @param tree_id
     * @return
     */
    public List<TreeElement> getAllTreeElements(int tree_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final String selectQuery = "SELECT * FROM " + TABLE_TREEDATA +
                " WHERE " + KEY_ID_TREE + " = " + tree_id +
                " ORDER BY " + KEY_ID_ELEMENT + " DESC";

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

    /**
     * Removes a node of a given id of a tree of a given id
     * @param tree_id
     * @param id
     */
    public void deleteTreeElement(int tree_id, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        final String delete = "DELETE FROM " + TABLE_TREEDATA + " WHERE " + KEY_ID_TREE + "=" + tree_id + " AND " + KEY_ID_ELEMENT + "=" + id;
        db.rawQuery(TABLE_TREEDATA, null);
        db.close();
    }

    /**
     * Removes the tree of a given id and all of the associate data
     * @param id Tree id no.
     */
    public void deleteTree(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        final String deleteTreeData = "DELETE FROM " + TABLE_TREEDATA + " WHERE " + KEY_ID_TREE + "=" + id;
        final String deleteTree = "DELETE FROM " + TABLE_TREES + " WHERE " + KEY_ID + "=" + id;
        db.execSQL(deleteTreeData, null);
        db.execSQL(deleteTree, null);
        db.close();
    }
}
