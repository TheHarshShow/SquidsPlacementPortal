package com.example.squidwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ShowApplicationActivity extends AppCompatActivity {


    private String TAG = "show_application_page";
    private TextView studentNameTextView;
    private TextView studentEmailTextView;
    private TextView companyNameTextView;
    private TextView companyEmailTextView;
    private TextView jobTitleTextView;
    private TextView bioTextView;
    private TextView skillsTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_application);

        StudentApplicationCCD sac = (StudentApplicationCCD) getIntent().getSerializableExtra("selected job");

        studentNameTextView = (TextView) findViewById(R.id.student_name_text_view);
        studentEmailTextView = (TextView) findViewById(R.id.student_email_text_view);
        companyNameTextView = (TextView) findViewById(R.id.company_name_text_view);
        companyEmailTextView = (TextView) findViewById(R.id.company_email_text_view);
        jobTitleTextView = (TextView)  findViewById(R.id.job_title_text_view);
        bioTextView = (TextView) findViewById(R.id.bio_text_view);
        skillsTextView = (TextView) findViewById(R.id.skills_text_view);


        studentNameTextView.setText(sac.studentName);
        studentEmailTextView.setText(sac.studentEmail);
        bioTextView.setText(sac.studentBio);
        companyEmailTextView.setText(sac.companyEmail);
        companyNameTextView.setText(sac.companyName);
        skillsTextView.setText(sac.skills);
        jobTitleTextView.setText(sac.jobTitle);

        Log.d(TAG, sac.studentName);

    }
}
