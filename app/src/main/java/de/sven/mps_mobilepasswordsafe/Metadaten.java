package de.sven.mps_mobilepasswordsafe;

/**
 * Created by sven on 20.10.15.
 */
public class Metadaten {

    int _id;
    String _site;
    String _username;
    String _password;

    public Metadaten(){

    }

    public Metadaten(int id, String site, String username, String password){
        this._id = id;
        this._site = site;
        this._username = username;
        this._password = password;
    }

    public Metadaten(String site, String username, String password){
        this._site = site;
        this._username = username;
        this._password = password;
    }


    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public String get_site() {
        return _site;
    }

    public void set_site(String site) {
        this._site = site;
    }

    public String get_username() {
        return _username;
    }

    public void set_username(String username) {
        this._username = username;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String password) {
        this._password = password;
    }
}
