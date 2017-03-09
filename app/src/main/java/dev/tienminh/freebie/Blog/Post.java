package dev.tienminh.freebie.Blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

import dev.tienminh.freebie.Dashboard;
import dev.tienminh.freebie.R;

public class Post extends AppCompatActivity {

    private ImageView img_post;
    private EditText edt_desc;
    private Button btn_post;
    private static final int REQUEST_CODE=888;
    private Uri imgUri=null;
    private StorageReference store;
    private DatabaseReference database;
    private ProgressDialog progess;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        store = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference().child("Blog");
        dbUser =FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        img_post = (ImageView) findViewById(R.id.img_post);
        edt_desc = (EditText) findViewById(R.id.edt_desc);
        btn_post= (Button) findViewById(R.id.btn_post);
        progess= new ProgressDialog(this);

        img_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,REQUEST_CODE);
            }
        });
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progess.setMessage("Posting...");
                progess.show();

                final String desc_val = edt_desc.getText().toString().trim();
                if (!TextUtils.isEmpty(desc_val)&&imgUri !=null){
                    StorageReference filepath = store.child("ImageStore").child(imgUri.getLastPathSegment());
                    filepath.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri downloadUri = taskSnapshot.getDownloadUrl();
                            final DatabaseReference newPost = database.push();
                            dbUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("avatar").setValue(dataSnapshot.child("image").getValue());
                                    newPost.child("desc").setValue(desc_val);
                                    newPost.child("image").setValue(downloadUri.toString());
                                    newPost.child("uid").setValue(user.getUid());
                                    newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(Post.this, Dashboard.class));
                                                finish();
                                            }else {

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            progess.dismiss();

                        }
                    });
                }


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE&&resultCode==RESULT_OK){
            imgUri = data.getData();
            img_post.setImageURI(imgUri);
        }
    }
}
