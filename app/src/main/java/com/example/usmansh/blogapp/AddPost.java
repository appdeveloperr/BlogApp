package com.example.usmansh.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddPost extends AppCompatActivity {

    private ImageButton mSelectImage;
    private EditText mPostTile,mPostDescription;
    private Button mSubmit;
    private Uri  imageUri;
    private ProgressDialog mProgressDialog;
    private DatabaseReference DatabaseStorage;
    private static final int GALLRY_REQUEST_CODE = 2;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mSelectImage = (ImageButton)findViewById(R.id.selectImg);
        mPostTile    = (EditText)findViewById(R.id.titleText);
        mPostDescription = (EditText)findViewById(R.id.DescriptionText);
        mSubmit      = (Button) findViewById(R.id.submit);
        mStorage     = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        DatabaseStorage = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUser   = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();
            }
        });




        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gotoGallry = new Intent(Intent.ACTION_GET_CONTENT);
                gotoGallry.setType("image/*");
                startActivityForResult(gotoGallry,GALLRY_REQUEST_CODE);
            }
        });

    }




//Posting
    private void startPosting() {

        final String title_val = mPostTile.getText().toString().trim();
        final String Description_val = mPostDescription.getText().toString().trim();


        if(!title_val.isEmpty() && !Description_val.isEmpty() && imageUri != null){

            mProgressDialog.setMessage("Uploading Blog..!");
            mProgressDialog.show();
            StorageReference filepath = mStorage.child("Blog_img").child(imageUri.getLastPathSegment());


            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUri = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost = DatabaseStorage.push();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("tiles").setValue(title_val);
                            newPost.child("descriptions").setValue(Description_val);
                            newPost.child("images").setValue(downloadUri.toString());
                            newPost.child("userID").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        startActivity(new Intent (getApplicationContext(),MainActivity.class));
                                        Toast.makeText(AddPost.this, "Successfully Posted", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgressDialog.dismiss();




                }
            });

        }

    }


    //GetImageResult


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLRY_REQUEST_CODE && resultCode == RESULT_OK){

             imageUri = data.getData();

            mSelectImage.setImageURI(imageUri);
        }
    }
}
