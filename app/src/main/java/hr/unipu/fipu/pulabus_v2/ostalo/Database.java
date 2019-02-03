package hr.unipu.fipu.pulabus_v2.ostalo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa Database - definira sve metode vezane za komunikaciju s bazom podataka
 */
public class Database extends SQLiteOpenHelper {

    // definiran put do baze
    private String DB_PATH = "/data/data/hr.unipu.fipu.pulabus_v2/databases/";
    // ime baze
    private static String DB_NAME = "bus_db.db";
    // verzija baze
    private static int DB_VERSION = 1;
    // instanca klase SQLiteDatabase
    private SQLiteDatabase database;
    // kontekst u kojem se koristi baza
    private Context context;

    // konstruktor s argumentom kontekst
    public Database(Context context_arg) {
        super(context_arg, DB_NAME, null, DB_VERSION);
        this.context = context_arg;
    }

    // metoda za otvaranje baze, poziva metode iz klase SQliteDatabase
    public void openDatabase(){
        String myPath = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // metoda koja provjerava nalazi li se baza na uredaju
    private boolean checkDatabase(){
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e){
            System.out.println("Can't open Database");
        }
        if (checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    // metoda koja kopira bazu na uredaj
    public void copyDatabase(){
        try {
            InputStream inputStream = context.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH + DB_NAME;
            OutputStream outputStream = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int lenght;
            // dok ima podataka u bazi, kopiraj ih u outputStream
            while ((lenght = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, lenght);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            System.out.println("Can't copy Database");
        }
    }

    // metoda za kreiranje baze podataka
    public void createDatabase() throws IOException {
        boolean isExist = checkDatabase();
        if (isExist){
            System.out.println("Database exists");
        } else {
            System.out.println("Database don't exists");
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (Exception e){
                System.out.println("Can't copy Database");
            }
        }
    }

    // nadjacavanje metode close za zatvaranje baze
    @Override
    public synchronized void close(){
        if (database != null){
            database.close();
        }
        super.close();
    }

    // metoda za nazive linija
    public List<String> getLinije(){
        // lista u koju se spremaju rezultati
        List<String> list = new ArrayList<String>();
        // instanca SQLiteDatabase baze samo za citanje iz iste
        SQLiteDatabase db = this.getReadableDatabase();
        // kursor koji pokazuje na element koji se cita
        Cursor c;

        try {
            // definiranje upita
            c = db.rawQuery("select naziv from linije", null);
            if (c == null) {
                return null;
            }

            // varijabla za trenutni rezultat
            String name;
            // postavi kursor na pocetak
            c.moveToFirst();

            // dok se kursor moze pomaknuti (dok ima u bazi), spremaj u name vrijednost elementa na koji kursor pokazuje
            do {
                name = c.getString(c.getColumnIndex("naziv"));
                // dodaj vrijednost u listu
                list.add(name);
            } while (c.moveToNext());
            // zatvori kursor
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        // zatvori bazu
        db.close();
        // vrati listu
        return list;

    }

    // metoda za duzi naziv linije
    public List<String> getNazivDuzi(){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        try {
            c = db.rawQuery("select duzi_naziv from linije", null);
            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            do {
                name = c.getString(c.getColumnIndex("duzi_naziv"));
                list.add(name);
            } while (c.moveToNext());

            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;

    }

    // metoda za naziv stanice, argumenti koji su potrebni su naziv linije i mjesto polaska iste
    public List<String> getStanice(String nazivLinije, String mjestoPolaska){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije, mjestoPolaska};
        try {
            c = db.rawQuery("select naziv from stanice where naziv_linije = ? and mjesto_polaska = ?", args);

            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            do{
                name = c.getString(c.getColumnIndex("naziv"));
                list.add(name);
            } while (c.moveToNext());

            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    // metoda za ubacivanje u bazu u tablicu omiljeno
    public void insertToOmiljenoStanice(String nazivLinije, String nazivStanice, String mjestoPolaska){
        // instanca SQLiteDatabase baze za pisanje u nju
        SQLiteDatabase db = this.getWritableDatabase();
        // argumenti koji se prosljeduju naredbi za ubacivanje
        ContentValues contentValues = new ContentValues();

        // argumenti se dodaju u contentValues, prvi arg je ime kolone u tablici, drugi je prosljedeni arg
        contentValues.put("nazivLinije", nazivLinije);
        contentValues.put("nazivStanice", nazivStanice);
        contentValues.put("mjestoPolaska", mjestoPolaska);

        // naredba za ubacivanje
        db.insert("omiljeno", null, contentValues);
        // zatvara bazu
        db.close();
    }

    // metoda za brisanje iz baze iz tablice omiljeno
    public Integer deleteFromOmiljenoStanice(String nazivLinije, String nazivStanice, String mjestoPolaska){
        // instanca SQLiteDatabase baze za pisanje u nju
        SQLiteDatabase db = this.getWritableDatabase();
        // naredba za brisanje iz baze
        // argumenti: tablica u bazi, naziv kolona, prosljedeni argumenti
        return db.delete("omiljeno", "nazivLinije = ? and nazivStanice = ? and mjestoPolaska = ?", new String[] {nazivLinije, nazivStanice, mjestoPolaska});
    }

    // metoda za naziv linije iz tablice omiljeno
    public List<String> getOmiljenoLinije(){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        try {
            c = db.rawQuery("select nazivLinije from omiljeno", null);
            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            if (c != null && c.moveToFirst()){
                do {
                    name = c.getString(c.getColumnIndex("nazivLinije"));
                    list.add(name);
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    // metoda za naziv stanice iz tablice omiljeno
    public List<String> getOmiljenoStanice(){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        try {
            c = db.rawQuery("select nazivStanice from omiljeno", null);
            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            if (c != null && c.moveToFirst()){
                do {
                    name = c.getString(c.getColumnIndex("nazivStanice"));
                    list.add(name);
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    // metoda za mjesto polaska iz tablice omiljeno
    public List<String> getOmiljenoMjestoPolaska(){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        try {
            c = db.rawQuery("select mjestoPolaska from omiljeno", null);
            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            if (c != null && c.moveToFirst()){
                do {
                    name = c.getString(c.getColumnIndex("mjestoPolaska"));
                    list.add(name);
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    // metoda za mjesto polaska 1 iz tablice linije
    public String getMjestoPolaska1(String nazivLinije){
        String rezz = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije};
        try {
            c = db.rawQuery("select mjesto_polaska_1 from linije where naziv = ?", args);
            if (c == null){
                return null;
            }

            c.moveToFirst();
            rezz = c.getString(c.getColumnIndex("mjesto_polaska_1"));
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return rezz;

    }

    // metoda za mjesto polaska 2 iz tablice linije
    public String getMjestoPolaska2(String nazivLinije){
        String rezz = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije};
        try {
            c = db.rawQuery("select mjesto_polaska_2 from linije where naziv = ?", args);
            if (c == null){
                return null;
            }

            c.moveToFirst();
            rezz = c.getString(c.getColumnIndex("mjesto_polaska_2"));
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return rezz;

    }

    // metoda za vrijeme iz tablice vrijemeZimsko
    public List<String> getVrijemePolaska(String nazivLinije, String mjestoPolaska, String dan){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije, mjestoPolaska, dan};
        try {
            c = db.rawQuery("select vrijeme from vrijemeZimsko where naziv_linije = ? and mjesto_polaska = ? and dan = ?", args);
            if (c == null) {
                return null;
            }

            String vrijeme;
            c.moveToFirst();

            if (c != null && c.moveToFirst()){
                do {
                    vrijeme = c.getString(c.getColumnIndex("vrijeme"));
                    list.add(vrijeme);
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    // metoda za stanice iz tablice stanice
    public List<String> getStaniceForVrijeme(String nazivLinije, String mjestoPolaska){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije, mjestoPolaska};
        try {
            c = db.rawQuery("select naziv from stanice where naziv_linije = ? and mjesto_polaska = ? order by broj_stanice ASC", args);

            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            do{
                name = c.getString(c.getColumnIndex("naziv"));
                list.add(name);
            } while (c.moveToNext());

            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    // metoda za vrijeme dolaska iz tablice stanice
    public List<String> getVrijemeDolaska(String nazivLinije, String mjestoPolaska){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije, mjestoPolaska};
        try {
            c = db.rawQuery("select vrijeme_dolaska from stanice where naziv_linije = ? and mjesto_polaska = ? order by broj_stanice ASC", args);

            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            do{
                name = c.getString(c.getColumnIndex("vrijeme_dolaska"));
                list.add(name);
            } while (c.moveToNext());

            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    public String getLinijaForMap(String sadrzaj){
        String rezz = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {sadrzaj};
        try {
            c = db.rawQuery("select naziv_linije from stanice where dodatno = ?", args);
            if (c == null) {
                return null;
            }

            c.moveToFirst();

            if (c != null && c.moveToFirst()){
                do {
                    rezz = c.getString(c.getColumnIndex("naziv_linije"));
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return rezz;
    }

    public String getMjestoPolaskaForMap(String sadrzaj){
        String rezz = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {sadrzaj};
        try {
            c = db.rawQuery("select mjesto_polaska from stanice where dodatno = ?", args);
            if (c == null) {
                return null;
            }

            c.moveToFirst();

            if (c != null && c.moveToFirst()){
                do {
                    rezz = c.getString(c.getColumnIndex("mjesto_polaska"));
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return rezz;
    }

    public List<String> getLatitude(String nazivLinije, String mjestoPolaska){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije, mjestoPolaska};
        try {
            c = db.rawQuery("select lat from stanice where naziv_linije = ? and mjesto_polaska = ? order by broj_stanice ASC", args);

            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            do{
                name = c.getString(c.getColumnIndex("lat"));
                list.add(name);
            } while (c.moveToNext());

            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    public List<String> getLongitude(String nazivLinije, String mjestoPolaska){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije, mjestoPolaska};
        try {
            c = db.rawQuery("select lng from stanice where naziv_linije = ? and mjesto_polaska = ? order by broj_stanice ASC", args);

            if (c == null) {
                return null;
            }

            String name;
            c.moveToFirst();

            do{
                name = c.getString(c.getColumnIndex("lng"));
                list.add(name);
            } while (c.moveToNext());

            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return list;
    }

    //

    public String getImeStaniceSadrzaj(String sadrzaj){
        String rezz = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {sadrzaj};
        try {
            c = db.rawQuery("select naziv from stanice where dodatno = ?", args);

            if (c == null) {
                return null;
            }

            c.moveToFirst();

            do{
                rezz = c.getString(c.getColumnIndex("naziv"));
            } while (c.moveToNext());

            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return rezz;
    }

    public String getVrijemeDoslskaForRealTime(String nazivLinije, String mjestoPolaska, String stanica){
        String rezz = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;

        String[] args = {nazivLinije, mjestoPolaska, stanica};
        try {
            c = db.rawQuery("select vrijeme_dolaska from stanice where naziv_linije = ? and mjesto_polaska = ? and naziv = ?", args);
            if (c == null) {
                return null;
            }

            c.moveToFirst();

            if (c != null && c.moveToFirst()){
                do {
                    rezz = c.getString(c.getColumnIndex("vrijeme_dolaska"));
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        return rezz;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
