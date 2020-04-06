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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


import android.os.Parcel;
import android.os.Parcelable;

class JobPostingStudent implements Serializable {

    String companyName;
    String jobTitle;
    String jobDescripion;
    Long timestamp;
    String email;
    String url;

    public JobPostingStudent(String a, String b, String c, Long d, String e, String f){

        this.companyName = a;
        this.jobTitle = b;
        this.jobDescripion = c;
        this.timestamp = d;
        this.email = e;
        this.url = f;
    }



}

public class StudentPage1 extends Fragment implements MyAdapter2.OnNoteListener{

    private RecyclerView applicationsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<JobPostingStudent> jobs = new ArrayList<JobPostingStudent>();

    private String TAG = "CompanyPage1";

    public StudentPage1() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_studentpage1, container, false);


        applicationsRecyclerView = (RecyclerView) v.findViewById(R.id.applications_recycler_view);
        applicationsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());

        applicationsRecyclerView.setLayoutManager(layoutManager);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAdapter = new MyAdapter2(jobs,this);
        applicationsRecyclerView.setAdapter(mAdapter);

        db.collection("posts").whereEqualTo("approvalStatus","Approved").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            JobPostingStudent job = new JobPostingStudent(docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("jobDescription").toString(),
                                    (Long) docData.get("timeStamp"), docData.get("companyEmail").toString(), docData.get("brochureURL").toString());
                            jobs.add(job);
                            jobs.sort(new Comparator<JobPostingStudent>() {
                                @Override
                                public int compare(JobPostingStudent o1, JobPostingStudent o2) {
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
    public void onNoteClick(int position) {

        Log.d(TAG, "onNoteClick: clicked." + position);

        Intent intent = new Intent(getActivity(),ShowJobActivity.class);
        intent.putExtra("selected job", jobs.get(position));
        startActivity(intent);
    }

}
