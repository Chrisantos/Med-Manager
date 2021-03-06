package com.eyzindskye.med_manager.Utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eyzindskye.med_manager.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class EditEmail extends AppCompatActivity {
    Toolbar mToolbar;
    private EditText edEmail;
    private Button mSave;

    UserSharedPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userPreference = new UserSharedPreference(this);

        edEmail = findViewById(R.id.ed_email);
        mSave = findViewById(R.id.save);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                if (!TextUtils.isEmpty(email)){
                    userPreference.setEmail(email);
                    StyleableToast.makeText(getApplicationContext(), "Changes saved",
                            R.style.success).show();
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                    finish();
                }else{
                    edEmail.setError("Field is empty");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
