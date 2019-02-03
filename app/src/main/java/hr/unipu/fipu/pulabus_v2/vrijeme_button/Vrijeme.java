package hr.unipu.fipu.pulabus_v2.vrijeme_button;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;
import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa za prikaz Vremena dolazaka na stanice
 */
public class Vrijeme extends AppCompatActivity {

    // spinneri za odabir atributa
    private Spinner mSpinnerMjestoPolaska;
    private Spinner mSpinnerDan;
    private Spinner mSpinnerVrijemePolska;

    private TextView mTextView;
    private Database database;

    // varijabla za vrijednost intenta
    private String nazivLinije;

    // adapteri za spinnere
    private ArrayAdapter adapterMjestoPolaska;
    private ArrayAdapter adapterDani;
    private ArrayAdapter adapterVrijemePolaska;

    // liste za vrijednosti iz baze
    private List<String> listaMjestoPolaska;
    private List<String> listaDani;
    private List<String> listaVrijemePolaska;

    private List<String> staniceList;
    private List<String> vrijemeList;
    private List<String> vrijemeIzracunato;

    // varijable za vrijednosti iz baze
    private String mjestoPolaska1;
    private String mjestoPolaska2;

    // varijable za spremanje odabrane vrijednosti spinnera
    private String mjestoPolaska;
    private String dan;
    private String vrijemePolaska;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vrijeme);

        // vrijednost intenta
        nazivLinije = getIntent().getStringExtra("nazivLinije");

        // postavljanje naziva linije u text view
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText(nazivLinije);

        database = new Database(this);

        // recycleView u kojemu se prikazuje sadrzaj klase Vrijeme
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        // definiranje layoutManagera za recycleView
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 1. spinner - sadrzi oba mjesta polaska za odabranu liniju
        mSpinnerMjestoPolaska = (Spinner) findViewById(R.id.spinnerMjestoPolaska);
        mjestoPolaska1 = database.getMjestoPolaska1(nazivLinije);
        mjestoPolaska2 = database.getMjestoPolaska2(nazivLinije);

        // dodavanje mjesta polazaka u listu
        listaMjestoPolaska = new ArrayList<>();
        listaMjestoPolaska.add(mjestoPolaska1);
        listaMjestoPolaska.add(mjestoPolaska2);

        // dodavanje liste u spinner i postavljanje adaptera za prikaz
        adapterMjestoPolaska = new ArrayAdapter<String>(Vrijeme.this, android.R.layout.simple_spinner_item, listaMjestoPolaska);
        adapterMjestoPolaska.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMjestoPolaska.setAdapter(adapterMjestoPolaska);

        // 2. spinner - sadrzi moguce dane za odabranu liniju
        mSpinnerDan = (Spinner) findViewById(R.id.spinnerDan);

        // dodavanje dana u listu
        listaDani = new ArrayList<>();
        listaDani.add("radni");
        listaDani.add("subota");
        listaDani.add("nedjelja");

        // dodavanje liste u spinner i postavljanje adaptera za prikaz
        adapterDani = new ArrayAdapter(Vrijeme.this, android.R.layout.simple_spinner_item, listaDani);
        adapterDani.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDan.setAdapter(adapterDani);

        // 3. spinner - sadrzi vrijeme koje je potrebno do dolaska na stanicu sa mjesta polaska
        mSpinnerVrijemePolska = (Spinner) findViewById(R.id.spinnerVrijemePolaska);
        listaVrijemePolaska = database.getVrijemePolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString(), mSpinnerDan.getSelectedItem().toString());

        // dodavanje liste u spinner i postavljanje adaptera za prikaz
        adapterVrijemePolaska = new ArrayAdapter(Vrijeme.this, android.R.layout.simple_spinner_item, listaVrijemePolaska);
        adapterVrijemePolaska.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerVrijemePolska.setAdapter(adapterVrijemePolaska);

        //

        // lista stanica za odabrano mjesto polaska
        staniceList = database.getStaniceForVrijeme(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

        // lista vremena dolaska na stanicu za odabrano mjesto polaska
        vrijemeList = database.getVrijemeDolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

        // pozivanje metode koja izracunava dolaska na stanicu u odnosu na vrijeme polaska
        vrijemeIzracunato = izracunaj(vrijemeList, mSpinnerVrijemePolska.getSelectedItem().toString());

        // adapter za recycle view
        // prosljeduju se liste
        adapter = new VrijemeAdapter(staniceList, vrijemeIzracunato);
        recyclerView.setAdapter(adapter);

        //

        // metoda koja se poziva odabirom vrijednosti u spinneru
        mSpinnerMjestoPolaska.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // sprema u varijablu odabrano mjesto polaska
                mjestoPolaska = parent.getItemAtPosition(position).toString();

                // lista stanica za odabrano mjesto polaska
                staniceList = database.getStaniceForVrijeme(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

                // lista vremena dolaska na stanicu za odabrano mjesto polaska
                vrijemeList = database.getVrijemeDolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

                // pozivanje metode koja izracunava dolaska na stanicu u odnosu na vrijeme polaska
                if (mSpinnerVrijemePolska.getSelectedItem() != null){
                    vrijemeIzracunato = izracunaj(vrijemeList, mSpinnerVrijemePolska.getSelectedItem().toString());

                    // adapter za recycle view
                    // prosljeduju se liste
                    adapter = new VrijemeAdapter(staniceList, vrijemeIzracunato);
                    recyclerView.setAdapter(adapter);

                    // definira vrijednosti 3. spinnera - razlicit je mjenjanjem 1. spinnera
                    listaVrijemePolaska = database.getVrijemePolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString(), mSpinnerDan.getSelectedItem().toString());
                    adapterVrijemePolaska = new ArrayAdapter(Vrijeme.this, android.R.layout.simple_spinner_item, listaVrijemePolaska);
                    adapterVrijemePolaska.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerVrijemePolska.setAdapter(adapterVrijemePolaska);
                } else {
                    List emptyStanice = new ArrayList();
                    List emptyVrijeme = new ArrayList();
                    adapter = new VrijemeAdapter(emptyStanice, emptyVrijeme);
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(Vrijeme.this, "Nema polazaka!", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // metoda koja se poziva odabirom vrijednosti u spinneru
        mSpinnerDan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // sprema u varijablu odabrani dan
                dan = parent.getItemAtPosition(position).toString();

                // lista stanica za odabrani dan
                staniceList = database.getStaniceForVrijeme(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

                // lista vremena dolaska na stanicu za odabrani dan
                vrijemeList = database.getVrijemeDolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

                listaVrijemePolaska = database.getVrijemePolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString(), mSpinnerDan.getSelectedItem().toString());
                adapterVrijemePolaska = new ArrayAdapter(Vrijeme.this, android.R.layout.simple_spinner_item, listaVrijemePolaska);
                adapterVrijemePolaska.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerVrijemePolska.setAdapter(adapterVrijemePolaska);

                // pozivanje metode koja izracunava dolaska na stanicu u odnosu na vrijeme polaska
                if (mSpinnerVrijemePolska.getSelectedItem() != null){
                    vrijemeIzracunato = izracunaj(vrijemeList, mSpinnerVrijemePolska.getSelectedItem().toString());

                    // adapter za recycle view
                    // prosljeduju se liste
                    adapter = new VrijemeAdapter(staniceList, vrijemeIzracunato);
                    recyclerView.setAdapter(adapter);

                    // definira vrijednosti 3. spinnera - razlicit je mjenjanjem 2. spinnera
                    listaVrijemePolaska = database.getVrijemePolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString(), mSpinnerDan.getSelectedItem().toString());
                    adapterVrijemePolaska = new ArrayAdapter(Vrijeme.this, android.R.layout.simple_spinner_item, listaVrijemePolaska);
                    adapterVrijemePolaska.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerVrijemePolska.setAdapter(adapterVrijemePolaska);
                } else {
                    List emptyStanice = new ArrayList();
                    List emptyVrijeme = new ArrayList();
                    adapter = new VrijemeAdapter(emptyStanice, emptyVrijeme);
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(Vrijeme.this, "Nema polazaka!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // metoda koja se poziva odabirom vrijednosti u spinneru
        mSpinnerVrijemePolska.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // sprema u varijablu odabrano vrijeme polaska
                vrijemePolaska = parent.getItemAtPosition(position).toString();

                // lista stanica za odabrano vrijeme polaska
                staniceList = database.getStaniceForVrijeme(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

                // lista vremena dolaska na stanicu za odabrano vrijeme polaska
                vrijemeList = database.getVrijemeDolaska(nazivLinije, mSpinnerMjestoPolaska.getSelectedItem().toString());

                // pozivanje metode koja izracunava dolaska na stanicu u odnosu na vrijeme polaska
                vrijemeIzracunato = izracunaj(vrijemeList, vrijemePolaska);

                // adapter za recycle view
                // prosljeduju se liste
                adapter = new VrijemeAdapter(staniceList, vrijemeIzracunato);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // metoda koja izracunava vrijeme dolaska na stanicu u odnosu na vrijeme polaska
    public List izracunaj(List vrijeme, String vrijemePolaska){
        // vrijemePolaska je vrijeme kada autobus krece sa mjesta polaska
        // vrijeme je lista vremena (koliko treba autobusu da dode do stanice)

        // lista za spremanje rezultata
        List vrijemeIzracunato = new ArrayList();

        // varijabla u koju se spremaju prva dva znaka varijable vrijemePolaska - sat
        String sat = vrijemePolaska.substring(0,2);

        // varijabla u koju se spremaju zadnja dva znaka varijable vrijemePolaska - minute
        String minuta = vrijemePolaska.substring(3,5);

        // varijabla za vrijeme dolaska odredene stanice
        int dolazak;

        // int vrijednst varijable sat
        int satTmp = Integer.valueOf(sat);

        // int vrijednost varijable minuta
        int minutaTmp = Integer.valueOf(minuta);

        // varijable za izracunat sat i minutu dolaska
        String finalSat = null;
        String finalMinuta;

        // sa sva vremena dolazaka izracunava se potpuno vrijeme
        for(int i = 0; i < vrijeme.size(); i++){

            // vrijeme dolaska za svaku stnaicu
            dolazak = Integer.valueOf(vrijeme.get(i).toString());

            // varijabla u kojoj se nalaze minute od vremena polazaka
            minutaTmp = Integer.valueOf(minuta);
            satTmp = Integer.valueOf(sat);

            // nova vrijednost je zbroj minuta od vremena polaska i vremena dolaska do stanice
            minutaTmp = minutaTmp + dolazak;

            // ako je zbroj veci od 60, sat od vremena polaska se povecava za jedan, a minute postaju ostatak %60
            if (minutaTmp >= 60){
                minutaTmp = minutaTmp%60;
                satTmp += 1;
            }

            // varijabla sat za izracunato vrijeme
            finalSat = String.valueOf(satTmp);

            // varijabla minute za izracunato vrijeme
            finalMinuta = String.valueOf(minutaTmp);

            // definiranje standarda za prikaz
            if (satTmp < 10){
                finalSat = "0" + finalSat;
            }

            if (minutaTmp < 10){
                finalMinuta = "0" + finalMinuta;
            }

            // dodavanje izracunatog vremena u listu
            vrijemeIzracunato.add(i, finalSat + ":" + finalMinuta);
        }

        return vrijemeIzracunato;
    }

    // metoda koja se poziva klikom na povratak iz aktivnosti
    // vraca se u klasu VrijemeLinije
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Vrijeme.this, VrijemeLinije.class);
        intent.putExtra("key", "forVrijeme");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

}
