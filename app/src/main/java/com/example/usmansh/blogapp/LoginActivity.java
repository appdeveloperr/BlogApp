package com.example.usmansh.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailField;
    private EditText loginPasswordField;
    private Button   loginBtn;
    private Button   newAccountBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);


        mProgressDialog = new ProgressDialog(this);

        loginEmailField = (EditText)findViewById(R.id.loginEmailField);
        loginPasswordField = (EditText)findViewById(R.id.loginPasswordField);

        newAccountBtn = (Button)findViewById(R.id.newAccountBtn);
        loginBtn = (Button)findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLogin();
            }
        });



        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Going to Register or Sign Up

                Intent RegisterActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                RegisterActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(RegisterActivity);

            }
        });




    }//End Main




    private void checkLogin() {

        String email = loginEmailField.getText().toString().trim();
        String password = loginPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgressDialog.setMessage("Checking Login..!");
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful()){

                        mProgressDialog.dismiss();
                        checkUserExist();
                    }
                    else{

                        mProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Sign In Error", Toast.LENGTH_SHORT).show();

                    }


                }
            });

        }

    }



    private void checkUserExist() {


        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){

                    Intent MainActivity = new Intent(getApplicationContext(),MainActivity.class);
                    MainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(MainActivity);
                }

                else{

                    Intent SetupActivity = new Intent(getApplicationContext(),SetupPage.class);
                    SetupActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(SetupActivity);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
