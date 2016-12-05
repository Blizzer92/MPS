package de.sven.mps_mobilepasswordsafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final Button saveBT = (Button) findViewById(R.id.saveBT);
        final EditText seite = (EditText) findViewById(R.id.pageText);
        final EditText user = (EditText) findViewById(R.id.userText);
        final EditText pw = (EditText) findViewById(R.id.passwortText);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);


        saveBT.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("Seite", seite.getText().toString());
                intent.putExtra("User", user.getText().toString());
                intent.putExtra("PW", pw.getText().toString());
                setResult(1, intent);
                finish();
            }
        });
    }
}
