package com.example.squidwork;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Posting_add_through_ccd extends AppCompatActivity {
    private EditText compname;
    private EditText compemail;
    private EditText jobtitle;
    private EditText jobdesc;
    private Button addpostingbutton;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posting_add_through_ccd);

        jobtitle = (EditText) findViewById(R.id.jobTitleCCD);
        jobdesc = (EditText) findViewById(R.id.jobDescriptionCCD);
        compemail = (EditText) findViewById(R.id.companyEmailCCD);
        compname = (EditText) findViewById(R.id.companyNameCCD);
        addpostingbutton = (Button) findViewById(R.id.addPostingCCD);
        addpostingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jt = jobtitle.getText().toString();
                String jd = jobdesc.getText().toString();
                String cn = compname.getText().toString();
                String ce = compemail.getText().toString();

                if(jt!=null && jd!=null &&cn!=null &&ce!=null ){
                    db = FirebaseFirestore.getInstance();
                    CollectionReference postref =  db.collection("posts");
                    Map postDesc = new HashMap();

                    final Long tsLong = System.currentTimeMillis();
                    postDesc.put("approvalStatus" ,"Waiting");
                    postDesc.put("companyName", cn);
                    postDesc.put("jobTitle", jt);
                    postDesc.put("jobDescription", jd);
                    postDesc.put("timeStamp", tsLong);
                    postDesc.put("companyEmail", ce);
                    String DocId = ce+"-"+tsLong;
                    postref.document(DocId).set(postDesc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Posting_add_through_ccd.this, "Added Post Successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(Posting_add_through_ccd.this, "Could not add post due to some error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else{
                    Toast.makeText(Posting_add_through_ccd.this,"Enter all the details properly !!",Toast.LENGTH_SHORT).show();
                }

            }
        });















    }
}


