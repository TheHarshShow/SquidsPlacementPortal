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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

class JobPostingCCD {

    String companyName;
    String jobTitle;
    String jobDescripion;
    String email;
    String approvalStatus;
    Long timestamp;

    public JobPostingCCD(String a, String b, String c, Long d, String e, String app){
        this.approvalStatus = app;
        this.companyName = a;
        this.jobTitle = b;
        this.jobDescripion = c;
        this.timestamp = d;
        this.email = e;
    }


    public void setApprovalStatus(String approved) {
        this.approvalStatus = approved;
    }
}

public class ccdPage1 extends Fragment implements MyAdapter3.OnItemClickListener {

    private RecyclerView applicationsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<MyAdapter3.MyViewHolder> mAdapter;
    private ArrayList<JobPosting> jobs = new ArrayList<JobPosting>();
    private String TAG = "ccdPage1";

    public ccdPage1() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        Log.d(TAG, "view created");

        View v=inflater.inflate(R.layout.fragment_ccdpage1, container, false);


        applicationsRecyclerView = (RecyclerView) v.findViewById(R.id.applications_recycler_view);
        applicationsRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 1);

        applicationsRecyclerView.setLayoutManager(layoutManager);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        mAdapter = new MyAdapter3(jobs, this);


        applicationsRecyclerView.setAdapter(mAdapter);

        db.collection("posts").whereEqualTo("approvalStatus","Waiting").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){

                    Log.d(TAG, "listen:error", e);
                    return;

                }
                System.out.println("TADADAAA");


                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentChange.getType()) {
                        case ADDED:

                            Map docData = new HashMap();
                            docData = documentChange.getDocument().getData();
                            System.out.println("ADDDD "+docData);

                            JobPosting job = new JobPosting(docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("jobDescription").toString(), (Long) docData.get("timeStamp"),docData.get("companyEmail").toString(),docData.get("approvalStatus").toString());

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

                            break;
                        case REMOVED:

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
                startActivity(new Intent(ccdPage1.this.getActivity(), Posting_add_through_ccd.class));

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

                    jobs.remove(position);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Delete post successfully", Toast.LENGTH_SHORT).show();

                }else {

                    Log.d(TAG, "Delete task failed");
                    Toast.makeText(getActivity(), "Delete Failed", Toast.LENGTH_SHORT).show();


                }
            }
        });


    }

    public void onUpdateClick(final int position){
        System.out.println("Approved clicked: "+ position);
        JobPosting job = jobs.get(position);
        String postToUpdateID = job.email+"-"+job.timestamp.toString();
        Map<String, Object> postDesc = new HashMap<String, Object>();
        System.out.println(postToUpdateID);
        System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ");
        final Long tsLong = System.currentTimeMillis();
        postDesc.put("approvalStatus" ,"Approved");
        postDesc.put("companyName", job.companyName);
        postDesc.put("jobTitle", job.jobTitle);
        postDesc.put("jobDescription", job.jobDescripion);
        postDesc.put("timeStamp", job.timestamp);
        postDesc.put("companyEmail", job.email);
        System.out.println(postDesc);
        System.out.println("999999999999999999999999");
        System.out.println(job);
        db.collection("posts").document(postToUpdateID).set(postDesc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    jobs.get(position).setApprovalStatus("Approved");
                    jobs.remove(position);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Approved successfully", Toast.LENGTH_SHORT).show();

                }else {

                    Log.d(TAG, "Approving task failed");
                    Toast.makeText(getActivity(), "Approving Failed", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }
}
