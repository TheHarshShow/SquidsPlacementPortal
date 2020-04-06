package com.example.squidwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPostingFormPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private Button brochureButton;
    private int PICK_FILE_REQUEST = 001;
    Intent myFileIntent;
    private TextView pathTextView;
    private String TAG = "addPostingPage";
    private Uri file;
    FirebaseStorage storage;

    private CheckBox csebox;
    private CheckBox mncbox;
    private CheckBox eeebox;
    private CheckBox ecebox;
    private CheckBox epbox;
    private CheckBox mebox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_posting_form_page);

        csebox = findViewById(R.id.cse_box);
        mncbox = findViewById(R.id.mnc_box);
        eeebox = findViewById(R.id.eee_box);
        ecebox = findViewById(R.id.ece_box);
        epbox = findViewById(R.id.ep_box);
        mebox = findViewById(R.id.me_box);

        db.collection("posts").where

        storage = FirebaseStorage.getInstance();

        brochureButton = (Button) findViewById(R.id.brochure_button);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        pathTextView = findViewById(R.id.path_text_view);

        brochureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);

                myFileIntent.setType("application/pdf");

                //starts new activity to select file and return data
                startActivityForResult(Intent.createChooser(myFileIntent,"Choose File to Upload.."),PICK_FILE_REQUEST);
            }
        });

        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameTextView = (EditText) findViewById(R.id.name_text);
                EditText jobTextView = (EditText) findViewById(R.id.job_text);
                EditText descriptionTextView = (EditText) findViewById(R.id.description_text);

                if (nameTextView.getText().toString().equals("")){

                    Toast.makeText(AddPostingFormPage.this, "Fill Company Name Field", Toast.LENGTH_SHORT).show();

                } else if(jobTextView.getText().toString().equals("")){

                    Toast.makeText(AddPostingFormPage.this, "Fill Job Title Field", Toast.LENGTH_SHORT).show();
                } else {

                    final CollectionReference postsRef = db.collection("posts");
                    Map postDesc = new HashMap();

                    final int[] x = {0};
                    final int[] y={0};

                    final Long tsLong = System.currentTimeMillis();
                    postDesc.put("approvalStatus" ,"Waiting");
                    postDesc.put("companyName", nameTextView.getText().toString());
                    postDesc.put("jobTitle", jobTextView.getText().toString());
                    postDesc.put("jobDescription", descriptionTextView.getText().toString());
                    postDesc.put("timeStamp", tsLong);
                    postDesc.put("companyEmail", currentUser.getEmail());
                    postDesc.put("brochureURL", "blank");

                    ArrayList<String> checked = new ArrayList<String>();

                    if(csebox.isChecked()){

                        checked.add("CSE");

                    }
                    if(mncbox.isChecked()){

                        checked.add("MnC");

                    }
                    if(eeebox.isChecked()){

                        checked.add("EEE");

                    }
                    if(ecebox.isChecked()){

                        checked.add("ECE");

                    }
                    if(epbox.isChecked()){

                        checked.add("EP");

                    }
                    if(mebox.isChecked()){

                        checked.add("ME");

                    }

                    postDesc.put("branches", checked);


                    postsRef.document(currentUser.getEmail()+"-"+tsLong).set(postDesc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(AddPostingFormPage.this, "Added Post Successfully.", Toast.LENGTH_SHORT).show();

                                x[0] =1;

                                if(x[0]==1 && y[0]==1){

                                    finish();

                                }

                            } else {

                                Toast.makeText(AddPostingFormPage.this, "Could not add post due to some error.", Toast.LENGTH_SHORT).show();



                            }


                        }
                    });

                    if(file != null){

                        final ProgressDialog progressDialog = new ProgressDialog(AddPostingFormPage.this);
                        progressDialog.setTitle("Uploading File...");
                        progressDialog.show();

                        final StorageReference storageRef = storage.getReference();
                        UploadTask uploadTask = storageRef.child("brochures/"+currentUser.getEmail()+"-"+tsLong).putFile(file);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                progressDialog.dismiss();
                                Toast.makeText(AddPostingFormPage.this, "Brochure upload succeeded", Toast.LENGTH_SHORT).show();





                                storageRef.child("brochures/"+currentUser.getEmail()+"-"+tsLong).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        db.collection("posts").document(currentUser.getEmail()+"-"+tsLong).update("brochureURL", uri.toString());

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
                                Toast.makeText(AddPostingFormPage.this, "Brochure upload failed", Toast.LENGTH_SHORT).show();
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

                    ContentResolver contentResolver = getContentResolver();

                    pathTextView.setText(path);

                }

                break;
            }



        }

    }
}


