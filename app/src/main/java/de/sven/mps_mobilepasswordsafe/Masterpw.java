package de.sven.mps_mobilepasswordsafe;

/**
 * Created by sven on 20.10.15.
 */
public class Masterpw {

    int _id;
    String _password;

    public Masterpw(){

    }

    public Masterpw(int id,  String password){
        this._id = id;
        this._password = password;
    }




    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String password) {
        this._password = password;
    }
}
