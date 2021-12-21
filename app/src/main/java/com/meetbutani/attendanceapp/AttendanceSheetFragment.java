package com.meetbutani.attendanceapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.meetbutani.attendanceapp.AttendanceSheetData.AdapterAttendanceSheet;
import com.meetbutani.attendanceapp.AttendanceSheetData.ModelAttendanceSheet;
import com.meetbutani.attendanceapp.CourseData.ModelCourse;

import java.util.ArrayList;
import java.util.List;

public class AttendanceSheetFragment extends BaseFragment {

    private View view;
    private Context CONTEXT;
    private Bundle bundle;
    private ModelCourse modelCourse;
    private ModelAttendanceSheet modelAttendanceSheet;
    private String COURSEID;

    private RecyclerView rvFragAS;

    private ArrayList<ModelAttendanceSheet> arrayListModelAttendanceSheet;
    private AdapterAttendanceSheet adapterAttendanceSheet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_attendance_sheet, container, false);

        CONTEXT = getContext();

        rvFragAS = view.findViewById(R.id.rvFragAS);

        modelCourse = getModelCourse();

        displayAttendanceSheet();

        return view;
    }

    private ModelCourse getModelCourse() {
        AttendanceSheetFragment fragment = new AttendanceSheetFragment();
        bundle = fragment.bundle;

        if (bundle != null) {
            modelCourse = (ModelCourse) bundle.getSerializable("modelCourse");
            COURSEID = modelCourse.courseId;
        }

        return modelCourse;
    }


    private void displayAttendanceSheet() {
        try {
            rvFragAS.setHasFixedSize(true);

            arrayListModelAttendanceSheet = new ArrayList<>();
            adapterAttendanceSheet = new AdapterAttendanceSheet(getActivity(), arrayListModelAttendanceSheet, getModelCourse());
            rvFragAS.setAdapter(adapterAttendanceSheet);

            getModelCourse();
            firebaseFirestore.collection(COURSESPATH + "/" + COURSEID + "/sheets").orderBy("timestamp", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                modelAttendanceSheet = documentSnapshot.toObject(ModelAttendanceSheet.class);
                                arrayListModelAttendanceSheet.add(modelAttendanceSheet);
                            }

                            adapterAttendanceSheet.notifyDataSetChanged();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(CONTEXT, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

}