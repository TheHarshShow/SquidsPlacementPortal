package com.example.squidwork;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotYetDecidedPage extends AppCompatActivity implements View.OnClickListener {

    private Button signOutButton;
    private Button studentChoiceButton;
    private Button companyChoiceButton;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    String TAG = "NOT_YET_DECIDED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_yet_decided_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signOutButton = findViewById(R.id.sign_out_button);
        studentChoiceButton = findViewById(R.id.student_choice_button);
        companyChoiceButton = findViewById(R.id.company_choice_button);

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.student_choice_button).setOnClickListener(this);
        findViewById(R.id.company_choice_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("863011320660-r32ro0lja4pjlu758sakd2ek25oio8fl.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public void onClick(View v) {

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        switch (v.getId()) {

            case R.id.sign_out_button:
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(NotYetDecidedPage.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }

                        }

                );

                break;
            case R.id.student_choice_button:

                if(currentUser!=null){

                    DocumentReference userRef = db.collection("users").document(currentUser.getEmail());
                    userRef.get().addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){

                                            Map<String, Object> docData = new HashMap();
                                            docData = document.getData();
                                            docData.replace("type", "student");
                                            db.collection("users").document(currentUser.getEmail()).set(docData);
                                            finish();

                                        }


                                    } else {
                                        Log.d(TAG, "Document not found");
                                        Map<String, Object> userDoc = new HashMap<>();
                                        userDoc.put("type", "student");
                                        userDoc.put("email", currentUser.getEmail());
                                        Log.d(TAG, "User does not exist! " + userDoc);
                                        db.collection("users").document(currentUser.getEmail()).set(userDoc);

                                        finish();

                                    }
                                }
                            }

                    );

                } else {

                    finish();

                }
                break;
            case R.id.company_choice_button:
                if(currentUser!=null){

                    DocumentReference userRef = db.collection("users").document(currentUser.getEmail());
                    userRef.get().addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){

                                            Map<String, Object> docData = new HashMap();
                                            docData = document.getData();
                                            docData.replace("type", "company");
                                            db.collection("users").document(currentUser.getEmail()).set(docData);
                                            finish();

                                        }


                                    } else {
                                        Log.d(TAG, "Document not found");
                                        Map<String, Object> userDoc = new HashMap<>();
                                        userDoc.put("type", "company");
                                        userDoc.put("email", currentUser.getEmail());
                                        Log.d(TAG, "User does not exist! " + userDoc);
                                        db.collection("users").document(currentUser.getEmail()).set(userDoc);

                                        finish();

                                    }
                                }
                            }

                    );

                } else {

                    finish();

                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }
}
