package com.meetbutani.attendanceapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends BaseActivity {

    private TextView tvRegisterNow, tvResendEmail, tvForgotPassword;
    private MaterialToolbar tbLoginActivity;
    private TextInputLayout etErLoginEmail, etErLoginPassword;
    private TextInputEditText etLoginPassword, etLoginEmail;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private boolean valid = true;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tbLoginActivity = findViewById(R.id.tbLoginActivity);
        etErLoginEmail = findViewById(R.id.etErLoginEmail);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etErLoginPassword = findViewById(R.id.etErLoginPassword);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvResendEmail = findViewById(R.id.tvResendEmail);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegisterNow = findViewById(R.id.tvRegisterNow);

        CONTEXT = LoginActivity.this;

        progressDialog = new ProgressDialog(this);

        if (getIntent() != null) {
            etLoginEmail.setText(getIntent().getStringExtra("email"));
            String emailSent = getIntent().getStringExtra("emailSent") + "";
            if (emailSent.equals("yes")) {
                AlertDialog.Builder verifyEmailDialog = new AlertDialog.Builder(CONTEXT);
                verifyEmailDialog.setMessage("Verification email has been sent...").create().show();
            }
        }

        tvRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CONTEXT, RegisterActivity.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                resetPasswordDialog(view);
            }
        });

        etLoginEmail.addTextChangedListener(new TextWatcher() {
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

        etLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(String.valueOf(s).trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvResendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        tvResendEmail.setVisibility(View.GONE);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CONTEXT);
                        dialog.setMessage("Verification email has been sent").create().show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorCode = ((FirebaseAuthException) e).getErrorCode();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CONTEXT);
                        dialog.setMessage("Error code: " + errorCode + "\nError: " + e.getMessage()).create().show();
                    }
                });
            }
        });
    }

    private void userLogin() {
        String email = Objects.requireNonNull(etLoginEmail.getText()).toString().toLowerCase(Locale.ROOT).trim();
        String password = Objects.requireNonNull(etLoginPassword.getText()).toString().trim();

        valid = true;
        validateEmail(email);
        validatePassword(password);

        if (valid) {
            progressDialog.setMessage("Login in progress!");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressDialog.cancel();

                    firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    if (!firebaseUser.isEmailVerified()) {
                        tvResendEmail.setVisibility(View.VISIBLE);
                        AlertDialog.Builder verifyEmailDialog = new AlertDialog.Builder(CONTEXT);
                        verifyEmailDialog.setMessage("Verify your email address").create().show();
                        return;
                    }

                    tvResendEmail.setVisibility(View.GONE);

                    setUid(email);
                    firebaseFirestore.collection(TEACHERPATH).document(getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (TextUtils.equals(documentSnapshot.getString("userType"), "Teacher")) {
                                        Toast.makeText(CONTEXT, "Login Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CONTEXT, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(CONTEXT, "Student Not Allow to Login in Teacher App.", Toast.LENGTH_LONG).show();
                                        firebaseAuth.signOut();
                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.cancel();
                    try {
                    String errorCode = ((FirebaseAuthException) e).getErrorCode();
                    switch (errorCode) {
                        case "ERROR_USER_NOT_FOUND":
                            etErLoginEmail.setErrorEnabled(true);
                            etErLoginEmail.setError("Email ID not Registered");
                            break;
                        case "ERROR_INVALID_EMAIL":
                            etErLoginEmail.setErrorEnabled(true);
                            etErLoginEmail.setError("Invalid Email ID");
                            break;
                        case "ERROR_WRONG_PASSWORD":
                            etErLoginPassword.setErrorEnabled(true);
                            etErLoginPassword.setError("Invalid Email ID");
                            etLoginPassword.setError("Invalid Password");
                            break;
                        default:
                            Toast.makeText(CONTEXT, e.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                    } catch (Exception error){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CONTEXT);
                        dialog.setMessage("Error: " + error.getMessage()).create().show();
                    }
                }
            });
        }
    }

    private void validateEmail(String email) {
        if (email.trim().isEmpty()) {
            etErLoginEmail.setErrorEnabled(true);
            etErLoginEmail.setError("Email ID Required");
            valid = false;
        } else if (!email.trim().matches("[a-zA-Z0-9._-]+@[a-z.]+\\.+[a-z]+")) {
            etErLoginEmail.setErrorEnabled(true);
            etErLoginEmail.setError("Invalid Email ID");
            valid = false;
        } else etErLoginEmail.setErrorEnabled(false);
    }

    private void validatePassword(String password) {
        if (password.isEmpty()) {
            etErLoginPassword.setErrorEnabled(true);
            etErLoginPassword.setError("Password Required");
            valid = false;
        } else {
            etErLoginPassword.setErrorEnabled(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void resetPasswordDialog(View view) {

        final View dialog = getLayoutInflater().inflate(R.layout.dialog_single_field, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CONTEXT);

        TextInputEditText etSingleField = dialog.findViewById(R.id.etSingleField);
        TextInputLayout etErSingleField = dialog.findViewById(R.id.etErSingleField);

        etErSingleField.setHint("Email Id");

        etSingleField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etSingleField.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);

        builder.setView(dialog)
                .setTitle("Reset Password Link")
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = Objects.requireNonNull(etSingleField.getText()).toString().toLowerCase(Locale.ROOT).trim();
                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(CONTEXT);
                                dialog.setMessage("Reset Link send to your email").create().show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String errorCode = ((FirebaseAuthException) e).getErrorCode();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(CONTEXT);

                                switch (errorCode) {
                                    case "ERROR_USER_NOT_FOUND":
                                        dialog.setMessage("Email ID not Registered");
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        dialog.setMessage("Invalid Email ID");
                                        break;
                                    default:
                                        dialog.setMessage("Error code: " + errorCode + "\nError: " + e.getMessage());
                                        break;
                                }
                                dialog.create().show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                });

        builder.create().show();
    }
}