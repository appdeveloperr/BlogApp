package com.example.usmansh.blogapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ui.idp.AuthMethodPickerActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity {

    private RecyclerView blogList;
    private DatabaseReference mDatabase;
    public DatabaseReference mDatabaseUsers;
    public DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQuerryCurrentUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                if(firebaseAuth.getCurrentUser() == null){

                    //Not Loged In
                    Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
                    loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginActivity);
                }
            }
        };



        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Blog");
        String currentUserId = mAuth.getCurrentUser().getUid();

        mQuerryCurrentUser = mDatabaseCurrentUser.orderByChild("userID").equalTo(currentUserId);





        //mDatabase.keepSynced(true);
        //mDatabaseUsers.keepSynced(true);
        //mDatabaseLike.keepSynced(true);



        blogList = (RecyclerView) findViewById(R.id.blogList);
        blogList.setHasFixedSize(true);
        blogList.setLayoutManager(new LinearLayoutManager(this));

        checkUserExistt();

    }



    @Override
    protected void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);


        FirebaseRecyclerAdapter<Blog,BlogviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogviewHolder>(
                Blog.class,
                R.layout.blogrow,
                BlogviewHolder.class,
                mQuerryCurrentUser
        ) {
            @Override
            protected void populateViewHolder(BlogviewHolder viewHolder, Blog model, final int position) {

                final String post_key = getRef(position).getKey();


                viewHolder.setTile(model.getTiles());
                viewHolder.setDesc(model.getDescriptions());
                viewHolder.setImage(getApplicationContext(),model.getImages());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setDate();
                viewHolder.setPubimg(getApplicationContext(),"https://firebasestorage.googleapis.com/v0/b/blogapp-8910d.appspot.com/o/Profile%20Pictures%2Fcropped-143744368.jpg?alt=media&token=736a9577-207d-4801-8788-bd3dc0a1a924");
                viewHolder.setLikeBtn(post_key);


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(MainActivity.this,post_key, Toast.LENGTH_SHORT).show();

                        Intent SingleBlogActivity = new Intent(getApplicationContext(),SingleBlogAct.class);
                        SingleBlogActivity.putExtra("blog-id",post_key);
                        startActivity(SingleBlogActivity);

                    }
                });




                viewHolder.mlikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessLike) {


                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                    } else {

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                        mProcessLike = false;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });



            }
        };


        blogList.setAdapter(firebaseRecyclerAdapter);


    }







    private void checkUserExistt() {


        if(mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent SetupActivity = new Intent(getApplicationContext(), SetupPage.class);
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










    public static class BlogviewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton mlikeBtn;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;



        public BlogviewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            //Applicable for All posts
            mlikeBtn = (ImageButton)mView.findViewById(R.id.likebtn);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);


        }


        public void setLikeBtn(final String post_key){

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        mlikeBtn.setImageResource(R.mipmap.ic_thumb_up_black_24dp);
                    }
                    else{

                        mlikeBtn.setImageResource(R.mipmap.ic_thumb_up_white_24dp);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


            public void setTile(String tile){

                TextView post_title = (TextView) mView.findViewById(R.id.Ptitle);
                post_title.setText(tile);
            }

            public void setDesc (String desc){

                TextView post_desc = (TextView)mView.findViewById(R.id.Pdesc);
                post_desc.setText(desc);
            }


            public void setImage(Context context,String image){

                ImageView post_image  = (ImageView)mView.findViewById(R.id.Pimage);
                Picasso.with(context).load(image).into(post_image);
            }


            public void setUsername(String username){

                TextView post_userrname = (TextView)mView.findViewById(R.id.username);
                post_userrname.setText(username);
            }

            public void setDate (){

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                TextView post_date = (TextView)mView.findViewById(R.id.date);
                post_date.setText(dateFormat.format(date));
            }

            public void setPubimg(Context context,String img){

                ImageView pub_img = (ImageView) mView.findViewById(R.id.publisher_pic);
                Picasso.with(context).load(img).into(pub_img);
            }





    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){

            Intent postAct = new Intent(getApplicationContext(),AddPost.class);
            startActivity(postAct);
        }


        if(item.getItemId() == R.id.action_logout){

            logout();
        }



        return super.onOptionsItemSelected(item);
    }



    private void logout() {


        mAuth.signOut();
    }



}
