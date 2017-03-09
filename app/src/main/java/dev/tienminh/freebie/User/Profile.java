package dev.tienminh.freebie.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import dev.tienminh.freebie.Dashboard;
import dev.tienminh.freebie.R;

public class Profile extends AppCompatActivity {

    private ImageButton img_avatar;
    private EditText edt_displayName;
    private Button btn_save;
    private Uri imageUri = null;
    private DatabaseReference dbUser;
    private FirebaseAuth auth;
    private StorageReference store;
    private static final int GALLER_REQUEST_CODE = 888;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        dbUser= FirebaseDatabase.getInstance().getReference().child("Users");
        store = FirebaseStorage.getInstance().getReference().child("Profile_image");

        img_avatar = (ImageButton) findViewById(R.id.img_avatar);
        edt_displayName= (EditText) findViewById(R.id.edt_displayName);
        btn_save = (Button) findViewById(R.id.btn_save);
        dialog = new ProgressDialog(this);

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLER_REQUEST_CODE);

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingacc();
            }
        });
    }
    private void savingacc() {

        final String name = edt_displayName.getText().toString().trim();
        final String user_id = auth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(name)&&imageUri!=null){
            dialog.setMessage("Saving data...");
            dialog.show();
            StorageReference filepath = store.child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    dbUser.child(user_id).child("name").setValue(name);
                    dbUser.child(user_id).child("image").setValue(downloadUri);

                    dialog.dismiss();

                    Intent iDashboard = new Intent(Profile.this, Dashboard.class);
                    iDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iDashboard);

                }
            });



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLER_REQUEST_CODE&&resultCode==RESULT_OK){
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                img_avatar.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
