package lt.ktu.gedmil.klusta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TreeManager";

    // Table name
    public static final String TABLE_TREES = "trees";    // Table name


    /// Table TABLE_TREE column key names
    // Keys (primary & foreign)
    public static final String KEY_TREE = "id_tree";    // There can be many trees stored in a table
    public static final String KEY_ID = "id_element";   // There can be many elements stored in a singular tree.
    public static final String KEY_REDIRECT = "id_redirect";    // A jump in the tree for code reuse.
    /*
        Tree is stored as a binary tree, so element indexation will be arithmetic. e.x. Right of 0 (head) is 2*id + 2 (id 2). Left is 2*id+1 (id 1)
     */

    // Columns
    public static final String KEY_BIGTEXT = "bigtext";
    public static final String KEY_SMALLTEXT = "smalltext";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TREES + " (" +
                KEY_TREE + " INTEGER PRIMARY KEY NOT NULL, " +
                KEY_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                KEY_REDIRECT + " INTEGER, " +
                KEY_BIGTEXT + " TEXT, " +
                KEY_SMALLTEXT + " TEXT, " +
                "FOREIGN KEY(" + KEY_REDIRECT + ") REFERENCES "+ TABLE_TREES + "(" + KEY_ID + "))";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    void add(TreeElement tree) {
        add(tree.getId(), tree);
    }

    /**
     * Adds an element to a particular tree
     * @param id
     * @param tree
     */
    public void add(int id, TreeElement tree) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_TREE, tree.getTreeId());
        values.put(KEY_BIGTEXT, tree.getBigText());
        values.put(KEY_SMALLTEXT, tree.getSmallText());
        values.put(KEY_REDIRECT, tree.getRedirect());

        db.insert(TABLE_TREES, null, values);
        db.close();
    }

    /**
     * Fetches a particular element of a particular tree
     * @param tree_id
     * @param id
     * @return
     */
    public TreeElement get(int tree_id, int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(TABLE_TREES, new String[]{KEY_ID, KEY_TREE, KEY_BIGTEXT, KEY_SMALLTEXT, KEY_REDIRECT},
                KEY_ID + "=?", new String[]{String.valueOf(id)},
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
    public List<TreeElement> getAll(int tree_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final String selectQuery = "SELECT * FROM " + TABLE_TREES +
                " WHERE " + KEY_TREE + " = " + tree_id +
                " ORDER BY " + KEY_ID + " DESC";

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
    public void delete(int tree_id, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        final String delete = "DELETE FROM " + TABLE_TREES + " WHERE " + KEY_TREE + "=" + tree_id + " AND " + KEY_ID + "=" + id;
        db.rawQuery(TABLE_TREES, null);
        db.close();
    }

    /**
     * Removes the tree of a given id
     * @param tree_id
     */
    public void delete(int tree_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        final String delete = "DELETE FROM " + TABLE_TREES + " WHERE " + KEY_TREE + "=" + tree_id;
        db.rawQuery(TABLE_TREES, null);
        db.close();
    }
}
