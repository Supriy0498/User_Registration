package com.sjcoders.userregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextView userSignUp,textViewForgotPwd;
    private TextInputLayout filledTextFieldPwd,filledTextFieldEmail,editTextEmail;
    private Button buttonLogin,btnSendResetEmail;
    private String email,password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            finish();
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        progressDialog = new ProgressDialog(MainActivity.this);
        filledTextFieldPwd = findViewById(R.id.filledTextFieldPwd);
        filledTextFieldEmail = findViewById(R.id.filledTextFieldEmail);
        buttonLogin = findViewById(R.id.btnLogin);
        userSignUp = findViewById(R.id.textViewUserSignUp);
        textViewForgotPwd = findViewById(R.id.textViewForgotPwd);
        userSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserSignUpActivity.class));
            }
        });

        textViewForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPasswordBottomSheet();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    public void loginUser(){

        email = Objects.requireNonNull(filledTextFieldEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(filledTextFieldPwd.getEditText()).getText().toString();
        if(validate()) {
            progressDialog.setMessage("Checking credentials, just a moment....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        progressDialog.cancel();
                        finish();
                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog.cancel();
                        Toast.makeText(MainActivity.this, "Login Unsuccessful,Please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean validate() {
        if (email.isEmpty()) {
            Objects.requireNonNull(filledTextFieldEmail.getEditText()).setError("Please enter your email id");
            Objects.requireNonNull(filledTextFieldEmail.getEditText()).requestFocus();
            return false;
        } else if (password.isEmpty()) {
            Objects.requireNonNull(filledTextFieldPwd.getEditText()).setError("Enter your password");
            Objects.requireNonNull(filledTextFieldPwd.getEditText()).requestFocus();;
            return false;
        }
        return true;
    }

    public void resetPassword (final BottomSheetDialog bottomSheetDialog)
    {
        String email= Objects.requireNonNull(editTextEmail.getEditText()).getText().toString();

        if(email.isEmpty())
        {
            editTextEmail.getEditText().setError("Please enter your email id");
            editTextEmail.getEditText().requestFocus();;
        }
        else {
            bottomSheetDialog.setCancelable(false);
            btnSendResetEmail.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if(bottomSheetDialog.isShowing())
                                    bottomSheetDialog.cancel();
                                Toast.makeText(MainActivity.this, "Reset Link Sent to Email", Toast.LENGTH_LONG).show();
                            }
                            else{
                                progressBar.setVisibility(View.GONE);

                                btnSendResetEmail.setVisibility(View.VISIBLE);
                                bottomSheetDialog.setCancelable(true);
                                Toast.makeText(MainActivity.this, "Some error occurred, Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    public void openForgotPasswordBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this
                , R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.bottom_sheet_send_reset_email,
                        (LinearLayout) findViewById(R.id.linearLayoutBottomSheetProfileContainer));
        editTextEmail = bottomSheetView.findViewById(R.id.editTextEmail);
        progressBar = bottomSheetView.findViewById(R.id.progress_bar);
        btnSendResetEmail = bottomSheetView.findViewById(R.id.btnSendResetEmail);
        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(bottomSheetDialog);
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


}