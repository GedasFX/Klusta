package lt.ktu.gedmil.klusta.Activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import lt.ktu.gedmil.klusta.Adapter.TreeListAdapter;
import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.Model.Serial.SerialTree;
import lt.ktu.gedmil.klusta.Model.Serial.SerialTreeElement;
import lt.ktu.gedmil.klusta.Model.Tree;
import lt.ktu.gedmil.klusta.Model.TreeContainer;
import lt.ktu.gedmil.klusta.Model.TreeElement;
import lt.ktu.gedmil.klusta.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TreeListAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);
        adapter = new TreeListAdapter(this, R.layout.tree_list_adapter_view, db.getAllTrees());
        ListView lw = findViewById(R.id.treeList);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TreeEditActivity.class)));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lw.setAdapter(adapter);
        lw.setOnItemClickListener((parent, view, position, id) -> {
            int treeId = ((Tree) view.getTag()).getId();
            Intent intent = new Intent(MainActivity.this, SwiperActivity.class);
            intent.putExtra("TreeId", treeId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    /**
     * Updates the entry.
     */
    private void updateList() {
        // Update the view after an edit
        adapter.clear();
        adapter.addAll(db.getAllTrees());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_import) {
            startActivity(new Intent(this, ImportActivity.class));
        } else if (id == R.id.nav_support) {
            startActivity(new Intent(this, DonateActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Handler for Edit button click. Information about the tree is stored in the tag.
     */
    public void onEditButtonClick(View view) {
        startActivity(new Intent(this, TreeEditActivity.class)
                .putExtra("TreeId", ((Tree) view.getTag()).getId()));
        Log.d("Main", view.getTag().toString());
    }

    /**
     * Handler for Delete button click. Information about the tree is stored in the tag.
     */
    public void onDeleteButtonClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_confirm_delete)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    Tree tree = ((Tree) view.getTag());
                    db.deleteTree(tree.getId());
                    adapter.remove(tree);
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
            // Do nothing
        }).show();
    }

    /**
     * Handler for Share button click. Information about the tree is stored in the tag.
     */
    public void onShareButtonClick(View view) {
        Tree dbTree = (Tree) view.getTag();
        TreeContainer dbContents = new TreeContainer(dbTree.getId(), db.getAllTreeElements(dbTree.getId()));
        TreeElement head = dbContents.getHead();
        SerialTreeElement el = new SerialTreeElement(head.getBigText(), head.getSmallText());
        fillSerialNode(el, head, dbContents);

        SerialTree serialTree = new SerialTree(dbTree.getName(), el);

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("JSONTree", serialTree.serialize());
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(this, R.string.text_copied, Toast.LENGTH_SHORT).show();
    }

    /**
     * Transers TreeContainer data to SerialTreeElement type. Updates serialTreeElements data.
     *
     * @param serialTreeElement Element to transfer data over to. Initialize with node with empty left and right children.
     * @param current           Current element of contents container
     * @param contents          Container for initial data
     */
    public void fillSerialNode(SerialTreeElement serialTreeElement, TreeElement current, TreeContainer contents) {
        if (current.getLeftId() == null || current.getRightId() == null)
            return;

        TreeElement left = contents.get(current.getLeftId());
        TreeElement right = contents.get(current.getRightId());
        SerialTreeElement leftSerial = new SerialTreeElement(left.getBigText(), left.getSmallText());
        SerialTreeElement rightSerial = new SerialTreeElement(right.getBigText(), right.getSmallText());
        serialTreeElement.setLeft(leftSerial);
        serialTreeElement.setRight(rightSerial);

        fillSerialNode(leftSerial, left, contents);
        fillSerialNode(rightSerial, right, contents);
    }
}
