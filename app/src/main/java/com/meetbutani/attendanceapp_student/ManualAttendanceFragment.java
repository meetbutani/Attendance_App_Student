package com.meetbutani.attendanceapp_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.meetbutani.attendanceapp_student.ModelClass.ModelAttendanceSheet;
import com.meetbutani.attendanceapp_student.ModelClass.ModelCourse;

public class ManualAttendanceFragment extends BaseFragment {

    private View view;
    private ShapeableImageView ivShowAttendance;
    private TextView tvRVFragMADate, tvRVFragMAClass, tvRVFragMATime, tvRVFragMAType, tvShowAttendance;
    private Bundle bundleAS;
    private Context CONTEXT;
    private ModelAttendanceSheet modelAttendanceSheet;
    private ModelCourse modelCourse;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manual_attendance, container, false);

        CONTEXT = getContext();

        tvRVFragMADate = view.findViewById(R.id.tvRVFragMADate);
        tvRVFragMAClass = view.findViewById(R.id.tvRVFragMAClass);
        tvRVFragMATime = view.findViewById(R.id.tvRVFragMATime);
        tvRVFragMAType = view.findViewById(R.id.tvRVFragMAType);
        tvShowAttendance = view.findViewById(R.id.tvShowAttendance);
        ivShowAttendance = view.findViewById(R.id.ivShowAttendance);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        bundleAS = this.getArguments();

        if (bundleAS != null) {
            modelAttendanceSheet = (ModelAttendanceSheet) bundleAS.getSerializable("modelAttendanceSheet");
            modelCourse = (ModelCourse) bundleAS.getSerializable("modelCourse");
        }

        tvRVFragMADate.setText(modelAttendanceSheet.date);
        tvRVFragMATime.setText((modelAttendanceSheet.startTime + " - " + modelAttendanceSheet.endTime));
        tvRVFragMAClass.setText(modelAttendanceSheet.Class);
        tvRVFragMAType.setText(modelAttendanceSheet.type);

        displayAttendance();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayAttendance();
                swipeRefresh.setRefreshing(false);
                Toast.makeText(CONTEXT, "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void displayAttendance() {
        try {
            firebaseFirestore.collection(COURSESPATH + "/" + modelCourse.courseId + "/sheets/" + modelAttendanceSheet.sheetId + "/attendance")
                    .document("attendance").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    String attendance = documentSnapshot.getString(getRollNo());

                    if (attendance != null) {
                        if (attendance.isEmpty()) {
                            ivShowAttendance.setImageResource(R.drawable.ic_unmark_back);
                            tvShowAttendance.setText("Un-Mark");

                        } else if (attendance.equalsIgnoreCase("present")) {
                            ivShowAttendance.setImageResource(R.drawable.ic_present_back);
                            tvShowAttendance.setText("Present");

                        } else if (attendance.equalsIgnoreCase("absent")) {
                            ivShowAttendance.setImageResource(R.drawable.ic_absent_back);
                            tvShowAttendance.setText("Absent");

                        }
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(CONTEXT, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}