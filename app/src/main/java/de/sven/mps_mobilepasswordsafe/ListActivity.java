package de.sven.mps_mobilepasswordsafe;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.sven.mps_mobilepasswordsafe.server.PWServer;

public class ListActivity extends AppCompatActivity  {

    static final int ADD_NEW_METADATA = 1;
    static final int SHOW_METADATA = 2;
    public static final String PREFS_NAME = "PW_Cache";
    AlertDialog.Builder builder1;
    private PWServer server;
    private String master_key;

    ListView mListView;
    ArrayList<Metadaten> metadaten;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = new DatabaseHandler(this);


    }

    @Override
    public void onStart() {
        super.onStart();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        builder1 = new AlertDialog.Builder(this);
        if(master_key == null) {
            final EditText edittext = new EditText(this);

            edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());

            final AlertDialog alert = new AlertDialog.Builder(this)
            .setMessage(R.string.ListSetMessage)
            .setTitle(R.string.Pas)
            .setView(edittext)
            .setPositiveButton(R.string.Okay, null) //Set to null. We override the onclick
            .setNegativeButton(R.string.Beenden, null)
            .create();

            alert.setOnShowListener(new DialogInterface.OnShowListener(){

                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button c = alert.getButton(AlertDialog.BUTTON_NEGATIVE);

                    c.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            System.exit(0);
                        }
                    });

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            String device_id = tm.getDeviceId();

                            Masterpw master = db.getMasterpw(0, device_id);

                            if (edittext.getText().toString().equals(master.get_password())) {
                                master_key = master.get_password();
                                setList();
                                alert.dismiss();
                            } else {
                                alert.setMessage(getString(R.string.FalschesPW));
                            }
                        }
                    });
                }
            });

            alert.show();

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivityForResult(intent, ADD_NEW_METADATA);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        master_key = null;

    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            server = new PWServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(server != null) {
            server.stop();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NEW_METADATA) {
            if (data != null) {
                DatabaseHandler db = new DatabaseHandler(this);
                db.addMetadaten(new Metadaten(data.getStringExtra("Seite"), data.getStringExtra("User"), data.getStringExtra("PW")), master_key);
                setList();
            }
        }

        if (requestCode == SHOW_METADATA) {
            if (data != null) {
                DatabaseHandler db = new DatabaseHandler(this);
                db.updateMetadaten(new Metadaten(data.getIntExtra("ID",0),data.getStringExtra("Seite"), data.getStringExtra("User"), data.getStringExtra("PW")), master_key);
                setList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();



        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setList() {
        metadaten = db.getAllMetadaten(master_key);

       CustomListAdapter adapter =  new CustomListAdapter(this, metadaten);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = mListView.getItemAtPosition(position);
                Metadaten meta = (Metadaten) o;


                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("ID", meta.get_id());
                intent.putExtra("Seite", meta.get_site());
                intent.putExtra("User", meta.get_username());
                intent.putExtra("PW", meta.get_password());
                startActivityForResult(intent, SHOW_METADATA);
            }
        });



        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                final int position = pos;

                builder1.setMessage("Löschen?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ja",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Object o = mListView.getItemAtPosition(position);
                                Metadaten meta = (Metadaten) o;
                                db.deleteMetadaten(meta);
                                setList();

                            }
                        });
                builder1.setNegativeButton("Nein",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();



                return true;
            }

        });









        adapter.notifyDataSetChanged();
        /*adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String arg1, String arg0) {
                return arg1.compareTo(arg0);
            }
        });*/
    }
}
