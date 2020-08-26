package com.farry.socialapp;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.farry.socialapp.notifications.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

public class DashboardActivity extends AppCompatActivity {

    String newToken="";
    FirebaseAuth mAuth;
    ActionBar actionBar;
    BottomNavigationView bottomNavigationView;
    SinchClient sinchClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        actionBar=getSupportActionBar();
        bottomNavigationView=findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);



    }

    public void updateToken(String token)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token mtoken=new Token(token);
        ref.child(mAuth.getCurrentUser().getUid()).setValue(mtoken);
        Toast.makeText(DashboardActivity.this,"token added",Toast.LENGTH_SHORT).show();

    }

    BottomNavigationView.OnNavigationItemSelectedListener listener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id=menuItem.getItemId();
            if(id==R.id.action_home)
            {
                actionBar.setTitle("Home");

                HomeFragment homeFragment=new HomeFragment();
                FragmentTransaction HomeFragmentTransaction=getSupportFragmentManager().beginTransaction();
                HomeFragmentTransaction.replace(R.id.content,homeFragment,"");
                HomeFragmentTransaction.commit();
                return true;
            }
            else if(id==R.id.action_profile)
            {
                actionBar.setTitle("Profile");

                ProfileFragment profileFragment=new ProfileFragment();
                FragmentTransaction ProfileFT=getSupportFragmentManager().beginTransaction();
                ProfileFT.replace(R.id.content,profileFragment,"");
                ProfileFT.commit();
                return true;
            }
            else if(id==R.id.action_users)
            {
                actionBar.setTitle("Users");
                UsersFragment usersFragment=new UsersFragment();
                FragmentTransaction UsersFT=getSupportFragmentManager().beginTransaction();
                UsersFT.replace(R.id.content,usersFragment,"");
                UsersFT.commit();
                return true;
            }
            else if(id==R.id.action_chat)
            {
                actionBar.setTitle("Chats");
                ChatListFragment chatFragment=new ChatListFragment();
                FragmentTransaction ChatFT=getSupportFragmentManager().beginTransaction();
                ChatFT.replace(R.id.content,chatFragment,"");
                ChatFT.commit();
                return true;
            }

            return false;
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
        {           // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            // Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = currentUser.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = currentUser.getUid();
            updateToken(FirebaseInstanceId.getInstance().getToken());
            SetUpSnitch(uid);
        }

        else
        {
            startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
            finish();
        }

}



    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
    public void SetUpSnitch(String uid)
    {
        android.content.Context context = DashboardActivity.this.getApplicationContext();
        sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey("8f90a149-c8cd-4500-a5a6-3c23d52952a1")
                .applicationSecret("++7LI+9g8kO8Kh6f3l9emg==")
                .environmentHost("clientapi.sinch.com")
                .userId(uid)
                .build();
        // "1" for reciever/callee & "2" for caller
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.setSupportManagedPush(true);
// or
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
    }
}
