package de.sven.mps_mobilepasswordsafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        final Button saveBT = (Button) findViewById(R.id.saveBT);
        final EditText seite = (EditText) findViewById(R.id.pageText);
        final EditText user = (EditText) findViewById(R.id.userText);
        final EditText pw = (EditText) findViewById(R.id.passwortText);
        final int id;


        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        seite.setText(b.getString("Seite"));
        user.setText(b.getString("User"));
        pw.setText(b.getString("PW"));
        id = b.getInt("ID");

        saveBT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("Seite", seite.getText().toString());
                intent.putExtra("User", user.getText().toString());
                intent.putExtra("PW", pw.getText().toString());
                setResult(2, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
