package com.example.example.example.example.vrihas.task.springlr.springlrtask.profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.example.example.example.vrihas.task.springlr.springlrtask.R;
import com.example.example.example.example.vrihas.task.springlr.springlrtask.helper.SharedPrefs;
import com.example.example.example.example.vrihas.task.springlr.springlrtask.login.LoginActivity;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private Button log_out_btn;
    private Button sign_out_btn;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPrefs sharedPrefs;
    private ProfilePictureView profilePictureView;
    private ImageView backgroundImage,googleProfilePic;
    private TextView userName,userEmail,userMobile,userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_profile);
        Profile profile = Profile.getCurrentProfile();

        sharedPrefs = new SharedPrefs(this);
        log_out_btn = (Button) findViewById(R.id.logout_button);
        sign_out_btn = (Button) findViewById(R.id.sign_out_button);
        profilePictureView = (ProfilePictureView) findViewById(R.id.profile_img);
        backgroundImage = (ImageView) findViewById(R.id.background_img);
        userName = (TextView) findViewById(R.id.user_name);
        userMobile = (TextView) findViewById(R.id.user_mobile);
        userEmail = (TextView) findViewById(R.id.user_email);
        userAddress= (TextView) findViewById(R.id.user_address);
        googleProfilePic = (ImageView) findViewById(R.id.google_profile_img);
        userName.setText(sharedPrefs.getUsername());
        userEmail.setText(sharedPrefs.getEmail());
        userMobile.setText(sharedPrefs.getMobile());
        userAddress.setText(sharedPrefs.getAddress());
        Glide.with(this).load("https://www.hdwallpapers.in/walls/dark_designer-wide.jpg").into(backgroundImage);
        if (sharedPrefs.isFacebookLoggedIn()){
            log_out_btn.setVisibility(View.VISIBLE);
            profilePictureView.setProfileId(profile.getId());
        }else{
            sign_out_btn.setVisibility(View.VISIBLE);
            Glide.with(this).load(sharedPrefs.getPhotoUrl()).into(googleProfilePic);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        log_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                sharedPrefs.setLogin(false);
                sharedPrefs.setFacebookLogIn(false);
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sharedPrefs.setLogin(false);
                        sharedPrefs.setGoogleLogIn(false);
                    }
                });
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}
