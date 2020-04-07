package com.example.squidwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class StudentProfile extends AppCompatActivity {

    private  TextView rollnumber;
    private TextView email;
    private  TextView cpi;
    private TextView branch;
    private TextView phone;
    private ImageButton Profile_Button;
    private TextView name;
    private Button edit_button;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final  String TAG = "Hello";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile);



        Profile_Button = (ImageButton) findViewById(R.id.profile_pic);
        edit_button = (Button) findViewById(R.id.edit_profile_button);
        name = (TextView) findViewById(R.id.nameTextView);

        branch = (TextView)findViewById(R.id.branch);
        rollnumber = (TextView)findViewById(R.id.roll_number);
        cpi = (TextView)findViewById(R.id.cpi);
        email = (TextView)findViewById(R.id.email);
        phone = (TextView)findViewById(R.id.phone);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getEmail().toString());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){

                        Map<String, Object> docdata = new HashMap();
                        docdata = document.getData();
                        String br = docdata.get("Branch").toString();
                        String em = docdata.get("email").toString();
                        String rn = docdata.get("Roll Number").toString();
                        String ph = docdata.get("Phone").toString();
                        String cp = docdata.get("CPI").toString();
                        String nm = docdata.get("Name").toString();
                        String img = docdata.get("ImageUrl").toString();
                        if(!img.equals("None")){
                            Picasso.with(StudentProfile.this).load(img).fit().into(Profile_Button);
                        }
                        branch.setText(br);
                        cpi.setText(cp);
                        email.setText(em);
                        rollnumber.setText(rn);
                        phone.setText(ph);
                        name.setText(nm);

                        

                    }


                }
            }
        });


        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editwalaintent = new Intent( StudentProfile.this ,EditProfilePage.class);
                startActivity(editwalaintent);
            }
        });

    }
}
