package com.example.squidwork;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;
    private  SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private String TAG = "myActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();

        // Access a Cloud Firestore instance from your Activity

        db = FirebaseFirestore.getInstance();

        signInButton.setVisibility(View.INVISIBLE);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("863011320660-r32ro0lja4pjlu758sakd2ek25oio8fl.apps.googleusercontent.com").requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        signInButton.setVisibility(View.INVISIBLE);

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getEmail());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Map docData = new HashMap();
                            docData = document.getData();

                            Log.d(TAG, "User exists! " + docData.get("type") + " " + docData.get("email"));

                            if (docData.get("type").equals("student")) {
                                if(docData.get("profile").equals("Updated")) {
                                    startActivity(new Intent(MainActivity.this, StudentPage.class));
                                    System.out.println(docData.get("profile"));
                                    System.out.println("UPDATED WALAAAA");
                                }else{
                                    startActivity(new Intent(MainActivity.this, EditProfilePage.class));
                                    System.out.println(docData.get("profile"));
                                    System.out.println("NOTTTTTT");
                                }

                            } else if (docData.get("type").equals("notYetDecided")) {

                                Log.d(TAG, "not yet decided");

                                startActivity(new Intent(MainActivity.this, NotYetDecidedPage.class));

                            } else if (docData.get("type").equals("company")){

                                Log.d(TAG, "company");

                                startActivity(new Intent(MainActivity.this, CompanyPage.class));
                            } else if (docData.get("type").equals("CCD")){
                                Log.d(TAG, "ccd");

                                startActivity(new Intent(MainActivity.this, ccdPage.class));

                            }


                        } else {

                            Map<String, Object> userDoc = new HashMap<>();
                            userDoc.put("type", "notYetDecided");
                            userDoc.put("email", currentUser.getEmail());
                            userDoc.put("profile","NotUpdated");
                            userDoc.put("ImageUrl","None");
                            Log.d(TAG, "User does not exist! " + userDoc);

                            db.collection("users").document(currentUser.getEmail()).set(userDoc);

                            startActivity(new Intent(MainActivity.this, NotYetDecidedPage.class));

                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                        signInButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {

            signInButton.setVisibility(View.VISIBLE);


        }

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();

            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(MainActivity.this, "Couldn't Sign In", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(MainActivity.this, "Success "+acct.getEmail(), Toast.LENGTH_SHORT).show();
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DocumentReference userRef = db.collection("users").document(user.getEmail());
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            Map docData = new HashMap();
                                            docData = document.getData();
                                            Log.d(TAG, "User exists! "+docData.get("type")+" "+docData.get("email"));

                                            if(docData.get("type").equals("student")){

                                                if(docData.get("profile").equals("Updated")) {
                                                    startActivity(new Intent(MainActivity.this, StudentPage.class));
                                                    System.out.println(docData.get("profile"));
                                                    System.out.println("UPDATED WALAAAA");
                                                }else{
                                                    startActivity(new Intent(MainActivity.this, EditProfilePage.class));
                                                    System.out.println(docData.get("profile"));
                                                    System.out.println("NOTTTTTT");
                                                }

                                            } else if(docData.get("type").equals("notYetDecided")){

                                                Log.d(TAG, "not yet decided");

                                                startActivity(new Intent(MainActivity.this, NotYetDecidedPage.class));

                                            } else if (docData.get("type").equals("company")){

                                                Log.d(TAG, "company");

                                                startActivity(new Intent(MainActivity.this, CompanyPage.class));
                                            } else if(docData.get("type").equals("CCD")){
                                                Log.d(TAG, "ccd");

                                                startActivity(new Intent(MainActivity.this, ccdPage.class));
                                            }


                                        } else {

                                            Map<String, Object> userDoc = new HashMap<>();
                                            userDoc.put("type","notYetDecided");
                                            userDoc.put("profile","NotUpdated");
                                            userDoc.put("ImageUrl","None");
                                            userDoc.put("email",user.getEmail());
                                            Log.d(TAG, "User does not exist! "+userDoc);
                                            db.collection("users").document(user.getEmail()).set(userDoc);

                                            startActivity(new Intent(MainActivity.this, NotYetDecidedPage.class));

                                        }
                                    } else {
                                        Log.d(TAG, "Failed with: ", task.getException());
                                        signInButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());


                            mGoogleSignInClient.signOut();
                            Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



}
