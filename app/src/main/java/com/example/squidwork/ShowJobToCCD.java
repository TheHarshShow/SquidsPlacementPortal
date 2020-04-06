package com.example.squidwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class ShowJobToCCD extends AppCompatActivity {

    private JobPostingCCD job;
    private TextView companyNameTextView;
    private TextView jobTitleTextView;
    private TextView jobDescriptionTextView;
    private TextView companyEmailTextView;
    private Button brochureButton;
    private Button acceptButton;

    private Button rejectButton;

    FirebaseFirestore db;

    private String TAG = "show_job_to_ccd";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job_to_c_c_d);

        db = FirebaseFirestore.getInstance();

        job = (JobPostingCCD) getIntent().getSerializableExtra("job to show");

        companyNameTextView = findViewById(R.id.company_name_text_view);
        jobTitleTextView = findViewById(R.id.job_title_text_view);
        companyEmailTextView = findViewById(R.id.company_email_text_view);
        jobDescriptionTextView = findViewById(R.id.job_description_text_view);
        acceptButton = findViewById(R.id.accept_button);
        rejectButton = findViewById(R.id.delete_button);

        brochureButton = findViewById(R.id.download_brochure_button);

        companyNameTextView.setText(job.companyName);
        jobTitleTextView.setText(job.jobTitle);
        jobDescriptionTextView.setText(job.jobDescripion);
        companyEmailTextView.setText(job.email);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("posts").document(job.email+"-"+job.timestamp.toString()).update("approvalStatus", "Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(ShowJobToCCD.this, "Successfully accepted job posting", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {

                            Toast.makeText(ShowJobToCCD.this, "Could Not Accept "+task.getException(), Toast.LENGTH_SHORT).show();
                            finish();


                        }

                    }
                });

            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("posts").document(job.email+"-"+job.timestamp.toString()).update("approvalStatus", "Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(ShowJobToCCD.this, "Successfully rejected job posting", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {

                            Toast.makeText(ShowJobToCCD.this, "Could Not Reject", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }
                });

            }
        });


        brochureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(job.url.equals("blank")){

                    Toast.makeText(ShowJobToCCD.this, "No Brochure Available", Toast.LENGTH_SHORT).show();


                } else {

                    if (ActivityCompat.checkSelfPermission(ShowJobToCCD.this, Manifest.permission. WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //Ask for permission
                        ActivityCompat.requestPermissions(ShowJobToCCD.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 002);
                    }else {

                        Toast.makeText(ShowJobToCCD.this, "Downloading Brochure...", Toast.LENGTH_SHORT).show();


                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(job.url);

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle(job.email + job.timestamp.toString() + ".pdf");
                        request.setMimeType("application/pdf");
                        request.setDescription("Brochure");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, job.email + job.timestamp.toString() + ".pdf");
                        downloadmanager.enqueue(request);
                    }
                }

            }
        });

    }
}
