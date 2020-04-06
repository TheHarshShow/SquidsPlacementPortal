package com.example.squidwork;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ApplyForm extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_form);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Button applyButton = (Button) findViewById(R.id.approve_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameTextView = (EditText) findViewById(R.id.name_text);
                EditText skillsTextView = (EditText) findViewById(R.id.skills_text);
                EditText bioTextView = (EditText) findViewById(R.id.bio_text);

                if (nameTextView.getText().toString().equals("")){

                    Toast.makeText(ApplyForm.this, "Fill Name Field", Toast.LENGTH_SHORT).show();

                } else if(skillsTextView.getText().toString().equals("")){

                    Toast.makeText(ApplyForm.this, "Fill Skills Title Field", Toast.LENGTH_SHORT).show();
                } else {
                    JobPostingStudent job = getIntent().getParcelableExtra("selected job");

                    final CollectionReference applRef = db.collection("application");
                    Map applDesc = new HashMap();

                    final Long tsLong = System.currentTimeMillis();
                    applDesc.put("approvalStatus" ,"Waiting");
                    applDesc.put("Name", nameTextView.getText().toString());
                    applDesc.put("skills", skillsTextView.getText().toString());
                    applDesc.put("bio", bioTextView.getText().toString());
                    applDesc.put("timeStamp", tsLong);
                    applDesc.put("candidateEmail", currentUser.getEmail());
                    applDesc.put("companyName",job.companyName);
                    applDesc.put("companyEmail",job.email);
                    applDesc.put("jobTitle",job.jobTitle);



                    applRef.document(job.email+"-"+job.timestamp+"-"+currentUser.getEmail()+"-"+tsLong).set(applDesc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ApplyForm.this, "Added Application Successfully.", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {

                                Toast.makeText(ApplyForm.this, "Could not add application due to some error.", Toast.LENGTH_SHORT).show();

                            }


                        }
                    });




                }

            }
        });

    }


}

