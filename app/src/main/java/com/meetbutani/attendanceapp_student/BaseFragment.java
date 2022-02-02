package com.meetbutani.attendanceapp_student;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

@SuppressLint("WrongConstant")
public class BaseFragment extends Fragment {

    protected final String STUDENTPATH = "/app/app/students";
    protected final String COURSESPATH = "/app/app/courses";
    protected Context CONTEXT = getContext();
    protected FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    protected FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    protected FirebaseUser firebaseUser;
    protected StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    protected SharedPreferences editorSP;
    protected SharedPreferences readSP;
    protected SharedPreferences.Editor editSP;

    protected String getUid() {
        readSP = requireContext().getSharedPreferences("userData", MODE_APPEND);
        return readSP.getString("uid", "null");
    }

    protected void setUid(String value) {
        editorSP = requireContext().getSharedPreferences("userData", MODE_PRIVATE);
        editSP = editorSP.edit();
        editSP.putString("uid", value).commit();
    }

    protected String getRollNo() {
        readSP = requireContext().getSharedPreferences("userData", MODE_APPEND);
        return readSP.getString("rollNo", "null");
    }

    protected void setRollNo(String value) {
        editorSP = requireContext().getSharedPreferences("userData", MODE_PRIVATE);
        editSP = editorSP.edit();
        editSP.putString("rollNo", value).commit();
    }

}
