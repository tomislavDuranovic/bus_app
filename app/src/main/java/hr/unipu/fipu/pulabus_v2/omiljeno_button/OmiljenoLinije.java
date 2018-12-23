package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;
import hr.unipu.fipu.pulabus_v2.R;

public class OmiljenoLinije extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Database database;
    private TextView textView;

    // Liste za spremanje rezultata iz baze podataka
    private List<String> linije;
    private List<String> duziNaziv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omiljeno_linije);

        // postavljanje teksta u TextView
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Linije");

        // kreiranje nove instance baze
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
        linije = database.getLinije();
        duziNaziv = database.getNazivDuzi();

        // recycleView u kojemu se prikazuje sadrzaj klase Omiljeno
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        // definiranje layoutManagera za recycleView
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // adapter za recycle view
        // prosljeduju se liste
        adapter = new OmiljenoLinijeAdapter(linije, duziNaziv);
        recyclerView.setAdapter(adapter);
    }

    // metoda koja se poziva klikom na povratak iz aktivnosti
    // vraca se u klasu Omiljeno
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OmiljenoLinije.this, Omiljeno.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }
}
