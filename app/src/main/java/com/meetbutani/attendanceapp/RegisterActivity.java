package com.meetbutani.attendanceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends BaseActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private CircularImageView ivProfileUserPic;
    private ShapeableImageView ivEditProfileUserPic, ivMin8Char, ivMin1Num, ivMin1Upper, ivMin1Lower, ivMin1SpeChar;
    private TextInputLayout etErRegisterFirstName, etErRegisterLastName, etErRegisterRollNo, etErRegisterEmail, etErRegisterPassword, etErRegisterConfirmPassword;
    private TextInputEditText etRegisterFirstName, etRegisterLastName, etRegisterRollNo, etRegisterEmail, etRegisterPassword, etRegisterConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginNow;
    private LinearLayout linearRegister;
    private ProgressDialog progressDialog;
    private boolean valid = true;
    private String password, emailSent = "no";
    private Uri imageUri;
    private String[] cameraPermission;
    private String[] storagePermission;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ivProfileUserPic = findViewById(R.id.ivProfileUserPic);
        ivEditProfileUserPic = findViewById(R.id.ivEditProfileUserPic);
        etErRegisterFirstName = findViewById(R.id.etErRegisterFirstName);
        etRegisterFirstName = findViewById(R.id.etRegisterFirstName);
        etErRegisterLastName = findViewById(R.id.etErRegisterLastName);
        etRegisterLastName = findViewById(R.id.etRegisterLastName);
        etErRegisterRollNo = findViewById(R.id.etErRegisterRollNo);
        etRegisterRollNo = findViewById(R.id.etRegisterRollNo);
        etErRegisterEmail = findViewById(R.id.etErRegisterEmail);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etErRegisterPassword = findViewById(R.id.etErRegisterPassword);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etErRegisterConfirmPassword = findViewById(R.id.etErRegisterConfirmPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginNow = findViewById(R.id.tvLoginNow);
        ivMin8Char = findViewById(R.id.ivMin8Char);
        ivMin1Num = findViewById(R.id.ivMin1Num);
        ivMin1Upper = findViewById(R.id.ivMin1Upper);
        ivMin1Lower = findViewById(R.id.ivMin1Lower);
        ivMin1SpeChar = findViewById(R.id.ivMin1SpeChar);
        linearRegister = findViewById(R.id.linearRegister);

        progressDialog = new ProgressDialog(this);

        CONTEXT = RegisterActivity.this;

        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        checkValidation();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

        tvLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(CONTEXT, LoginActivity.class));
                finish();
            }
        });

/*
        ivEditProfileUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectProfile = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityIfNeeded(selectProfile, imageRequestCode);
            }
        });
*/

        ivEditProfileUserPic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });
    }

/*
    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CONTEXT);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                // checking camera permissions
                boolean camera = ContextCompat.checkSelfPermission(CONTEXT, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
                // checking storage permissions
                boolean external = ContextCompat.checkSelfPermission(CONTEXT, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

                if (selected == 0) {
                    if (!(camera && external)) {
                        // Requesting camera permission
                        requestPermissions(cameraPermission, CAMERA_REQUEST);
                    } else {
                        pickFromGallery();
                    }
                } else if (selected == 1) {
                    if (!external) {
                        // Requesting  gallery permission

                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }
*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showImagePicDialog() {
        // checking camera permissions
        boolean camera = ContextCompat.checkSelfPermission(CONTEXT, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        // checking storage permissions
        boolean external = ContextCompat.checkSelfPermission(CONTEXT, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        if (!(camera && external)) {
            // Requesting camera permission
            requestPermissions(cameraPermission, CAMERA_REQUEST);
            requestPermissions(storagePermission, STORAGE_REQUEST);
        } else {
            pickFromGallery();
        }
    }


    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity()
                .setActivityTitle("Image Cropper")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .setBackgroundColor(Color.parseColor("#AA000000"))
                .start(RegisterActivity.this);
    }

    // Requesting camera and gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    CropImage.ActivityResult croped_image = CropImage.getActivityResult(data);
                    imageUri = croped_image.getUri();
                    Picasso.get().load(imageUri).into(ivProfileUserPic);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == imageRequestCode && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                if (data.getData() != null) {
                    imageUri = data.getData();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
*/

    private void RegisterUser() {
        String firstName = Objects.requireNonNull(etRegisterFirstName.getText()).toString().toUpperCase(Locale.ROOT).trim();
        String lastName = Objects.requireNonNull(etRegisterLastName.getText()).toString().toUpperCase(Locale.ROOT).trim();
        String rollNo = Objects.requireNonNull(etRegisterRollNo.getText()).toString().trim();
        String email = Objects.requireNonNull(etRegisterEmail.getText()).toString().toLowerCase(Locale.ROOT).trim();
        String password = Objects.requireNonNull(etRegisterPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etRegisterConfirmPassword.getText()).toString().trim();

        valid = true;
        validateFirstName(firstName);
        validateLastName(lastName);
        validateRollNo(rollNo);
        validateEmail(email);
        validatePassword(password);
        validateConfirmPassword(confirmPassword);

        if (valid) {
            progressDialog.setMessage("Registration in progress...");
            progressDialog.show();

            setUid(email);

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressDialog.cancel();

                    firebaseUser = firebaseAuth.getCurrentUser();

                    if (imageUri != null) {
                        StorageReference imageStorageRef = storageReference.child("Student Profile Pic/" + getUid());

                        imageStorageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.get().load(uri).into(ivProfileUserPic);

                                        updateProfile(uri.toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(CONTEXT);
                                dialog.setMessage("Error: " + e.getMessage()).create().show();

                                updateProfile(" ");
                            }
                        });
                    } else {
                        updateProfile(" ");
                    }
                }

                private void updateProfile(String uri) {

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstName + " " + lastName)
                            .setPhotoUri(Uri.parse(uri))
                            .build();

                    firebaseUser.updateProfile(profileUpdates)
                            .addOnSuccessListener(unused -> {
                            });

                    storeData(uri);
                }

                private void storeData(String uri) {

                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            emailSent = "yes";
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder onFailure = new AlertDialog.Builder(CONTEXT);
                            onFailure.setTitle("Error");
                            onFailure.setMessage("Error Code: " + ((FirebaseAuthException) e).getErrorCode() + "\nError: " + e.getMessage()).create().show();
                        }
                    });

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("firstName", firstName);
                    userInfo.put("lastName", lastName);
                    userInfo.put("rollNo", rollNo);
                    userInfo.put("emailId", email);
                    userInfo.put("userType", "Student");
                    userInfo.put("imageURL", uri);

                    firebaseFirestore.collection(STUDENTPATH).document(getUid())
                            .set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            setRollNo(rollNo);

                            Toast.makeText(CONTEXT, "Registered Successfully", Toast.LENGTH_SHORT).show();

                            firebaseAuth.signOut();
                            startActivity(new Intent(CONTEXT, LoginActivity.class)
                                    .putExtra("email", email)
                                    .putExtra("emailSent", emailSent));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder onFailure = new AlertDialog.Builder(CONTEXT);
                            onFailure.setTitle("Error");
                            onFailure.setMessage("Error: Data Not created on Firestore").create().show();
                        }
                    });
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.cancel();
                    String errorCode = ((FirebaseAuthException) e).getErrorCode();
                    switch (errorCode) {
                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            etErRegisterEmail.setErrorEnabled(true);
                            etErRegisterEmail.setError("This Email already Registered");
                            break;
                        case "ERROR_INVALID_EMAIL":
                            etErRegisterEmail.setErrorEnabled(true);
                            etErRegisterEmail.setError("Invalid Email ID");
                            break;
                        case "ERROR_WEAK_PASSWORD":
                            etErRegisterPassword.setErrorEnabled(true);
                            etErRegisterPassword.setError("Password should be at least 6 characters");
                            break;
                        default:
                            AlertDialog.Builder onFailure = new AlertDialog.Builder(CONTEXT);
                            onFailure.setTitle("Error");
                            onFailure.setMessage("Error Code: " + errorCode + "\nError: " + e.getMessage()).create().show();
                            break;
                    }
                }
            });
        }
    }

    private void checkValidation() {

        etRegisterFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFirstName(String.valueOf(s).trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etRegisterLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateLastName(String.valueOf(s).trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etRegisterRollNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateRollNo(String.valueOf(s).trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etRegisterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(String.valueOf(s).trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(String.valueOf(s).trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etRegisterConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateConfirmPassword(String.valueOf(s).trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void validateConfirmPassword(String confirmPassword) {
        etErRegisterConfirmPassword.setErrorEnabled(false);
        if (confirmPassword.isEmpty()) {
            etErRegisterConfirmPassword.setErrorEnabled(true);
            etErRegisterConfirmPassword.setError("Confirm Password Required");
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            etErRegisterConfirmPassword.setErrorEnabled(true);
            etErRegisterConfirmPassword.setError("Password doesn't match");
            valid = false;
        }
    }

    private void validatePassword(String password) {
        this.password = password;
        linearRegister.setVisibility(View.GONE);
        ivMin8Char.setBackgroundColor(Color.parseColor("#53D853"));
        ivMin1Num.setBackgroundColor(Color.parseColor("#53D853"));
        ivMin1Upper.setBackgroundColor(Color.parseColor("#53D853"));
        ivMin1Lower.setBackgroundColor(Color.parseColor("#53D853"));
        ivMin1SpeChar.setBackgroundColor(Color.parseColor("#53D853"));

        if (password.isEmpty()) {
            linearRegister.setVisibility(View.GONE);
            etErRegisterPassword.setErrorEnabled(true);
            etErRegisterPassword.setError("Password Required");
            valid = false;
        } else {
            etErRegisterPassword.setErrorEnabled(false);
            // 8 character
            if (!(password.length() >= 8)) {
                linearRegister.setVisibility(View.VISIBLE);
                ivMin8Char.setBackgroundColor(Color.parseColor("#DADADA"));
            }
            //number
            if (!password.matches("(.*[0-9].*)")) {
                linearRegister.setVisibility(View.VISIBLE);
                ivMin1Num.setBackgroundColor(Color.parseColor("#DADADA"));
            }
            //upper case
            if (!password.matches("(.*[A-Z].*)")) {
                linearRegister.setVisibility(View.VISIBLE);
                ivMin1Upper.setBackgroundColor(Color.parseColor("#DADADA"));
            }
            //lower case
            if (!password.matches("(.*[a-z].*)")) {
                linearRegister.setVisibility(View.VISIBLE);
                ivMin1Lower.setBackgroundColor(Color.parseColor("#DADADA"));
            }
            //symbol
            if (!password.matches("^(?=.*[_.()$&@]).*$")) {
                linearRegister.setVisibility(View.VISIBLE);
                ivMin1SpeChar.setBackgroundColor(Color.parseColor("#DADADA"));
            }

            if (!(password.length() >= 8) ||
                    !password.matches("(.*[0-9].*)") ||
                    !password.matches("(.*[A-Z].*)") ||
                    !password.matches("(.*[a-z].*)") ||
                    !password.matches("^(?=.*[_.()$&@]).*$")) {
                valid = false;
            }
        }
    }

    private void validateEmail(String email) {
        if (email.trim().isEmpty()) {
            etErRegisterEmail.setErrorEnabled(true);
            etErRegisterEmail.setError("Email ID Required");
            valid = false;
        } else if (!email.trim().matches("[a-zA-Z0-9._-]+@[a-z.]+\\.+[a-z]+")) {
            etErRegisterEmail.setErrorEnabled(true);
            etErRegisterEmail.setError("Invalid Email ID");
            valid = false;
        } else etErRegisterEmail.setErrorEnabled(false);
    }

    private void validateRollNo(String rollNo) {
        if (rollNo.trim().isEmpty()) {
            etErRegisterRollNo.setErrorEnabled(true);
            etErRegisterRollNo.setError("Roll No Required");
            valid = false;
        } else etErRegisterRollNo.setErrorEnabled(false);
    }

    private void validateLastName(String lastName) {
        if (lastName.isEmpty()) {
            etErRegisterLastName.setErrorEnabled(true);
            etErRegisterLastName.setError("Last Name Required");
            valid = false;
        } else etErRegisterLastName.setErrorEnabled(false);
    }

    private void validateFirstName(String firstName) {
        if (firstName.isEmpty()) {
            etErRegisterFirstName.setErrorEnabled(true);
            etErRegisterFirstName.setError("First Name Required");
            valid = false;
        } else etErRegisterFirstName.setErrorEnabled(false);
    }

/*
    private void uploadImage() {
        StorageReference imageStorageRef = storageReference.child("Teacher Profile Pic/" + uid);

        imageStorageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUri = uri;
                        Picasso.get().load(uri).into(ivProfileUserPic);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
*/

}