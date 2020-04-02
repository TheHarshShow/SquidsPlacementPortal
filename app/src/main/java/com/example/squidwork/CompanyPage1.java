package com.example.squidwork;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

class JobPosting {

    String companyName;
    String jobTitle;
    String jobDescripion;
    String email;
    String approvalStatus;
    Long timestamp;

    public JobPosting(String a, String b, String c, Long d, String e,String app){
        this.approvalStatus = app;
        this.companyName = a;
        this.jobTitle = b;
        this.jobDescripion = c;
        this.timestamp = d;
        this.email = e;
    }


    public void setApprovalStatus(String approved) {
        this.approvalStatus=approved;
    }
}

public class CompanyPage1 extends Fragment implements MyAdapter.OnItemClickListener {

    private RecyclerView applicationsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<JobPosting> jobs = new ArrayList<JobPosting>();
    private String TAG = "CompanyPage1";

    public CompanyPage1() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_companypage1, container, false);


        applicationsRecyclerView = (RecyclerView) v.findViewById(R.id.applications_recycler_view);
        applicationsRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 1);

        applicationsRecyclerView.setLayoutManager(layoutManager);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        mAdapter = new MyAdapter(jobs, this);


        applicationsRecyclerView.setAdapter(mAdapter);

        db.collection("posts").whereEqualTo("companyEmail", currentUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){

                    Log.d(TAG, "listen:error", e);
                    return;

                }



                System.out.println(queryDocumentSnapshots.getDocumentChanges());

                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentChange.getType()) {
                        case ADDED:

                            Map docData = new HashMap();
                            docData = documentChange.getDocument().getData();
                            System.out.println("ADDDD "+docData);
                            String status=docData.get("approvalStatus").toString();
                            if(status==null){
                                status="Waiting";
                            }
                            JobPosting job = new JobPosting(docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("jobDescription").toString(), (Long) docData.get("timeStamp"), currentUser.getEmail(), status);

                            jobs.add(job);
                            jobs.sort(new Comparator<JobPosting>() {
                                @Override
                                public int compare(JobPosting o1, JobPosting o2) {
                                    return o2.timestamp.compareTo(o1.timestamp);
                                }
                            });

                            mAdapter.notifyDataSetChanged();

                            break;
                        case MODIFIED:
                            //Map docData = new HashMap();
                            docData = documentChange.getDocument().getData();
                            System.out.println("777777777777777777777777777777777777777777777777777777777777777777777777777777777");
                            System.out.println("MODDDDD"+docData);
                            int i = 0;
                            for(JobPosting j : jobs){
                                if(j.timestamp.toString().equals(docData.get("timeStamp").toString())){
                                    System.out.println("9999999999999999999999999999999999999999999999999999777");
                                    jobs.set(i,new JobPosting(docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("jobDescription").toString(), (Long) docData.get("timeStamp"),docData.get("companyEmail").toString(), docData.get("approvalStatus").toString()));
                                    System.out.println(jobs.get(i));
                                    JobPosting p =jobs.get(i);
                                    System.out.println(p.approvalStatus);
                                }
                            i++;
                            }
                            mAdapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            docData = documentChange.getDocument().getData();
                            System.out.println("DDD"+docData);
                            //docData = documentChange.getDocument().getData();
                            System.out.println("777777777777777777777777777777777777777777777777777777777777777777777777777777777");
                            System.out.println("DDDDD"+docData);
                            int k = 0;

                            for(JobPosting j : jobs){
                                if(j.timestamp.toString().equals(docData.get("timeStamp").toString())){
                                    System.out.println("maa-chod-di");
                                    jobs.remove(k);
                                    break;
                                }
                                k++;
                            }
                            mAdapter.notifyDataSetChanged();

                            break;
                    }
                }

            }
        });

        Button addPostingButton = (Button) v.findViewById(R.id.add_posting_button);

        addPostingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("NENENE");
                startActivity(new Intent(CompanyPage1.this.getActivity(), AddPostingFormPage.class));

            }
        });



        return v;
    }

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("863011320660-r32ro0lja4pjlu758sakd2ek25oio8fl.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onDeleteClick(final int position) {
        System.out.println("Delete clicked: "+ position);
        JobPosting job = jobs.get(position);
        String postToDeleteID = job.email+"-"+job.timestamp.toString();
        db.collection("posts").document(postToDeleteID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

//                    jobs.remove(position);
                    Toast.makeText(getActivity(), "Delete post successfully", Toast.LENGTH_SHORT).show();

                }else {

                    Log.d(TAG, "Delete task failed");
                    Toast.makeText(getActivity(), "Delete Failed", Toast.LENGTH_SHORT).show();


                }
            }
        });


    }
}
