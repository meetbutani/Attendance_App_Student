package com.meetbutani.attendanceapp_student;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.meetbutani.attendanceapp_student.AdapterClass.AdapterAttendanceSheet;
import com.meetbutani.attendanceapp_student.ModelClass.ModelAttendanceSheet;
import com.meetbutani.attendanceapp_student.ModelClass.ModelCourse;

import java.util.ArrayList;
import java.util.List;

public class AttendanceSheetFragment extends BaseFragment {

    private View view;
    private Context CONTEXT;
    private Bundle bundle;
    private ModelCourse modelCourse;
    private ModelAttendanceSheet modelAttendanceSheet;
    private TextView tvDisASCourseName, tvDisASCourseId;
    private ShapeableImageView ivCopyCourseId;
    private String COURSEID;

    private RecyclerView rvFragAS;

    private ArrayList<ModelAttendanceSheet> arrayListModelAttendanceSheet;
    private AdapterAttendanceSheet adapterAttendanceSheet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getArguments();

        if (bundle != null) {
            modelCourse = (ModelCourse) bundle.getSerializable("modelCourse");
            COURSEID = modelCourse.courseId;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_attendance_sheet, container, false);

        CONTEXT = getContext();

        rvFragAS = view.findViewById(R.id.rvFragAS);

        tvDisASCourseName = view.findViewById(R.id.tvDisASCourseName);
        tvDisASCourseId = view.findViewById(R.id.tvDisASCourseId);
        ivCopyCourseId = view.findViewById(R.id.ivCopyCourseId);


        tvDisASCourseName.setText(modelCourse.courseName);
        tvDisASCourseId.setText(modelCourse.courseId);

        displayAttendanceSheet();

        ivCopyCourseId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, tvDisASCourseId.getText().toString());
                if (clipboard == null) return;
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Course ID Copied!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void displayAttendanceSheet() {
        try {
            rvFragAS.setHasFixedSize(true);

            arrayListModelAttendanceSheet = new ArrayList<>();
            adapterAttendanceSheet = new AdapterAttendanceSheet(getActivity(), arrayListModelAttendanceSheet, bundle);
            rvFragAS.setAdapter(adapterAttendanceSheet);

            firebaseFirestore.collection(COURSESPATH + "/" + COURSEID + "/sheets").orderBy("timestamp", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                modelAttendanceSheet = documentSnapshot.toObject(ModelAttendanceSheet.class);
                                assert modelAttendanceSheet != null;

                                String[] classArray = modelAttendanceSheet.Class.split(" ");
                                if (classArray[0].equalsIgnoreCase(modelCourse.Class) || modelAttendanceSheet.Class.equalsIgnoreCase("Lecture")) {
                                    arrayListModelAttendanceSheet.add(modelAttendanceSheet);
//                                    Toast.makeText(CONTEXT, modelAttendanceSheet.Class, Toast.LENGTH_SHORT).show();
                                }
                            }
//                            Toast.makeText(CONTEXT, modelCourse.Class, Toast.LENGTH_SHORT).show();

                            adapterAttendanceSheet.notifyDataSetChanged();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(CONTEXT, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

}