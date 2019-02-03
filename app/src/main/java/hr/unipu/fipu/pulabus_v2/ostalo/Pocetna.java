package hr.unipu.fipu.pulabus_v2.ostalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import hr.unipu.fipu.pulabus_v2.R;
import hr.unipu.fipu.pulabus_v2.omiljeno_button.Omiljeno;
import hr.unipu.fipu.pulabus_v2.sadrzaj_button.MapsActivity;
import hr.unipu.fipu.pulabus_v2.sadrzaj_button.PopisSadrzaja;
import hr.unipu.fipu.pulabus_v2.vrijeme_button.VrijemeLinije;

/**
 * Klasa Pocetna - aktivnost koja se pokrece nakon aktivnosti Ulaz
 */
public class Pocetna extends AppCompatActivity {

    // ImageButtoni za pokretanje odabranje aktivnosti
    private ImageButton imageButtonOmiljeno;
    private ImageButton imageButtonLinije;
    private ImageButton imageButtonVrijeme;
    private ImageButton imageButtonSadrzaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocetna);

        imageButtonOmiljeno = (ImageButton) findViewById(R.id.imageButtonOmiljeno);
        imageButtonLinije = (ImageButton) findViewById(R.id.imageButtonLinije);
        imageButtonVrijeme = (ImageButton) findViewById(R.id.imageButtonVrijeme);
        imageButtonSadrzaj = (ImageButton) findViewById(R.id.imageButtonSadrzaj);

        imageButtonOmiljeno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Pocetna.this, Omiljeno.class));
            }
        });

        // intentExtra - kljuc za otvaranje aktivnosti VrijemeLinija
        // potreban jer su dvije razlicite aktivnosti usmjerene na istu, a razliciti sadrzaj
        imageButtonLinije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pocetna.this, VrijemeLinije.class);
                intent.putExtra("key", "forLinije");
                startActivity(intent);
            }
        });

        imageButtonVrijeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pocetna.this, VrijemeLinije.class);
                intent.putExtra("key", "forVrijeme");
                startActivity(intent);
            }
        });

        imageButtonSadrzaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pocetna.this, PopisSadrzaja.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}