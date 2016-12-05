package de.sven.mps_mobilepasswordsafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sven on 20.10.15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "PasswordManager";

    private static final String TABLE_METADATEN = "metadaten";
    private static final String METADATEN_ID = "id";
    private static final String METADATEN_SITE = "site";
    private static final String METADATEN_USERNAME = "username";
    private static final String METADATEN_PASSWORD = "password";

    private static final String TABLE_MASTERPW = "masterpw";
    private static final String MASTERPW_ID = "id";
    private static final String MASTERPW_PW = "pw";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_METADATEN_TABLE = "CREATE TABLE " + TABLE_METADATEN + "("
                + METADATEN_ID + " INTEGER PRIMARY KEY," + METADATEN_SITE + " TEXT,"
                + METADATEN_USERNAME + " TEXT," + METADATEN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_METADATEN_TABLE);

        String CREATE_MASTERPW_TABLE = "CREATE TABLE " + TABLE_MASTERPW + "("
                + MASTERPW_ID + " INTEGER PRIMARY KEY," + MASTERPW_PW + " TEXT" + ")";
        db.execSQL(CREATE_MASTERPW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METADATEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASTERPW);
        // Create tables again
        onCreate(db);
    }


    SecretKeySpec getsecretKeySpec(String masterpw) {
        byte[] key = new byte[0];
        try {
            key = (masterpw).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        key = sha.digest(key);

        key = Arrays.copyOf(key, 16);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        return secretKeySpec;
    }

    String verschluesseln(String passwort, String masterpw)  {
        Cipher cipher = null;
        String geheim = "";
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getsecretKeySpec(masterpw));
            byte[] encrypted = cipher.doFinal(passwort.getBytes());



            geheim = Base64.encodeToString(encrypted,64);



        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return geheim;

    }

    String entschluesseln(String passwort, String masterpw)  {

        byte[] crypted2 = Base64.decode(passwort, 64);
        Cipher cipher2 = null;
        String erg = "";
        try {
            cipher2 = Cipher.getInstance("AES");
            cipher2.init(Cipher.DECRYPT_MODE, getsecretKeySpec(masterpw));
            byte[] cipherData2 = cipher2.doFinal(crypted2);
            erg = new String(cipherData2);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return erg;
    }

    // Adding new contact
    void addMetadaten(Metadaten metadaten, String master) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(METADATEN_SITE, metadaten.get_site());
        values.put(METADATEN_USERNAME, metadaten.get_username());
        values.put(METADATEN_PASSWORD, verschluesseln(metadaten.get_password(),master));

        // Inserting Row
        db.insert(TABLE_METADATEN, null, values);
        db.close();
    }

    void addMasterpw(String master, String IMEI) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(MASTERPW_ID, 0);
        values.put(MASTERPW_PW, verschluesseln(master,IMEI));


        // Inserting Row
        db.insert(TABLE_MASTERPW, null, values);
        db.close();
    }

    Masterpw getMasterpw(int id, String IMEI) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MASTERPW, new String[] {MASTERPW_ID, MASTERPW_PW }, MASTERPW_ID + "=?",
                new String[] { String.valueOf(id) }, null,null,null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Masterpw masterpw = new Masterpw(Integer.parseInt(cursor.getString(0)), entschluesseln(cursor.getString(1), IMEI));
        // return contact
        return masterpw;
    }

    // Getting single contact
    Metadaten getMetadaten(int id, String masterpw) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_METADATEN, new String[] { METADATEN_ID,
                        METADATEN_SITE, METADATEN_USERNAME, METADATEN_PASSWORD }, METADATEN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Metadaten metadaten = new Metadaten(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), entschluesseln(cursor.getString(3), masterpw));
        // return contact
        return metadaten;
    }

    Metadaten getMetadaten(String Site, String masterpw) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_METADATEN, new String[] { METADATEN_ID,
                        METADATEN_SITE, METADATEN_USERNAME, METADATEN_PASSWORD }, METADATEN_SITE + "=?",
                new String[] { String.valueOf(Site) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Metadaten metadaten = new Metadaten(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), entschluesseln(cursor.getString(3), masterpw));
        // return contact
        return metadaten;
    }

    // Getting All Contacts
    public ArrayList<Metadaten> getAllMetadaten(String masterpw) {
        ArrayList<Metadaten> contactList = new ArrayList<Metadaten>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_METADATEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Metadaten metadaten = new Metadaten();
                metadaten.set_id(Integer.parseInt(cursor.getString(0)));
                metadaten.set_site(cursor.getString(1));
                metadaten.set_username(cursor.getString(2));
                metadaten.set_password(entschluesseln(cursor.getString(3), masterpw));
                // Adding contact to list
                contactList.add(metadaten);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateMetadaten(Metadaten metadaten, String pw) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(METADATEN_SITE, metadaten.get_site());
        values.put(METADATEN_USERNAME, metadaten.get_username());
        values.put(METADATEN_PASSWORD, verschluesseln(metadaten.get_password(),pw));
        // updating row
        return db.update(TABLE_METADATEN, values, METADATEN_ID + " = ?",
                new String[] { String.valueOf(metadaten.get_id()) });
    }

    // Deleting single contact
    public void deleteMetadaten(Metadaten metadaten) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_METADATEN, METADATEN_ID + " = ?",
                new String[] { String.valueOf(metadaten.get_id()) });
        db.close();
    }


    // Getting contacts Count
    public int getMetadatenCount() {
        String countQuery = "SELECT  * FROM " + TABLE_METADATEN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
