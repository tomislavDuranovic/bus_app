package hr.unipu.fipu.pulabus_v2.vrijeme_button;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;
import hr.unipu.fipu.pulabus_v2.ostalo.Pocetna;
import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa VrijemeLinije - sadrzi popis linija
 */
public class VrijemeLinije extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Database database;
    private TextView textView;

    // liste za vrijednosti iz baze
    private List<String> linije;
    private List<String> duziNaziv;

    // varijabla za vrijednost koja je poslana iz prethodne aktivnosti
    private String keyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vrijeme_linije);

        // dohvaca vrijednost intenta
        keyIntent = getIntent().getStringExtra("key");

        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Linije");

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

        // poziva metode iz klase Database za dohvanja podataka o liniji
        linije = database.getLinije();
        duziNaziv = database.getNazivDuzi();

        // recycleView u kojemu se prikazuje sadrzaj klase VrijemeLinije
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        // definiranje layoutManagera za recycleView
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // adapter za recycle view
        // prosljeduju se liste i vrijednost intent-a zbog buducih aktivnosti
        adapter = new VrijemeLinijeAdapter(linije, duziNaziv, keyIntent);
        recyclerView.setAdapter(adapter);

    }

    // metoda koja se poziva klikom na povratak iz aktivnosti
    // vraca se u klasu Pocetna
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VrijemeLinije.this, Pocetna.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }
}
