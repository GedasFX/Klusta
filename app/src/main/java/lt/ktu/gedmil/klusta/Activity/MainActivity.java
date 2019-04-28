package lt.ktu.gedmil.klusta.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import lt.ktu.gedmil.klusta.Adapter.TreeListAdapter;
import lt.ktu.gedmil.klusta.DatabaseHelper;
import lt.ktu.gedmil.klusta.Model.Tree;
import lt.ktu.gedmil.klusta.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TreeListAdapter adapter;
    private DatabaseHelper db;
    private ListView lw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);
        adapter = new TreeListAdapter(this, R.layout.tree_list_adapter_view, db.getAllTrees());
        lw = findViewById(R.id.treeList);

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
            String item = view.toString();

            Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
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
}
