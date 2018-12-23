package hr.unipu.fipu.pulabus_v2.ostalo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa Ulaz - aktivnost koja se pokrece samo pri otvaranju aplikacije
 */
public class Ulaz extends AppCompatActivity {

    // varijabla za definiranje trajanja aktivnosti
    private static int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ulaz);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent pocetna = new Intent(Ulaz.this, Pocetna.class);
                startActivity(pocetna);
                finish();
            }
        }, SPLASH_TIME);
    }
}
