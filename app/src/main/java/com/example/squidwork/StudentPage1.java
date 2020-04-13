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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class StudentPage1 extends Fragment implements MyAdapter2.OnNoteListener {

    private RecyclerView applicationsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<JobPostingStudent> jobs = new ArrayList<JobPostingStudent>();
    private Float student_cpi;
    private String student_branch;
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

        System.out.println("3333333333333333333333VVVVVVVVVVVVVVVVVVVVVVVVVVV");
        applicationsRecyclerView = (RecyclerView) v.findViewById(R.id.applications_recycler_view);
        applicationsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());

        applicationsRecyclerView.setLayoutManager(layoutManager);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAdapter = new MyAdapter2(jobs,this);
        applicationsRecyclerView.setAdapter(mAdapter);


        System.out.println("6666666666666666666664444444443333333333333333333333");
        DocumentReference userDoc = db.collection("users").document(mAuth.getCurrentUser().getEmail().toString());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                System.out.println("77777777777777777777777qqpppppppppppppppppppqqqqqqqqqqqqqqqqqqppppppppppppppp");
                DocumentSnapshot doc = task.getResult();
                if(task.isSuccessful()){
                    System.out.println("99999999999999999pppppppppppppppqqqqqqqqqqqqqqqqqqppppppppppppppp");
                    Map docData = new HashMap();
                    docData = doc.getData();
                    student_branch = docData.get("Branch").toString();
                    student_cpi = Float.parseFloat(docData.get("CPI").toString()) ;
                    System.out.println(student_branch);
                    System.out.println(student_cpi);
                    if(student_branch!=null && student_cpi != null) {


                        db.collection("posts")
                                .whereEqualTo("approvalStatus", "Approved")
                                .whereArrayContains("branches",student_branch)
                                .whereLessThanOrEqualTo("minCPI",student_cpi)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {

                                            Log.d(TAG, "listen:error", e);
                                            return;

                                        }
                                        System.out.println("TADADAAA");


                                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                            switch (documentChange.getType()) {
                                                case ADDED:
                                                    System.out.println("ADDDEDEDEDEDEDEDED");
                                                    Map docData = new HashMap();
                                                    docData = documentChange.getDocument().getData();
                                                    System.out.println("ADDDD " + docData);
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
                                                    System.out.println("MODIFIEDDEDEDED");
                                                    Map docData1 = new HashMap();
                                                    docData1 = documentChange.getDocument().getData();

                                                    String ID=documentChange.getDocument().getId();

                                                    for(JobPostingStudent job1:jobs){


                                                        if(ID.equals(job1.email+"-"+job1.timestamp.toString())){

                                                            job1.companyName = docData1.get("companyName").toString();
                                                            job1.jobTitle = docData1.get("jobTitle").toString();
                                                            job1.jobDescripion = docData1.get("jobDescription").toString();
                                                            job1.timestamp = (Long) docData1.get("timeStamp");
                                                            job1.email = docData1.get("companyEmail").toString();
                                                            //job.approvalStatus = docData.get("approvalStatus").toString();
                                                            job1.url = docData1.get("brochureURL").toString();
                                                            break;

                                                        }




                                                    }
                                                    jobs.sort(new Comparator<JobPostingStudent>() {
                                                        @Override
                                                        public int compare(JobPostingStudent o1, JobPostingStudent o2) {
                                                            return o2.timestamp.compareTo(o1.timestamp);
                                                        }
                                                    });

                                                    mAdapter.notifyDataSetChanged();
                                                    break;
                                                case REMOVED:
                                                    System.out.println("REMOVEDDEDED");
                                                    Map docData2 = new HashMap();
                                                    docData1 = documentChange.getDocument().getData();
                                                    String ID1=documentChange.getDocument().getId();

                                                    for(JobPostingStudent jobq:jobs){


                                                        if(ID1.equals(jobq.email+"-"+jobq.timestamp.toString())){

                                                            jobs.remove(jobq);
                                                            break;

                                                        }




                                                    }
                                                    jobs.sort(new Comparator<JobPostingStudent>() {
                                                        @Override
                                                        public int compare(JobPostingStudent o1, JobPostingStudent o2) {
                                                            return o2.timestamp.compareTo(o1.timestamp);
                                                        }
                                                    });
                                                    mAdapter.notifyDataSetChanged();
                                                    break;
                                            }
                                        }

                                    }
                                });
                    }
                    else{
                        Intent intent = new Intent(getActivity(),EditProfilePage.class);
                        startActivity(intent);
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
        System.out.println("3333333333333333333333MMMMMMMMMMMMMMMMMMMMM");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("863011320660-r32ro0lja4pjlu758sakd2ek25oio8fl.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(student_branch!=null && student_cpi != null) {
//
//
//            db.collection("posts")
//                    .whereEqualTo("approvalStatus", "Approved")
//                    .whereArrayContains("branches",student_branch)
//                    .whereLessThanOrEqualTo("minCPI",student_cpi)
//                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @RequiresApi(api = Build.VERSION_CODES.N)
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                            if (e != null) {
//
//                                Log.d(TAG, "listen:error", e);
//                                return;
//
//                            }
//                            System.out.println("TADADAAA");
//
//
//                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
//                                switch (documentChange.getType()) {
//                                    case ADDED:
//                                        System.out.println("ADDDEDEDEDEDEDEDED");
//                                        Map docData = new HashMap();
//                                        docData = documentChange.getDocument().getData();
//                                        System.out.println("ADDDD " + docData);
//                                        JobPostingStudent job = new JobPostingStudent(docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("jobDescription").toString(),
//                                                (Long) docData.get("timeStamp"), docData.get("companyEmail").toString(), docData.get("brochureURL").toString());
//                                        jobs.add(job);
//                                        jobs.sort(new Comparator<JobPostingStudent>() {
//                                            @Override
//                                            public int compare(JobPostingStudent o1, JobPostingStudent o2) {
//                                                return o2.timestamp.compareTo(o1.timestamp);
//                                            }
//                                        });
//
//                                        mAdapter.notifyDataSetChanged();
//
//                                        break;
//                                    case MODIFIED:
//                                        System.out.println("MODIFIEDDEDEDED");
//                                        Map docData1 = new HashMap();
//                                        docData1 = documentChange.getDocument().getData();
//
//                                        String ID=documentChange.getDocument().getId();
//
//                                        for(JobPostingStudent job1:jobs){
//
//
//                                            if(ID.equals(job1.email+"-"+job1.timestamp.toString())){
//
//                                                job1.companyName = docData1.get("companyName").toString();
//                                                job1.jobTitle = docData1.get("jobTitle").toString();
//                                                job1.jobDescripion = docData1.get("jobDescription").toString();
//                                                job1.timestamp = (Long) docData1.get("timeStamp");
//                                                job1.email = docData1.get("companyEmail").toString();
//                                                //job.approvalStatus = docData.get("approvalStatus").toString();
//                                                job1.url = docData1.get("brochureURL").toString();
//                                                break;
//
//                                            }
//
//
//
//
//                                        }
//                                        jobs.sort(new Comparator<JobPostingStudent>() {
//                                            @Override
//                                            public int compare(JobPostingStudent o1, JobPostingStudent o2) {
//                                                return o2.timestamp.compareTo(o1.timestamp);
//                                            }
//                                        });
//
//                                        mAdapter.notifyDataSetChanged();
//                                        break;
//                                    case REMOVED:
//                                        System.out.println("REMOVEDDEDED");
//                                        Map docData2 = new HashMap();
//                                        docData1 = documentChange.getDocument().getData();
//                                        String ID1=documentChange.getDocument().getId();
//
//                                        for(JobPostingStudent jobq:jobs){
//
//
//                                            if(ID1.equals(jobq.email+"-"+jobq.timestamp.toString())){
//
//                                                jobs.remove(jobq);
//                                                break;
//
//                                            }
//
//
//
//
//                                        }
//                                        jobs.sort(new Comparator<JobPostingStudent>() {
//                                            @Override
//                                            public int compare(JobPostingStudent o1, JobPostingStudent o2) {
//                                                return o2.timestamp.compareTo(o1.timestamp);
//                                            }
//                                        });
//                                        mAdapter.notifyDataSetChanged();
//                                        break;
//                                }
//                            }
//
//                        }
//                    });
//        }
        /*
        System.out.println("6666666666666666666664444444443333333333333333333333");
        final DocumentReference userDoc = db.collection("users").document(mAuth.getCurrentUser().getEmail().toString());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                System.out.println("77777777777777777777777qqpppppppppppppppppppqqqqqqqqqqqqqqqqqqppppppppppppppp");
                DocumentSnapshot doc = task.getResult();
                if (task.isSuccessful()){
                    System.out.println("99999999999999999pppppppppppppppqqqqqqqqqqqqqqqqqqppppppppppppppp");
                    Map docData = new HashMap();
                    docData = doc.getData();
                    student_branch = docData.get("Branch").toString();
                    student_cpi = Float.parseFloat(docData.get("CPI").toString()) ;
                    System.out.println(student_branch);
                    System.out.println(student_cpi);
                }
            }
        });
        //while (student_cpi==null);
        System.out.println("333333333333333333333311111111111111111");
        System.out.println("student_branch");
        System.out.println(student_cpi);
        System.out.println(student_branch);
        System.out.println("student_cpi");*/
    }



    @Override
    public void onNoteClick(int position) {

        Log.d(TAG, "onNoteClick: clicked." + position);

        Intent intent = new Intent(getActivity(),ShowJobActivity.class);
        intent.putExtra("selected job", jobs.get(position));
        startActivity(intent);
    }

}
