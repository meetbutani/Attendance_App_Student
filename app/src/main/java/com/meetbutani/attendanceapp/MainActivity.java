package com.meetbutani.attendanceapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private MaterialToolbar tbMainActivity;
    private NavigationView navViewMain;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.tbMainActivity));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawerLayout);
        tbMainActivity = findViewById(R.id.tbMainActivity);
        navViewMain = findViewById(R.id.navViewMain);

        CONTEXT = MainActivity.this;

        setFragment(new CourseFragment());

        tbMainActivity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navViewMain.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id) {
                    case R.id.itCourse:
                        setFragment(new CourseFragment());
                        break;

                    case R.id.nav_home2:
                        Toast.makeText(CONTEXT, "Home 2", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home3:
                        Toast.makeText(CONTEXT, "Home 3", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home4:
                        Toast.makeText(CONTEXT, "Home 4", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home5:
                        Toast.makeText(CONTEXT, "Home 5", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home6:
                        Toast.makeText(CONTEXT, "Home 6", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home7:
                        Toast.makeText(CONTEXT, "Home 7", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home8:
                        Toast.makeText(CONTEXT, "Home 8", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home9:
                        Toast.makeText(CONTEXT, "Home 9", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home10:
                        Toast.makeText(CONTEXT, "Home 10", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_home11:
                        Toast.makeText(CONTEXT, "Home 11", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itJoinCourse:
                joinNewCourse();
                break;

            case R.id.itLogout:
                firebaseAuth.signOut();
                startActivity(new Intent(CONTEXT, LoginActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void joinNewCourse() {

        final View dialog = getLayoutInflater().inflate(R.layout.dialog_join_course, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextInputEditText etDialogCourseId = dialog.findViewById(R.id.etDialogCourseId);
        TextInputLayout etErDialogCourseId = dialog.findViewById(R.id.etErDialogCourseId);
        RadioButton rBtnDialogA = dialog.findViewById(R.id.rBtnDialogA);
        RadioButton rBtnDialogB = dialog.findViewById(R.id.rBtnDialogB);

        builder.setView(dialog)
                .setTitle("Join New Course")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String courseId = String.valueOf(etDialogCourseId.getText()).trim();
                        if (courseId.isEmpty()) return;

                        firebaseFirestore.collection(COURSESPATH).document(courseId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (!String.valueOf(documentSnapshot.get("courseId")).isEmpty()) {
                                    String id = String.valueOf(documentSnapshot.get("courseId"));
                                    if (id.equalsIgnoreCase(courseId)) {
                                        String Class = " ";
                                        if (rBtnDialogA.isChecked())
                                            Class = "A";
                                        if (rBtnDialogB.isChecked())
                                            Class = "B";

                                        Map<String, Object> addCourse = new HashMap<>();
                                        addCourse.put("courseName", documentSnapshot.get("courseName"));
                                        addCourse.put("courseId", id);

                                        String finalClass = Class;
                                        firebaseFirestore.collection(STUDENTPATH + "/" + getUid() + "/courses").document(courseId).set(addCourse).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                firebaseFirestore.collection(STUDENTPATH).document(getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Map<String, Object> addStudent = new HashMap<>();
                                                        addStudent.put("firstName", documentSnapshot.get("firstName"));
                                                        addStudent.put("lastName", documentSnapshot.get("lastName"));
                                                        addStudent.put("rollNo", documentSnapshot.get("rollNo"));
                                                        addStudent.put("emailId", documentSnapshot.get("emailId"));
                                                        addStudent.put("imageURL", documentSnapshot.get("imageURL"));
                                                        addStudent.put("Class", finalClass);
                                                        firebaseFirestore.collection(COURSESPATH + "/" + id + "/students").document(getUid()).set(addStudent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getApplicationContext(), "Course Add Successfully", Toast.LENGTH_SHORT).show();
                                                                setFragment(new CourseFragment());
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        Toast.makeText(MainActivity.this, "Course Not Found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Course Not Found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Course Not Found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                });

        builder.create().show();
    }
}