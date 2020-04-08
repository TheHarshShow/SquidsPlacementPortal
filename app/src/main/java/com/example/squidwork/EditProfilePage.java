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

public class EditProfilePage extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    private FirebaseFirestore db;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private ImageButton profile_button;
    private TextView name;
    private EditText roll;
    private EditText cpi;
    private EditText phone;
    private String BranchSelected;
    private Button saveButton;
    private final String TAG = "TAG";
    private Spinner branch;
    private Uri resultUri = null;
    private static final int GALLERY_REQUEST = 182;
    private ProgressDialog mProgress;
    String [] branches = {"Select..." , "CSE" , "ME" , "EP", "EEE" , "ECE" , "MnC"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);
        System.out.println("EditPage............");
        name = (TextView)findViewById(R.id.name_text);

        roll = (EditText)findViewById(R.id.ROLLNUMBER_ET);
        cpi = (EditText)findViewById(R.id.CPI_ET);
        phone = (EditText)findViewById(R.id.PHONE_ET);
        saveButton = (Button)findViewById(R.id.csave_profile_button);
        profile_button = (ImageButton)findViewById(R.id.cupdate_image_button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("profile_images");

        name.setText(mAuth.getCurrentUser().getDisplayName().toString());
        branch = (Spinner)findViewById(R.id.BRANCH_ET);
        branch.setOnItemSelectedListener(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, branches);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter);


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

                                    String rn = roll.getText().toString();
                                    String ph = phone.getText().toString();
                                    String cp = cpi.getText().toString();
                                    String nm = mAuth.getCurrentUser().getDisplayName();

                                    System.out.println(rn);
                                    System.out.println(rn);
                                    System.out.println(rn);
                                    System.out.println(cp);
                                    System.out.println(BranchSelected);
                                    System.out.println("BRACCHFWFWRVWV4444444444444444");
                                    if(BranchSelected!="Select..." &&!TextUtils.isEmpty(rn)  && !TextUtils.isEmpty(ph)  && !TextUtils.isEmpty(cp) && !TextUtils.isEmpty(nm) ){
                                        //docdata.put("Branch",br);
                                        docdata.put("Roll Number",rn);
                                        docdata.put("Phone",ph);
                                        docdata.put("CPI",cp);
                                        docdata.put("Name",nm);
                                        docdata.put("Branch", BranchSelected);
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
                                                                Toast.makeText(EditProfilePage.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(EditProfilePage.this,MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                                    //Toast.makeText(EditProfilePage.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                                                    //finish();

                                                }else{
                                                    System.out.println("RRRRRR8888888888888888888RRRRRRRRRRRRRRR");
                                                    Toast.makeText(EditProfilePage.this,"Profile Updated Successfully But Try Once Again",Toast.LENGTH_LONG).show();
                                                    //finish();
                                                }
                                            }

                                        });

                                    }else{
                                    //fields empty
                                        Toast.makeText(EditProfilePage.this,"FILL ALL THE FIELDS",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }else {

                            String br = docdata.get("Branch").toString();
                            String em = docdata.get("email").toString();
                            String rn = docdata.get("Roll Number").toString();
                            String ph = docdata.get("Phone").toString();
                            String cp = docdata.get("CPI").toString();
                            String nm = docdata.get("Name").toString();
                            String img = docdata.get("ImageUrl").toString();
                            if(!img.equals("None")){
                                Picasso.with(EditProfilePage.this).load(img).fit().into(profile_button);
                            }
                            //branch.setText(br);
                            cpi.setText(cp);
                            roll.setText(rn);
                            phone.setText(ph);
                            name.setText(mAuth.getCurrentUser().getDisplayName().toString());
                            int spinnerPosition = adapter.getPosition(br);

//set the default according to value
                            branch.setSelection(spinnerPosition);
                            System.out.println("BRACCHFWFWRVWV");
                            System.out.println(BranchSelected);
                            saveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                   // String br = branch.getText().toString();

                                    String rn = roll.getText().toString();
                                    String ph = phone.getText().toString();
                                    String cp = cpi.getText().toString();
                                    String nm = name.getText().toString();
                                    if( rn!=null && ph!=null && cp!=null && nm!=null && BranchSelected!="Select..."){

                                        //docdata.put("Branch",br);
                                        docdata.put("Roll Number",rn);
                                        docdata.put("Phone",ph);
                                        docdata.put("CPI",cp);
                                        docdata.put("Name",nm);
                                        docdata.put("Branch", BranchSelected);
                                        System.out.println("BRACCHFWFWRVWV111111111111111111111111");
                                        System.out.println(BranchSelected);
                                        System.out.println("YHA..............");
                                        userRef.set(docdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @RequiresApi( api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    System.out.println("YHA..............44");
                                                    Toast.makeText(EditProfilePage.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(EditProfilePage.this,StudentProfile.class);
                                                    startActivity(intent);
                                                }else{
                                                    System.out.println("YHA.............777");
                                                }


                                            }

                                        });

                                    }else{
                                        //fields empty
                                        Toast.makeText(EditProfilePage.this,"FILL ALL THE FIELDS",Toast.LENGTH_LONG).show();
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
                Picasso.with(EditProfilePage.this).load(resultUri).fit().into(profile_button);
                System.out.println("NAA>>>>>>>>>>>>>>>>>");

                mStorage.child(mAuth.getCurrentUser().getEmail()).putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(EditProfilePage.this," Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "Selected User: "+ branches[position] ,Toast.LENGTH_SHORT).show();
        BranchSelected = branches[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
                //Later Work
    }


}




















