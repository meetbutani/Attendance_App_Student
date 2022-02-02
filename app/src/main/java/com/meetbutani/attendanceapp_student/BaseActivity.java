package com.meetbutani.attendanceapp_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

@SuppressLint("WrongConstant")
public class BaseActivity extends AppCompatActivity {

    protected final String STUDENTPATH = "/app/app/students";
    protected final String COURSESPATH = "/app/app/courses";
    protected Context CONTEXT = BaseActivity.this;

    protected FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    protected FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    protected FirebaseUser firebaseUser;
    protected StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    protected SharedPreferences editorSP;
    protected SharedPreferences readSP;
    protected SharedPreferences.Editor editSP;

    private void readSP() {
        readSP = CONTEXT.getSharedPreferences("userData", MODE_APPEND);
    }

    private void editSP() {
        editorSP = CONTEXT.getSharedPreferences("userData", MODE_PRIVATE);
        editSP = editorSP.edit();
    }

    protected void setUid(String value) {
        editSP();
        editSP.putString("uid", value).commit();
    }

    protected String getUid() {
        readSP();
        return readSP.getString("uid", "null");
    }

    protected void setName(String firstName, String lastName) {
        editSP();
        editSP.putString("name", firstName + " " + lastName).commit();
    }

    protected String getName() {
        readSP();
        return readSP.getString("name", " ");
    }

    protected void setImageURL(String imageURL) {
        editSP();
        editSP.putString("imageURL", imageURL).commit();
    }

    protected String getImageURL() {
        readSP();
        return readSP.getString("imageURL", " ");
    }

    protected void setEmailId(String emailId) {
        editSP();
        editSP.putString("emailId", emailId).commit();
    }

    protected String getEmailId() {
        readSP();
        return readSP.getString("emailId", " ");
    }

    protected String getRollNo() {
        readSP = getSharedPreferences("userData", MODE_APPEND);
        return readSP.getString("rollNo", "null");
    }

    protected void setRollNo(String value) {
        editorSP = getSharedPreferences("userData", MODE_PRIVATE);
        editSP = editorSP.edit();
        editSP.putString("rollNo", value).commit();
    }

    protected void setFragment(Fragment fragment, String FragTitle) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayMain, fragment, FragTitle)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
