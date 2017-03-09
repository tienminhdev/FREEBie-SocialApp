package dev.tienminh.freebie.Blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import dev.tienminh.freebie.Dashboard;
import dev.tienminh.freebie.R;

public class PostDetail extends AppCompatActivity {

    private String postKey = null;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private ImageView imgSinglePost;
    private TextView txtSingleDesc;
    private Button btn_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        auth = FirebaseAuth.getInstance();

        imgSinglePost = (ImageView) findViewById(R.id.img_Singlepost);
        txtSingleDesc = (TextView) findViewById(R.id.txt_Singledesc);
        btn_remove= (Button) findViewById(R.id.btn_remove);

        db = FirebaseDatabase.getInstance().getReference().child("Blog");

        postKey = getIntent().getExtras().getString("blog_id");

        db.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                txtSingleDesc.setText(post_desc);
                Picasso.with(getApplicationContext()).load(post_image).into(imgSinglePost);

                if (auth.getCurrentUser().getUid().equals(post_uid)){
                    btn_remove.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Toast.makeText(this, ""+postKey, Toast.LENGTH_SHORT).show();
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.child(postKey).removeValue();
                startActivity(new Intent(PostDetail.this, Dashboard.class));
                finish();
            }
        });
    }
}
