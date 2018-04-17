package com.eyzindskye.med_manager;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.eyzindskye.med_manager.HelperClass.DrugDB;
import com.eyzindskye.med_manager.Utils.DrugRecyclerView;

import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.MONTH;
import static com.eyzindskye.med_manager.HelperClass.DrugDB.TABLE_NAME;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchView = null;
    private RecyclerView recyclerView;
    private DrugRecyclerView mDrugAdapter;
    private SQLiteDatabase mDB;
    private Cursor mCursor;
    private DrugDB mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = new DrugDB(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDB = mDatabase.getReadableDatabase();




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds item to action bar
        getMenuInflater().inflate(R.menu.search, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();

            //Insert the mainQuery from mainActivity into the searchview
//            searchView.setQuery("gensen", true);

            //Expands the search widget once the activity is launched
            MenuItemCompat.expandActionView(searchItem);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        process(query);
//        mCursor = getDrugs(query.toLowerCase());
//        if (mCursor != null){
//            mDrugAdapter.swapCursor(mCursor);
//        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        process(newText);
//        mCursor = getDrugs(newText.toLowerCase());
//        mDrugAdapter.swapCursor(mCursor);
        return false;
    }

    private Cursor getDrugs(String name) {
        return mDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + MONTH + " = ?", new String[]{name});
    }
    private Cursor getAllDrugs(){
        return mDB.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private void process(String name){
        mCursor = getDrugs(name.toLowerCase());
        mDrugAdapter = new DrugRecyclerView(this, mCursor, "");
        recyclerView.setAdapter(mDrugAdapter);
    }
}
