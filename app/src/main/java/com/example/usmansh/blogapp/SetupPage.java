package com.example.usmansh.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class SetupPage extends AppCompatActivity {

    private ImageButton SetupprofilePic;

    private EditText SetupnameField;

    private Button   SetupsubmitBtn;

    private Uri mImageUri;

    private static final int GALLRY_REQUES_CODE = 1;

    private DatabaseReference mDatabaseRef;

    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_page);

        SetupprofilePic = (ImageButton)findViewById(R.id.SetupProfile_pic);
        SetupnameField  = (EditText)findViewById(R.id.SetupnameField);
        SetupsubmitBtn  = (Button)findViewById(R.id.SetupsubmitBtn);
        mProgressDialog = new ProgressDialog(this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");



        SetupsubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSetupAccount();
            }
        });





        SetupprofilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallryIntent = new Intent();
                gallryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallryIntent.setType("image/*");
                startActivityForResult(gallryIntent,GALLRY_REQUES_CODE);

            }
        });

    }





    private void startSetupAccount() {

        final String setName = SetupnameField.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(setName) && mImageUri != null){

            mProgressDialog.setMessage("Creating Account..!");
            mProgressDialog.show();

            final StorageReference filepath  = mStorageRef.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloaduri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseRef.child(user_id).child("name").setValue(setName);
                    mDatabaseRef.child(user_id).child("images").setValue(downloaduri);

                    mProgressDialog.dismiss();

                    Intent MainActivity = new Intent(getApplicationContext(),MainActivity.class);
                    MainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(MainActivity);

                }
            });




        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == GALLRY_REQUES_CODE && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();
                SetupprofilePic.setImageURI(mImageUri);

            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}
