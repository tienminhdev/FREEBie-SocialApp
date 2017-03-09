package dev.tienminh.freebie.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.tienminh.freebie.Dashboard;
import dev.tienminh.freebie.R;

public class Login extends AppCompatActivity {

    EditText edt_email;
    EditText edt_password;
    Button btn_login;
    Button btn_signup;
    Button btn_reset_password;

    FirebaseAuth auth;
    DatabaseReference dbUser;
    ProgressDialog dialog;

    SharedPreferences sharedPreferences;
    private static final String myPREFERENT ="MyPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        dbUser = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUser.keepSynced(true);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, Dashboard.class));
            finish();
        }

        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_login= (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_reset_password = (Button) findViewById(R.id.btn_reset_password);
        dialog= new ProgressDialog(this);

        sharedPreferences = getSharedPreferences(myPREFERENT, Context.MODE_PRIVATE);

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QuenMatKhau.class));
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Signup.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {

        String email = edt_email.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();

        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password",password);
        editor.commit();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.setMessage("Checking Login...");
        dialog.show();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    dialog.dismiss();
                    checkUserExits();

                }else {

                    dialog.dismiss();
                    if (password.length()<6){
                        edt_email.setError(getString(R.string.minimum_password));

                    }else {
                        Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void checkUserExits() {

        if (auth.getCurrentUser()!=null) {

            final String user_id = auth.getCurrentUser().getUid();

            dbUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        Intent iDashboard = new Intent(getApplicationContext(), Dashboard.class);
                        iDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(iDashboard);

                    } else {
                        Intent iSetup = new Intent(getApplicationContext(), Dashboard.class);
                        iSetup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(iSetup);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
