package com.example.squidwork;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ShowJobActivity extends AppCompatActivity {

    private static final String TAG = "NewActivity";
    private TextView mcompany_name,mjob_title,mjob_description;
    private Button downloadBrochureButton;
    private String brochureURL;
    private String companyEmail;
    private Long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_job_show);
        Log.d(TAG, "onCreate: created");
        mcompany_name=findViewById(R.id.company_name_text_view);
        mjob_title=findViewById(R.id.job_title_text_view);
        mjob_description=findViewById(R.id.job_description_text_view);


        downloadBrochureButton = findViewById(R.id.download_brochure_button);

        downloadBrochureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!brochureURL.equals("blank")){

                    Toast.makeText(ShowJobActivity.this, "Downloading Brochure...", Toast.LENGTH_SHORT).show();



                    if (ActivityCompat.checkSelfPermission(ShowJobActivity.this, Manifest.permission. WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //Ask for permission
                        ActivityCompat.requestPermissions(ShowJobActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 002);
                    }else{

                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(brochureURL);

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle(companyEmail+timestamp.toString()+".pdf");
                        request.setMimeType("application/pdf");
                        request.setDescription("Brochure");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,companyEmail+timestamp.toString()+".pdf");
                        downloadmanager.enqueue(request);


                    }


                } else {

                    Toast.makeText(ShowJobActivity.this, "No brochure available", Toast.LENGTH_SHORT).show();

                }




            }
        });

        if(getIntent().hasExtra("selected job")) {
            JobPostingStudent job = (JobPostingStudent) getIntent().getSerializableExtra("selected job");
            Log.d(TAG, "onCreate: " + job.toString());
            mcompany_name.setText(job.companyName);
            mjob_title.setText(job.jobTitle);
            mjob_description.setText(job.jobDescripion);
            brochureURL = job.url;
            companyEmail = job.email;
            timestamp = job.timestamp;
        }
        Button applyButton = (Button) findViewById(R.id.approve_button);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: Apply Button Clicked");
                JobPostingStudent job = (JobPostingStudent) getIntent().getSerializableExtra("selected job");
                Intent intent = new Intent(ShowJobActivity.this,ApplyForm.class);
                intent.putExtra("selected job", job);
                startActivity(intent);
                //startActivity(new Intent(getActivity(), AddPostingFormPage.class));

            }
        });
    }
}
