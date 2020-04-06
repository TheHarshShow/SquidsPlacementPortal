package com.example.squidwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class StudentShowApplicationActivity extends AppCompatActivity {


    private String TAG = "student_show_application_page";
    private TextView studentNameTextView;
    private TextView studentEmailTextView;
    private TextView companyNameTextView;
    private TextView companyEmailTextView;
    private TextView jobTitleTextView;
    private TextView bioTextView;
    private TextView skillsTextView;
    private TextView statusTextView;

    private Button cvButton;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_show_application);


        final ApplicationToLoad sac = (ApplicationToLoad) getIntent().getSerializableExtra("selected job");


        studentNameTextView = (TextView) findViewById(R.id.student_name_text_view);
        studentEmailTextView = (TextView) findViewById(R.id.student_email_text_view);
        companyNameTextView = (TextView) findViewById(R.id.company_name_text_view);
        companyEmailTextView = (TextView) findViewById(R.id.company_email_text_view);
        jobTitleTextView = (TextView)  findViewById(R.id.job_title_text_view);
        bioTextView = (TextView) findViewById(R.id.bio_text_view);
        skillsTextView = (TextView) findViewById(R.id.skills_text_view);
        statusTextView = (TextView) findViewById(R.id.status_text_view);
        cvButton = findViewById(R.id.cv_button);



        studentNameTextView.setText(sac.studentName);
        studentEmailTextView.setText(sac.studentEmail);
        bioTextView.setText(sac.studentBio);
        companyEmailTextView.setText(sac.companyEmail);
        companyNameTextView.setText(sac.companyName);
        skillsTextView.setText(sac.skills);
        jobTitleTextView.setText(sac.jobTitle);
        statusTextView.setText(sac.approvalStatus);

        url = sac.url;


        cvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!url.equals("blank")){

                    Toast.makeText(StudentShowApplicationActivity.this, "Downloading Brochure...", Toast.LENGTH_SHORT).show();



                    if (ActivityCompat.checkSelfPermission(StudentShowApplicationActivity.this, Manifest.permission. WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //Ask for permission
                        ActivityCompat.requestPermissions(StudentShowApplicationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 002);
                    }else{

                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(url);

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle(sac.id+".pdf");
                        request.setMimeType("application/pdf");
                        request.setDescription("CV");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,sac.id+".pdf");
                        downloadmanager.enqueue(request);


                    }


                } else {

                    Toast.makeText(StudentShowApplicationActivity.this, "No CV available", Toast.LENGTH_SHORT).show();

                }

            }
        });



    }
}
