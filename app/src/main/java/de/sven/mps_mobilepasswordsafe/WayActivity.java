package de.sven.mps_mobilepasswordsafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WayActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // second argument is the default to use if the preference can't be found
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);

        if (!welcomeScreenShown) {

                    Intent intent = new Intent(getApplicationContext(), SetUpActivity.class);
                    startActivityForResult(intent, 1);

        }

        else {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                DatabaseHandler db = new DatabaseHandler(this);
                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String device_id = tm.getDeviceId();

                db.addMasterpw(data.getStringExtra("PW"),device_id);

                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(welcomeScreenShownPref, true);
                editor.commit(); // Very important to save the preference
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);

            }
        }


    }
}
