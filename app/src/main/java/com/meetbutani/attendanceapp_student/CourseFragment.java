package com.meetbutani.attendanceapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.meetbutani.attendanceapp_student.AdapterClass.AdapterCourse;
import com.meetbutani.attendanceapp_student.ModelClass.ModelCourse;

import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends BaseFragment {

    private View view;
    private RecyclerView rvCourse;
    private TextView tvDisCourses;
    private ModelCourse modelCourse;
    private ArrayList<ModelCourse> arrayListModelCourse;
    private AdapterCourse adapterCourse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_course, container, false);

        CONTEXT = getContext();

        rvCourse = view.findViewById(R.id.rvCourse);
        tvDisCourses = view.findViewById(R.id.tvDisCourses);

        tvDisCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CONTEXT, "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        displayCourses();

        return view;
    }

    protected void displayCourses() {
        try {
            rvCourse.setHasFixedSize(true);

            arrayListModelCourse = new ArrayList<>();
            adapterCourse = new AdapterCourse(getActivity(), arrayListModelCourse);
            rvCourse.setAdapter(adapterCourse);

            firebaseFirestore.collection(STUDENTPATH + "/" + getUid() + "/courses").orderBy("courseName", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                modelCourse = documentSnapshot.toObject(ModelCourse.class);
                                arrayListModelCourse.add(modelCourse);
                            }

                            adapterCourse.notifyDataSetChanged();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(CONTEXT, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
}