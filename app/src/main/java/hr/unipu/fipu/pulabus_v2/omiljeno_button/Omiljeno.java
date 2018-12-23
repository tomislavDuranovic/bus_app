package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;
import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa Omiljeno - mogucnost dodavanja "omiljnih" linija, odnosno stanica, te prikaz istih
 */
public class Omiljeno extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton floatingActionButton;
    private TextView textView;

    private Database database;

    // Liste za spremanje rezultata iz baze podataka
    private List<String> listLinije;
    private List<String> listStanice;
    private List<String> mjestoPolaska;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omiljeno);

        textView = (TextView) findViewById(R.id.textView);

        // FloatingActionButton - na klik se otvara nova aktivnost OmiljenoLinije
        // Intent sadrzi kljuc za identifikaciju prethodne aktivnosti
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Omiljeno.this, OmiljenoLinije.class);
                intent.putExtra("key", "fromOmiljeno");
                startActivity(intent);
            }
        });

        // instanca baze
        database = new Database(this);

        try {
            database.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            database.openDatabase();
        } catch (Exception e){
            e.printStackTrace();
        }

        // liste sa podacima iz baze
        listLinije = database.getOmiljenoLinije();
        listStanice = database.getOmiljenoStanice();
        mjestoPolaska = database.getOmiljenoMjestoPolaska();

        // recycleView u kojemu se prikazuje sadrzaj klase Omiljeno
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        // definiranje layoutManagera za recycleView
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // adapter za recycle view
        // prosljeduju se liste
        adapter = new OmiljenoAdapter(listLinije, listStanice, mjestoPolaska);
        recyclerView.setAdapter(adapter);

        // ispis u slucaju da jos nema dodanih linija/stanice i u slucaju da ima
        if (adapter.getItemCount() == 0){
            textView.setText("Dodajte svoje linije i stanice");
        } else {
            textView.setText("Moje linije i stanice");
        }
    }
}
