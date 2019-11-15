package com.example.usmansh.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class SingleBlogAct extends AppCompatActivity {

    private  String mPost_key;
    private DatabaseReference mDatabase;
    private TextView mBlogSingleTitle;
    private TextView mBlogSingleDesc;
    private ImageView mBlogSingleImage;
    private Button mSingleRemoveBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog);

        mBlogSingleTitle = (TextView)findViewById(R.id.singleTitle);
        mBlogSingleDesc  = (TextView)findViewById(R.id.singleDesc);
        mBlogSingleImage = (ImageView)findViewById(R.id.singleImg);
        mSingleRemoveBtn = (Button)findViewById(R.id.singleRemoveBtn);
        mProgressDialog  = new ProgressDialog(this);
        mSingleRemoveBtn.setVisibility(View.INVISIBLE);


        mPost_key = getIntent().getExtras().getString("blog-id");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth = FirebaseAuth.getInstance();

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_titlee = (String) dataSnapshot.child("tiles").getValue();
                String post_descc  = (String) dataSnapshot.child("descriptions").getValue();
                String post_image = (String) dataSnapshot.child("images").getValue();
                String user_id    = (String) dataSnapshot.child("userID").getValue();

                mBlogSingleTitle.setText(post_titlee);
                mBlogSingleDesc.setText(post_descc);
                Picasso.with(getApplicationContext()).load(post_image).into(mBlogSingleImage);

                if(mAuth.getCurrentUser().getUid().equals(user_id)){

                    mSingleRemoveBtn.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mProgressDialog.setMessage("Deleting Post..!");
                mProgressDialog.show();

                mDatabase.child(mPost_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mProgressDialog.dismiss();
                        Intent mainAct = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(mainAct);

                    }
                });
            }
        });




     }
}
