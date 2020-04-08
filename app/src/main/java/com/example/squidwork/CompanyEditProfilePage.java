package com.example.squidwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;

public class CompanyEditProfilePage extends AppCompatActivity   {

    private FirebaseFirestore db;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private ImageButton profile_button;
    private EditText name;

    private EditText phone;

    private Button saveButton;

    private Uri resultUri = null;
    private static final int GALLERY_REQUEST = 182;
    private ProgressDialog mProgress;
    String [] branches = {"Select..." , "CSE" , "ME" , "EP", "EEE" , "ECE" , "MnC"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_edit_profile_page);
        System.out.println("EditPage............");
        name = (EditText)findViewById(R.id.CNAME_ET);

        phone = (EditText)findViewById(R.id.CPHONE_ET);
        saveButton = (Button)findViewById(R.id.csave_profile_button);
        profile_button = (ImageButton)findViewById(R.id.cupdate_image_button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("profile_images");

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);

            }
        });




        final  DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getEmail().toString());
        System.out.println("EditPage............");
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                System.out.println("EditPage.......444444444");
                if(task.isSuccessful()){
                    System.out.println("EditPage...8888888888");
                    DocumentSnapshot document = task.getResult();
                    System.out.println(document);
                    if(document.exists()){

                        final Map<String, Object> docdata = document.getData();
                        System.out.println(docdata);
                        String isupdated = docdata.get("profile").toString();


                        if(isupdated.equals("NotUpdated")){
                            System.out.println("EditPage.....notupdated");

                            saveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //String br = branch.getText().toString();


                                    String ph = phone.getText().toString();

                                    String nm = name.getText().toString();


                                    System.out.println("BRACCHFWFWRVWV4444444444444444");
                                    if(!TextUtils.isEmpty(ph)   && !TextUtils.isEmpty(nm) ){
                                        //docdata.put("Branch",br);

                                        docdata.put("Phone",ph);

                                        docdata.put("Name",nm);

                                        System.out.println(docdata);
                                        userRef.set(docdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @RequiresApi( api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
                                                if(task.isSuccessful()){
                                                    System.out.println("RR9999999999999999999RRRRRRRRRRR");
                                                    docdata.put("profile","Updated");
                                                    userRef.set(docdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(CompanyEditProfilePage.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(CompanyEditProfilePage.this,MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                                    //Toast.makeText(CompanyEditProfilePage.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                                                    //finish();

                                                }else{
                                                    System.out.println("RRRRRR8888888888888888888RRRRRRRRRRRRRRR");
                                                    Toast.makeText(CompanyEditProfilePage.this,"Profile Updated Successfully But Try Once Again",Toast.LENGTH_LONG).show();
                                                    //finish();
                                                }
                                            }

                                        });

                                    }else{
                                        //fields empty
                                        Toast.makeText(CompanyEditProfilePage.this,"FILL ALL THE FIELDS",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }else {


                            String em = docdata.get("email").toString();

                            String ph = docdata.get("Phone").toString();

                            String nm = docdata.get("Name").toString();
                            String img = docdata.get("ImageUrl").toString();
                            if(!img.equals("None")){
                                Picasso.with(CompanyEditProfilePage.this).load(img).fit().into(profile_button);
                            }
                            //branch.setText(br);

                            phone.setText(ph);
                            name.setText(nm);

                            saveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // String br = branch.getText().toString();


                                    String ph = phone.getText().toString();

                                    String nm = name.getText().toString();
                                    if(  ph!=null&& nm!=null){

                                        //docdata.put("Branch",br);

                                        docdata.put("Phone",ph);

                                        docdata.put("Name",nm);


                                        userRef.set(docdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @RequiresApi( api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    System.out.println("YHA..............44");
                                                    Toast.makeText(CompanyEditProfilePage.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(CompanyEditProfilePage.this,CompanyProfile.class);
                                                    startActivity(intent);
                                                }else{
                                                    System.out.println("YHA.............777");
                                                }


                                            }

                                        });

                                    }else{
                                        //fields empty
                                        Toast.makeText(CompanyEditProfilePage.this,"FILL ALL THE FIELDS",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });







                        }
                        //db.collection("users").document(currentUser.getEmail()).set(docData);
                        //finish();

                    }

                }
            }
        });








    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){


            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //profile_button.setImageURI(resultUri);
                Picasso.with(CompanyEditProfilePage.this).load(resultUri).fit().into(profile_button);
                System.out.println("NAA>>>>>>>>>>>>>>>>>");

                mStorage.child(mAuth.getCurrentUser().getEmail()).putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CompanyEditProfilePage.this," Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
                        System.out.println("NAHI HUA>>>>>>>><<<<<<<<<<<<<<<>>>>>>>>>");
                        StorageReference downloaduriref = mStorage.child(mAuth.getCurrentUser().getEmail());
                        Task<Uri> downloaduritask = downloaduriref.getDownloadUrl();
                        while (!downloaduritask.isSuccessful());
                        Uri downloaduri = downloaduritask.getResult();
                        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
                        userRef.update("ImageUrl",downloaduri.toString());
                        //mProgress.dismiss();
                        //profile_button.setImageURI();

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            //mProgress.setMessage("Uploading...");
            //mProgress.show();


        }
    }

}




















