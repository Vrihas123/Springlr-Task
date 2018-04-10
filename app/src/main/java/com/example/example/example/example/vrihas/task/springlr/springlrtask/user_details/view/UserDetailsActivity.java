package com.example.example.example.example.vrihas.task.springlr.springlrtask.user_details.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.example.example.example.vrihas.task.springlr.springlrtask.profile.ProfileActivity;
import com.example.example.example.example.vrihas.task.springlr.springlrtask.R;
import com.example.example.example.example.vrihas.task.springlr.springlrtask.helper.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDetailsActivity extends AppCompatActivity {

    private EditText inputUserName, inputUserMobile,inputUserEmail,inputUserAddress;
    private TextInputLayout inputLayoutUserName, inputLayoutUserMobile,inputLayoutUserEmail,inputLayoutUserAddress;
    private Button btn_submit;
    private String name,mobile,email,address,userId;
    private SharedPrefs sharedPrefs;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        sharedPrefs = new SharedPrefs(this);

        inputLayoutUserName = (TextInputLayout) findViewById(R.id.input_layout_user_name);
        inputLayoutUserMobile = (TextInputLayout) findViewById(R.id.input_layout_user_mobile);
        inputLayoutUserEmail = (TextInputLayout) findViewById(R.id.input_layout_user_email);
        inputLayoutUserAddress = (TextInputLayout) findViewById(R.id.input_layout_user_address);
        inputUserName = (EditText) findViewById(R.id.input_user_name);
        inputUserEmail = (EditText) findViewById(R.id.input_user_email);
        inputUserMobile = (EditText) findViewById(R.id.input_user_mobile);
        inputUserAddress = (EditText) findViewById(R.id.input_user_address);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        if (mFirebaseUser == null){
//            Toast.makeText(this,"Not Signed in",Toast.LENGTH_LONG).show();
//        }


//        authlistener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//
//                } else {
//                    // User is signed out
//
//                }
//                // ...
//            }
//        };
//        FirebaseAuth.getInstance().addAuthStateListener(authListener);



        inputUserName.addTextChangedListener(new MyTextWatcher(inputUserName));
        inputUserMobile.addTextChangedListener(new MyTextWatcher(inputUserMobile));
        inputUserEmail.addTextChangedListener(new MyTextWatcher(inputUserEmail));
        inputUserAddress.addTextChangedListener(new MyTextWatcher(inputUserAddress));

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    public void submit(){
        hideKeyboard();
        if (!validateName()){
            return;
        }
        if (!validateMobile()){
            return;
        }
        if (!validateEmail()){
            return;
        }
        if (!validateAddress()){
            return;
        }
        sharedPrefs.setUsername(name);
        sharedPrefs.setMobile(mobile);
        sharedPrefs.setEmailId(email);
        sharedPrefs.setAddress(address);
       // createUserWithEmailAndPassword();
        writeNewUser(name,mobile,email,address);
        Toast.makeText(this,"Registration Successfull, proceeding to login page",Toast.LENGTH_LONG).show();
        Intent i = new Intent(UserDetailsActivity.this, ProfileActivity.class);
        startActivity(i);
        finish();
    }

    public void writeNewUser(String name, String mobile, String email, String address){
       // userId = mFirebaseAuth.getCurrentUser().getUid();
        userId = mFirebaseDatabase.push().getKey();
       // userId = mobile;
        Toast.makeText(UserDetailsActivity.this,"userId = "+userId,Toast.LENGTH_LONG).show();
        UserInformation userInformation = new UserInformation(name,mobile,email,address);
        mFirebaseDatabase.child(userId).setValue(userInformation);


    }
    private void createUserWithEmailAndPassword(){
        mFirebaseAuth.createUserWithEmailAndPassword(email, mobile)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                          //  Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                          //  Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserDetailsActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private boolean validateName(){
        name = inputUserName.getText().toString().trim();
        if (name.isEmpty()){
            inputLayoutUserName.setError(getString(R.string.err_msg_name));
            requestFocus(inputUserName);
            return false;
        }else {
            inputLayoutUserName.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateMobile(){
        mobile = inputUserMobile.getText().toString().trim();
        if (mobile.isEmpty()){
            inputLayoutUserMobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(inputUserMobile);
            return false;
        }else if (mobile.length()!=10){
            inputLayoutUserMobile.setError(getString(R.string.err_msg_mobile_digits));
            requestFocus(inputUserMobile);
            return false;
        }else {
            inputLayoutUserMobile.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail(){
        email = inputUserEmail.getText().toString().trim();
        if (email.isEmpty()){
            inputLayoutUserEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputUserEmail);
            return false;
        }else if(emailInvalid(email)) {
            inputLayoutUserEmail.setError("Entered email is not a valid email");
            requestFocus(inputUserEmail);
            return false;
        }else {
            inputLayoutUserEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress(){
        address = inputUserAddress.getText().toString().trim();
        if (address.isEmpty()){
            inputLayoutUserAddress.setError(getString(R.string.err_msg_address));
            requestFocus(inputUserAddress);
            return false;
        }else {
            inputLayoutUserAddress.setErrorEnabled(false);
        }
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean emailInvalid(String email) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        boolean a = matcher.matches();
        return !a;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_user_name:
                    validateName();
                    break;
                case R.id.input_user_mobile:
                    validateMobile();
                    break;
                case R.id.input_user_email:
                    validateEmail();
                    break;
                case R.id.input_user_address:
                    validateAddress();
                    break;
            }
        }
    }
}
