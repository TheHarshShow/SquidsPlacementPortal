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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShowJobToCompany extends AppCompatActivity {

    private TextView companyNameTextView;
    private TextView jobTitleTextView;
    private TextView jobDescriptionTextView;
    private TextView approvalStatusTextView;
    private Button brochureButton;
    private Button deleteButton;


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job_to_company);


        db = FirebaseFirestore.getInstance();

        companyNameTextView = findViewById(R.id.company_name_text_view);
        jobTitleTextView = findViewById(R.id.job_title_text_view);
        jobDescriptionTextView = findViewById(R.id.job_description_text_view);
        approvalStatusTextView = findViewById(R.id.approval_status_text_view);
        brochureButton = findViewById(R.id.download_brochure_button);
        deleteButton = findViewById(R.id.delete_button);

        final JobPosting job = (JobPosting) getIntent().getSerializableExtra("job to show");

        companyNameTextView.setText(job.companyName);
        jobTitleTextView.setText(job.jobTitle);
        jobDescriptionTextView.setText(job.jobDescripion);
        approvalStatusTextView.setText(job.approvalStatus);



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("posts").document(job.email+"-"+job.timestamp.toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(ShowJobToCompany.this, "Successfully deleted post", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {

                            Toast.makeText(ShowJobToCompany.this, "Couldn't delete post "+task.getException(), Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(ShowJobToCompany.this, "No Brochure Available", Toast.LENGTH_SHORT).show();


                } else {

                    if (ActivityCompat.checkSelfPermission(ShowJobToCompany.this, Manifest.permission. WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //Ask for permission
                        ActivityCompat.requestPermissions(ShowJobToCompany.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 002);
                    }else {


                        Toast.makeText(ShowJobToCompany.this, "Downloading Brochure...", Toast.LENGTH_SHORT).show();

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
