package com.eyzindskye.med_manager;


import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

//import com.eyzindskye.med_manager.Databases.AprDB;
//import com.eyzindskye.med_manager.Databases.AugDB;
//import com.eyzindskye.med_manager.Databases.DecDB;
//import com.eyzindskye.med_manager.Databases.FebDB;
//import com.eyzindskye.med_manager.Databases.JanDB;
//import com.eyzindskye.med_manager.Databases.JulDB;
//import com.eyzindskye.med_manager.Databases.JunDB;
//import com.eyzindskye.med_manager.Databases.MarDB;
//import com.eyzindskye.med_manager.Databases.MayDB;
//import com.eyzindskye.med_manager.Databases.NovDB;
//import com.eyzindskye.med_manager.Databases.OctDB;
//import com.eyzindskye.med_manager.Databases.SepDB;

import com.eyzindskye.med_manager.HelperClass.DrugDB;
import com.eyzindskye.med_manager.Utils.DrugRecyclerView;

import java.util.Calendar;

import static android.provider.BaseColumns._ID;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.DESC;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.DURATION;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.INTERVAL;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.MONTH;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.NAME;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.QUANTITY;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.START_TIME;
import static com.eyzindskye.med_manager.HelperClass.DrugDB.TABLE_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private Spinner mSpinner;
    private String[] month = {"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

    private ArrayAdapter arrMonth;
    private RecyclerView recyclerView, recyclerViewSpinner;
    private TextView tvMonth;
    private DrugRecyclerView mDrugAdapter;
    private SQLiteDatabase mDB;
    private Cursor mCursor;
    private DrugDB mDatabase;
    private String monthName;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        arrMonth = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, month);
        mSpinner = view.findViewById(R.id.spinnner);
        arrMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrMonth);

        mDatabase = new DrugDB(getActivity());
        mDB = mDatabase.getReadableDatabase();

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerViewSpinner = view.findViewById(R.id.recyclerview_spinner);

        tvMonth = view.findViewById(R.id.month);

        final Calendar calendar = Calendar.getInstance();
        int monthNum = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        tvMonth.setText(month[monthNum] + ", " + year);
        monthName = month[monthNum].toLowerCase();

        mCursor = getAllDrugs(monthName);
        mDrugAdapter = new DrugRecyclerView(getActivity(), mCursor, monthName);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mDrugAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mCursor = getAllDrugs(month[position].toLowerCase());
                mDrugAdapter = new DrugRecyclerView(getActivity(), mCursor, month[position].toLowerCase());
                recyclerViewSpinner.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewSpinner.setAdapter(mDrugAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private Cursor getAllDrugs(String monthName) {
        return mDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + MONTH + " = ?", new String[]{monthName});

    }
    private boolean removeQuest(int id, String monthName){
        return mDB.delete(monthName, _ID+"="+id, null)>0;
    }
}
