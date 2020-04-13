package com.example.squidwork;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ApplyForm extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private Button cvButton;
    private TextView pathTextView;

    Intent myFileIntent;
    private String TAG = "applyFormPage";
    private Uri file;
    FirebaseStorage storage;

    private int PICK_FILE_REQUEST = 001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_form);

        storage = FirebaseStorage.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        pathTextView = findViewById(R.id.path_text_view);
        cvButton = findViewById(R.id.cv_button);



        cvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);

                myFileIntent.setType("application/pdf");

                //starts new activity to select file and return data
                startActivityForResult(Intent.createChooser(myFileIntent,"Choose File to Upload.."),PICK_FILE_REQUEST);
            }
        });

        Button applyButton = (Button) findViewById(R.id.approve_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText nameTextView = (EditText) findViewById(R.id.name_text);
                EditText skillsTextView = (EditText) findViewById(R.id.skills_text);
                EditText bioTextView = (EditText) findViewById(R.id.bio_text);

                if(skillsTextView.getText().toString().equals("")){

                    Toast.makeText(ApplyForm.this, "Fill Skills Title Field", Toast.LENGTH_SHORT).show();

                } else {



                    final int[] x = {0};
                    final int[] y={0};

                    final JobPostingStudent job = (JobPostingStudent) getIntent().getSerializableExtra("selected job");

                    final CollectionReference applRef = db.collection("application");
                    Map applDesc = new HashMap();

                    final Long tsLong = System.currentTimeMillis();
                    applDesc.put("approvalStatus" ,"Waiting");
                    applDesc.put("Name", mAuth.getCurrentUser().getDisplayName().toString());
                    applDesc.put("skills", skillsTextView.getText().toString());
                    applDesc.put("bio", bioTextView.getText().toString());
                    applDesc.put("timeStamp", tsLong);
                    applDesc.put("candidateEmail", currentUser.getEmail());
                    applDesc.put("companyName",job.companyName);
                    applDesc.put("companyEmail",job.email);
                    applDesc.put("jobTitle",job.jobTitle);
                    applDesc.put("cvURL","blank");

                    applRef.document(job.email+"-"+job.timestamp+"-"+currentUser.getEmail()+"-"+tsLong).set(applDesc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ApplyForm.this, "Added Application Successfully.", Toast.LENGTH_SHORT).show();
                                x[0]=1;

                                if(x[0]==1 && y[0]==1){

                                    finish();

                                }

                            } else {

                                Toast.makeText(ApplyForm.this, "Could not add application due to some error.", Toast.LENGTH_SHORT).show();



                            }


                        }
                    });

                    if(file != null){

                        final ProgressDialog progressDialog = new ProgressDialog(ApplyForm.this);
                        progressDialog.setTitle("Uploading File...");
                        progressDialog.show();

                        final StorageReference storageRef = storage.getReference();
                        UploadTask uploadTask = storageRef.child("cvs/"+job.email+"-"+job.timestamp+"-"+currentUser.getEmail()+"-"+tsLong).putFile(file);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                progressDialog.dismiss();
                                Toast.makeText(ApplyForm.this, "CV upload succeeded", Toast.LENGTH_SHORT).show();

                                storageRef.child("cvs/"+job.email+"-"+job.timestamp+"-"+currentUser.getEmail()+"-"+tsLong).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        db.collection("application").document(job.email+"-"+job.timestamp+"-"+currentUser.getEmail()+"-"+tsLong).update("cvURL", uri.toString());

                                        y[0]=1;

                                        if(x[0]==1 && y[0]==1){

                                            finish();

                                        }
//                                        Log.d(TAG, "URL: "+uri);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        y[0]=1;

                                        Toast.makeText(ApplyForm.this, "could not post CV", Toast.LENGTH_SHORT).show();


                                        if(x[0]==1 && y[0]==1){

                                            finish();

                                        }
                                    }
                                });




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ApplyForm.this, "Brochure upload failed", Toast.LENGTH_SHORT).show();
                                y[0]=1;

                                if(x[0]==1 && y[0]==1){

                                    finish();

                                }

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                double progress = (100.0)*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage(((int)progress) + "% Uploaded");

                            }
                        });


                    } else {

                        y[0]=1;

                    }




                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case 001:{
                if(resultCode == RESULT_OK){

                    Log.d(TAG, "OK");

                    String path = data.getData().getPath();

                    Log.d(TAG, "OK2");

                    assert path != null;
                    file = data.getData();

                    Log.d(TAG, "OK3"+file.toString());


                    pathTextView.setText(path);

                }

                break;
            }



        }

    }


}

