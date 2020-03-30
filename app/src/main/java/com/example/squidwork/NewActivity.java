package com.example.squidwork;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewActivity extends AppCompatActivity {

    private static final String TAG = "NewActivity";
    private TextView mcompany_name,mjob_title,mjob_description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_job_show);
        Log.d(TAG, "onCreate: created");
        mcompany_name=findViewById(R.id.company_name);
        mjob_title=findViewById(R.id.job_title);
        mjob_description=findViewById(R.id.job_description);

        if(getIntent().hasExtra("selected job")) {
            JobPostingStudent job = getIntent().getParcelableExtra("selected job");
            Log.d(TAG, "onCreate: " + job.toString());
            mcompany_name.setText(job.companyName);
            mjob_title.setText(job.jobTitle);
            mjob_description.setText(job.jobDescripion);
        }
    }
}
