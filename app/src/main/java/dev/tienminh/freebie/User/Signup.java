package dev.tienminh.freebie.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.tienminh.freebie.R;

public class Signup extends AppCompatActivity {

    EditText edt_name;
    EditText edt_email;
    EditText edt_password;
    Button btn_sigup;
    Button btn_signin;
    Button btn_reset;
    Button btn_Login;

    ProgressDialog dialog;
    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_sigup = (Button) findViewById(R.id.sign_up_button);
        btn_signin = (Button) findViewById(R.id.sign_in_button);
        btn_reset = (Button) findViewById(R.id.btn_reset_password);
        dialog = new ProgressDialog(this);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QuenMatKhau.class));
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
        btn_sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }
    private void signUp() {

        final String name = edt_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6){
            Toast.makeText(this, "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.setMessage("Signing up...");
        dialog.show();

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Toast.makeText(Signup.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (!task.isSuccessful()){
                    Toast.makeText(Signup.this, "Authentication failed." + task.getException(),
                            Toast.LENGTH_SHORT).show();
                }else {
                    String user_id = auth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = db.child(user_id);
                    current_user_db.child("name").setValue(name);
                    current_user_db.child("image").setValue("default");

                    Intent iLogin = new Intent(Signup.this, Login.class);
                    iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iLogin);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog.dismiss();
    }
}
