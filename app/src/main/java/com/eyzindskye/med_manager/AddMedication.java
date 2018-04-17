package com.eyzindskye.med_manager;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eyzindskye.med_manager.AlarmClass.ReminderManager;
import com.eyzindskye.med_manager.HelperClass.DrugDB;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMedication extends Fragment {

    private Switch mSwitch;
    private Spinner mSpinner;
    private EditText edName, edDesc;
    private TextView tvQuantity, tvStartTime, tvDuration;
    private RelativeLayout relativeDosage, relativeTime;
    private Button btnSave, btnNext;
    private CardView mCard1, mCard2, mCard3;
    private String[] times = {"Once a day", "Twice a day", "3 times a day"};
    private String[] month = {"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

    private ArrayAdapter arrTimes;
    private DatePickerDialog datePickerDialog;
    private String days, startDate, quantity;
    private int timesSelected;
    private SQLiteDatabase mDB;
    private DrugDB mDatabase;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    private String startMonth, startTime;
    private int mMonth, mDay, hour, minute;
    private Calendar mCalendar;





    public AddMedication() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_medication, container, false);

        mDatabase = new DrugDB(getActivity());
        mDB = mDatabase.getWritableDatabase();

        initViews(view);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSwitch.isChecked()){
                    mSpinner.setVisibility(View.VISIBLE);
                    relativeDosage.setVisibility(View.VISIBLE);
                    relativeTime.setVisibility(View.VISIBLE);
                    mCard3.setVisibility(View.VISIBLE);

                }else{
                    mSpinner.setVisibility(View.GONE);
                    relativeDosage.setVisibility(View.GONE);
                    relativeTime.setVisibility(View.GONE);
                    mCard3.setVisibility(View.GONE);

                }
            }
        });

        mDatabase = new DrugDB(getActivity());

        arrTimes = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, times);
        mSpinner = view.findViewById(R.id.spinnner);
        arrTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrTimes);

        mCalendar = Calendar.getInstance();
        mMonth = mCalendar.get(Calendar.MONTH); // current month
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH); // current day

        hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        minute = mCalendar.get(Calendar.MINUTE);

        startMonth = month[mMonth].toLowerCase();

        tvStartTime.setText(hour + ":" + minute);
        startTime = hour + ":" + minute;

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timesSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvOnClicks();
        btnOnClicks();

        return view;
    }

    private void initViews(View view){
        edDesc = view.findViewById(R.id.ed_desc);
        edName = view.findViewById(R.id.ed_name);
        tvQuantity = view.findViewById(R.id.quantity);
        tvStartTime = view.findViewById(R.id.time);
        tvDuration = view.findViewById(R.id.duration);
        btnSave = view.findViewById(R.id.save);
        btnNext = view.findViewById(R.id.next);

        relativeDosage = view.findViewById(R.id.relative_dosage);
        relativeTime = view.findViewById(R.id.relative_time);

        mSwitch = view.findViewById(R.id.drag_switch);

        mCard1 = view.findViewById(R.id.card1);
        mCard2 = view.findViewById(R.id.card2);
        mCard3 = view.findViewById(R.id.card3);
    }

    private void btnOnClicks(){
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                if (!TextUtils.isEmpty(name)){
                    mCard1.setVisibility(View.VISIBLE);
                    mCard2.setVisibility(View.VISIBLE);

                    btnNext.setVisibility(View.GONE);
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                String desc = edDesc.getText().toString();
                if (isEditTextFilled(edDesc, "drug description needed") && isDaysEmpty(days)
                        && isQuantityEmpty(quantity) && isSwitchChecked(mSwitch)){
                    String interval = "";
                    switch (timesSelected){
                        case 0: interval = "Once a day"; break;
                        case 1: interval = "Twice a day"; break;
                        case 2: interval = "Thrice a day"; break;
                    }
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
                    String reminderDateTime = dateTimeFormat.format(mCalendar.getTime());

                    long id = mDatabase.addMed(name, desc, startMonth, reminderDateTime, days, interval, quantity, mDB);


                    new ReminderManager(getActivity()).setReminder(id, mCalendar);

                    getActivity().setTitle("Med Manager");
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, homeFragment).commit();
                }

            }
        });
    }

    private void tvOnClicks(){
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hour1, minute1;
                        if (hour < 10){
                            hour1 = "0" + selectedHour;
                        }else {
                            hour1 = "" + selectedHour;
                        }

                        if (minute < 10){
                            minute1 = "0" + selectedMinute;
                        }else{
                            minute1 = "" + selectedMinute;
                        }

                        tvStartTime.setText(hour1 + ":" + minute1);
//                        startTime = hour1 + ":" + minute1;
                        mCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mCalendar.set(Calendar.MINUTE, selectedMinute);
                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }

        });

        tvDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                final View view = layoutInflater.inflate(R.layout.dialog_layout, null);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setView(view);
                final EditText edDuration = view.findViewById(R.id.dialog_edittext);
                TextView tvTitle = view.findViewById(R.id.dialog_title);
                tvTitle.setText("Duration");
                alert.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        days = edDuration.getText().toString();
                        tvDuration.setText(days + " day(s)");

                    }
                }).setNegativeButton("Cancel", null).create().show();

            }
        });

        tvQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                final View view = layoutInflater.inflate(R.layout.dialog_layout, null);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setView(view);
                final EditText edDosage = view.findViewById(R.id.dialog_edittext);
                TextView tvTitle = view.findViewById(R.id.dialog_title);
                tvTitle.setText("Dosage");
                alert.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quantity = edDosage.getText().toString();
                        tvQuantity.setText("Take " + quantity);

                    }
                }).setNegativeButton("Cancel", null).create().show();
            }
        });
    }

    private boolean isEditTextFilled(EditText editText, String message) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty()) {
            editText.setError(message);
            hideKeyboardFrom(editText);
            return false;
        }
        return true;
    }
    private boolean isDaysEmpty(String days){
        if (days.equals(null)){
            StyleableToast.makeText(getActivity(), "Duration must be entered", R.style.error).show();
            return false;
        }
        return true;
    }

    private boolean isQuantityEmpty(String quantity){
        if (quantity.equals(null)){
            StyleableToast.makeText(getActivity(), "Dosage must be entered", R.style.error).show();
            return false;
        }
        return true;
    }

    private boolean isSwitchChecked(Switch mSwitch){
        if (!mSwitch.isChecked()){
            StyleableToast.makeText(getActivity(), "Reminder not added", R.style.error).show();
            return false;
        }
        return true;
    }

    private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}
