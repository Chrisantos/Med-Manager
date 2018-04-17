package com.eyzindskye.med_manager.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eyzindskye.med_manager.HelperClass.DBContract;
import com.eyzindskye.med_manager.HelperClass.DrugDB;
import com.eyzindskye.med_manager.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.DESC;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.DURATION;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.INTERVAL;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.MONTH;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.NAME;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.QUANTITY;
import static android.provider.BaseColumns._ID;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.START_TIME;
import static com.eyzindskye.med_manager.HelperClass.DrugDB.TABLE_NAME;

/**
 * Created by CHRISANTUS EZE on 4/4/2018.
 */

public class DrugRecyclerView extends RecyclerView.Adapter<DrugRecyclerView.DrugViewHolder> {
    private Context context;
    private Cursor cursor;
    private String month;
    private SQLiteDatabase mDb;
    private DrugDB drugDB;

    public DrugRecyclerView(Context context, Cursor cursor, String month){
        this.context = context;
        this.cursor = cursor;
        this.month = month;
        drugDB = new DrugDB(context);
        mDb = drugDB.getReadableDatabase();
    }

    @Override
    public DrugRecyclerView.DrugViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drug_layout, parent, false);
        return new DrugViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrugRecyclerView.DrugViewHolder holder, int position) {
//        if (!cursor.moveToPosition(position))return;

        int idIndex = cursor.getColumnIndex(DBContract.DBEntry._ID);
        int nameIndex = cursor.getColumnIndex(NAME);
        int timeIndex = cursor.getColumnIndex(START_TIME);
        int intervalIndex = cursor.getColumnIndex(INTERVAL);
        int dosageIndex = cursor.getColumnIndex(QUANTITY);
        int durationIndex = cursor.getColumnIndex(DURATION);

        cursor.moveToPosition(position);

        final int id = cursor.getInt(idIndex);
        String name = cursor.getString(nameIndex);
        String time = cursor.getString(timeIndex);
        String interval = cursor.getString(intervalIndex);
        String dosage = cursor.getString(dosageIndex);
        String duration = cursor.getString(durationIndex);


        String letter = name.substring(0,1);
        String lettr = letter.toUpperCase();


        holder.itemView.setTag(id);
        holder.tvName.setText(name);
        holder.tvStartTime.setText(formatDate(time));
        holder.tvInterval.setText(interval);
        holder.tvDosage.setText(dosage + " pill(s)");
        holder.tvDuration.setText(duration + " day(s)");
        holder.imgBtn.setBackground(getDrugColor(lettr));
    }

    private Drawable getDrugColor(String letter){
        Drawable drugColor;
        switch (letter){
            case "A": drugColor = ContextCompat.getDrawable(context, R.drawable.blue_drug);
                break;
            case "B": drugColor = ContextCompat.getDrawable(context, R.drawable.green_drug);
                break;
            case "C": drugColor = ContextCompat.getDrawable(context, R.drawable.blue_drug);
                break;
            case "D": drugColor = ContextCompat.getDrawable(context, R.drawable.green_drug);
                break;
            case "E": drugColor = ContextCompat.getDrawable(context, R.drawable.yellow_drug);
                break;
            case "F": drugColor = ContextCompat.getDrawable(context, R.drawable.yellow_drug);
                break;
            case "G": drugColor = ContextCompat.getDrawable(context, R.drawable.orange_drug);
                break;
            case "H": drugColor = ContextCompat.getDrawable(context, R.drawable.orange_drug);
                break;
            case "I": drugColor = ContextCompat.getDrawable(context, R.drawable.blue_drug);
                break;
            case "J": drugColor = ContextCompat.getDrawable(context, R.drawable.yellow_drug);
                break;
            case "K": drugColor = ContextCompat.getDrawable(context, R.drawable.green_drug);
                break;
            case "L": drugColor = ContextCompat.getDrawable(context, R.drawable.green_drug);
                break;
            case "M": drugColor = ContextCompat.getDrawable(context, R.drawable.orange_drug);
                break;
            case "N": drugColor = ContextCompat.getDrawable(context, R.drawable.green_drug);
                break;
            case "O": drugColor = ContextCompat.getDrawable(context, R.drawable.white_button);
                break;
            default: drugColor = ContextCompat.getDrawable(context, R.drawable.white_button);
                break;
        }
        return drugColor;
    }


    @Override
    public int getItemCount() {
        if (cursor==null){
            return  0;
        }
        return cursor.getCount();
    }

    public class DrugViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDuration, tvStartTime, tvInterval, tvDosage;
        private ImageButton imgBtn;
        public DrugViewHolder(final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvDuration = itemView.findViewById(R.id.end_date);
            tvStartTime = itemView.findViewById(R.id.start_time);
            tvInterval = itemView.findViewById(R.id.interval);
            tvDosage = itemView.findViewById(R.id.dosage);
            imgBtn = itemView.findViewById(R.id.drug_color);

            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int)itemView.getTag();
                    Cursor cursor = drugDB.fetchDrug(id, mDb);

                    getDialog(cursor, id);
                }
            });
        }
    }
    public void swapCursor(Cursor c){
        if (cursor == c){
            return;
        }
        this.cursor = c;

        if (c!=null){
            this.notifyDataSetChanged();
        }
    }

    private Cursor getAllGuests() {
        return mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + MONTH + " = ?", new String[]{month});
    }
    private boolean removeQuest(long id){
        return mDb.delete(TABLE_NAME, _ID+"="+id, null)>0;
    }


    private String formatDate(String dateStr){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d HH:mm");
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void getDialog(final Cursor cursor, final int id){

        if (cursor != null)
            cursor.moveToFirst();


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.dialog_desc_layout, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(context).setView(view);

        TextView tvTitle = view.findViewById(R.id.dialog_title);
        TextView tvDesc = view.findViewById(R.id.dialog_desc);
        TextView tvDosage = view.findViewById(R.id.dialog_dosage);
        ImageButton imageButton = view.findViewById(R.id.dialog_drug_color);

        String name = cursor.getString(cursor.getColumnIndex(NAME));
        String desc = cursor.getString(cursor.getColumnIndex(DESC));
        String dosage = cursor.getString(cursor.getColumnIndex(QUANTITY));
        String interval = cursor.getString(cursor.getColumnIndex(INTERVAL));
        
        String letter = name.substring(0,1);
        String lettr = letter.toUpperCase();

        imageButton.setBackground(getDrugColor(lettr));
        tvTitle.setText(name);
        tvDesc.setText(desc);

        String message = "Take "+ dosage + " - " + interval;

        tvDosage.setText(message);

        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeQuest(id);
                swapCursor(getAllGuests());

                StyleableToast.makeText(context, "Deleted!", R.style.success).show();
            }
        }).create().show();


        cursor.close();

    }

}
