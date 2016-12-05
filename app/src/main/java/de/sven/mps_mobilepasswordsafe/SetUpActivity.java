package de.sven.mps_mobilepasswordsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

            final Button setUpBT = (Button) findViewById(R.id.setUpBT);

            final EditText pwText = (EditText) findViewById(R.id.pwText);
            pwText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            setUpBT.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), WayActivity.class);
                    intent.putExtra("PW", pwText.getText().toString());
                    setResult(1, intent);
                    finish();

                }
        });

    }
}
