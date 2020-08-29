package com.sjcoders.userregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserSignUpActivity extends AppCompatActivity {

    private int genderPosition = -1;
    private String[] genders;
    private FirebaseAuth firebaseAuth;
    private ArrayAdapter<String> adapter;
    TextInputLayout editTextName, editTextEmail, editTextAddress, editTextPwd, editTextAge,editTextPhoneNo;
    private String name ;
    private String email ;
    private String pwd ;
    private String address ;
    private String age ;
    private String phoneNo;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(UserSignUpActivity.this);
        genders = new String[]{"Male", "Female"};

        adapter = new ArrayAdapter<>(UserSignUpActivity.this, R.layout.dropdown_menu_popup_item, genders);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextAge = findViewById(R.id.editTextAge);
        editTextPwd = findViewById(R.id.editTextPwd);
        editTextPhoneNo = findViewById(R.id.editTextPhoneNo);

        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                genderPosition = position;
            }
        });

        ((Button)findViewById(R.id.btnUserSignUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserAccount();
            }
        });
    }

    public void createUserAccount(){

         name = Objects.requireNonNull(editTextName.getEditText()).getText().toString();
         email = Objects.requireNonNull(editTextEmail.getEditText()).getText().toString();
         pwd = Objects.requireNonNull(editTextPwd.getEditText()).getText().toString();
         address = Objects.requireNonNull(editTextAddress.getEditText()).getText().toString();
         age = Objects.requireNonNull(editTextAge.getEditText()).getText().toString();
         phoneNo = Objects.requireNonNull(editTextPhoneNo.getEditText()).getText().toString();
        if(TextUtils.isEmpty(name)){
            editTextName.getEditText().setError("Field Required");
            editTextName.getEditText().requestFocus();
            return;
        }
        if(TextUtils.isEmpty(email)){
            editTextEmail.getEditText().setError("Field Required");
            editTextEmail.getEditText().requestFocus();
            return;
        }
        if(TextUtils.isEmpty(address)){
            editTextAddress.getEditText().setError("Field Required");
            editTextAddress.getEditText().requestFocus();
            return;
        }
        if(TextUtils.isEmpty(age)){
            editTextAge.getEditText().setError("Field Required");
            editTextAge.getEditText().requestFocus();
            return;
        }
        if(TextUtils.isEmpty(phoneNo)){
            editTextPhoneNo.getEditText().setError("Field Required");
            editTextPhoneNo.getEditText().requestFocus();
            return;
        }
        if(genderPosition==-1){
            Snackbar.make(editTextAge,"Select your gender",Snackbar.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            editTextPwd.getEditText().setError("Field Required");
            editTextPwd.getEditText().requestFocus();
            return;
        }
        if(pwd.length()<3 || pwd.length()>10){
            editTextPwd.getEditText().setError("Password length must be between 3 and 10");
            editTextPwd.getEditText().requestFocus();
            return;
        }

        signUpUser();

    }

    public void signUpUser(){

        progressDialog.setMessage("Creating your account, just a moment....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserSignUpActivity.this,"SignUp Successful",Toast.LENGTH_LONG).show();
                    uploadUserData();


                } else {
                    if(progressDialog.isShowing())
                        progressDialog.cancel();
                    Toast.makeText(UserSignUpActivity.this, "SignUp Unsuccessful!,Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void uploadUserData(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null) {
            progressDialog.setMessage("Uploading your Profile");
            String gender="";
            if(genderPosition==0)
                gender="Male";
            else
                gender="Female";
            ProfileData newUserProfileData = new ProfileData(name,email,phoneNo,address,gender,age);
            firebaseFirestore.collection("UserProfileData")
                    .document(firebaseUser.getUid())
                    .set(newUserProfileData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(progressDialog.isShowing())
                                progressDialog.cancel();
                            Toast.makeText(UserSignUpActivity.this, "Account created successfully, Welcome !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserSignUpActivity.this,HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(progressDialog.isShowing())
                        progressDialog.cancel();
                    Toast.makeText(UserSignUpActivity.this, "SignUp Unsuccessful!,Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            if(progressDialog.isShowing())
                progressDialog.cancel();
            Toast.makeText(UserSignUpActivity.this, "SignUp Unsuccessful!,Please Try Again", Toast.LENGTH_SHORT).show();
        }

    }

}