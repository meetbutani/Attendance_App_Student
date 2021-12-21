package com.meetbutani.attendanceapp;

import static android.content.Context.MODE_APPEND;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BaseFragment extends Fragment {

    protected Context CONTEXT = getContext();
    protected final String STUDENTPATH = "/app/app/students";
    protected final String COURSESPATH = "/app/app/courses";

    protected FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    protected FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    protected FirebaseUser firebaseUser;
    protected StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    protected SharedPreferences editorSP;
    protected SharedPreferences readSP;
    protected SharedPreferences.Editor editSP;

    @SuppressLint("WrongConstant")
    protected String getUid() {
        readSP = CONTEXT.getSharedPreferences("userData", MODE_APPEND);
        return readSP.getString("uid", "null");
    }

}
