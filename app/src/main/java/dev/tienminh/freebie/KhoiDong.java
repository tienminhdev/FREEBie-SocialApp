package dev.tienminh.freebie;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import dev.tienminh.freebie.User.Login;

public class KhoiDong extends AppCompatActivity {

    TextView txt_random;
    ProgressBar progressBar;
    int [] social = {R.string.random1,R.string.random2,R.string.random3,R.string.random4,R.string.random5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khoi_dong);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_random = (TextView) findViewById(R.id.txt_random);

        txt_random.setShadowLayer(0,0,0, Color.RED);

        Random random = new Random();
        int rd = random.nextInt(social.length);
        txt_random.setText(social[rd]);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);

                if (isConnected()==true){
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    progressBar.setVisibility(View.GONE);
                    finish();

                }else {
                    Toast.makeText(KhoiDong.this, "Your device not connected to internet, please try again!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    finish();
                }

            }
        },2000);
    }
    public boolean isConnected (){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni!=null&&ni.isConnectedOrConnecting()){
            return true;
        }
        return false;
    }
}
