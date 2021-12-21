package com.meetbutani.attendanceapp.CourseData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.meetbutani.attendanceapp.AttendanceSheetFragment;
import com.meetbutani.attendanceapp.R;

import java.util.ArrayList;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.ViewHolder> {

    private final FragmentActivity CONTEXT;
    private final ArrayList<ModelCourse> arrayListModelCourse;

    public AdapterCourse(FragmentActivity CONTEXT, ArrayList<ModelCourse> arrayListModelCourse) {
        this.CONTEXT = CONTEXT;
        this.arrayListModelCourse = arrayListModelCourse;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_frag_courses, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvRecViewCourseListCourseName.setText(arrayListModelCourse.get(position).courseName);
    }

    @Override
    public int getItemCount() {
        return arrayListModelCourse.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvRecViewCourseListCourseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            tvRecViewCourseListCourseName = itemView.findViewById(R.id.tvLayCoursesCourseName);

        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();

            ModelCourse modelCourse = arrayListModelCourse.get(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("modelCourse", modelCourse);

            AttendanceSheetFragment fragment = new AttendanceSheetFragment();
            fragment.setArguments(bundle);
            CONTEXT.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayMain, fragment)
                    .addToBackStack(null)
                    .commit();

//            Toast.makeText(CONTEXT, position + "", Toast.LENGTH_SHORT).show();
        }
    }
}
