package com.example.squidwork;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ShowApplicationActivity extends AppCompatActivity {


    private String TAG = "show_application_page";
    private TextView studentNameTextView;
    private TextView studentEmailTextView;
    private TextView companyNameTextView;
    private TextView companyEmailTextView;
    private TextView jobTitleTextView;
    private TextView bioTextView;
    private TextView skillsTextView;
    private Button acceptButton;
    private Button rejectButton;


    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_application);

        final StudentApplicationCCD sac = (StudentApplicationCCD) getIntent().getSerializableExtra("selected job");

        db = FirebaseFirestore.getInstance();


        studentNameTextView = (TextView) findViewById(R.id.student_name_text_view);
        studentEmailTextView = (TextView) findViewById(R.id.student_email_text_view);
        companyNameTextView = (TextView) findViewById(R.id.company_name_text_view);
        companyEmailTextView = (TextView) findViewById(R.id.company_email_text_view);
        jobTitleTextView = (TextView)  findViewById(R.id.job_title_text_view);
        bioTextView = (TextView) findViewById(R.id.bio_text_view);
        skillsTextView = (TextView) findViewById(R.id.skills_text_view);

        acceptButton = (Button) findViewById(R.id.accept_button);
        rejectButton = (Button) findViewById(R.id.reject_button);

        studentNameTextView.setText(sac.studentName);
        studentEmailTextView.setText(sac.studentEmail);
        bioTextView.setText(sac.studentBio);
        companyEmailTextView.setText(sac.companyEmail);
        companyNameTextView.setText(sac.companyName);
        skillsTextView.setText(sac.skills);
        jobTitleTextView.setText(sac.jobTitle);


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Accept Button Clicked "+sac.id);

                db.collection("application").document(sac.id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (task.isSuccessful()){

                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){

                                Map docData = new HashMap();
                                docData = documentSnapshot.getData();


                                docData.replace("approvalStatus", "Approved By CCD");

                                Log.d(TAG, "status replaced");

                                db.collection("application").document(sac.id).set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Toast.makeText(ShowApplicationActivity.this, "Successfully accepted application", Toast.LENGTH_SHORT).show();


                                            finish();

                                        } else {


                                            Toast.makeText(ShowApplicationActivity.this, "Application acceptance unsuccessful", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                });





                            } else {

                                Log.d(TAG, "application does not exist");

                            }

                        } else{

                            Log.d(TAG, "could not fetch document to approve");

                        }
                    }
                });

            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Reject Button Clicked");


                db.collection("application").document(sac.id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Log.d(TAG, "Application delete successful");
                            Toast.makeText(ShowApplicationActivity.this, "Successfully accepted application", Toast.LENGTH_SHORT).show();


                            finish();


                        } else {

                            Log.d(TAG, "Application delete failed");
                            Toast.makeText(ShowApplicationActivity.this, "Application Rejection Failed!", Toast.LENGTH_SHORT).show();


                        }

                    }
                });

            }
        });


    }
}
