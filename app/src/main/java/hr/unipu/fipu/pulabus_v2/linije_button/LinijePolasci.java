package hr.unipu.fipu.pulabus_v2.linije_button;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import hr.unipu.fipu.pulabus_v2.R;
import hr.unipu.fipu.pulabus_v2.vrijeme_button.VrijemeLinije;

/**
 * Klasa LinijePolasci - poziva FragmentPageAdapter za prikaz polazaka linije
 */
public class LinijePolasci extends AppCompatActivity {

    // varijabla prosljedenog naziva linije
    private String nazivLinije;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linije_polasci);

        // postavi naziv linije
        nazivLinije = getIntent().getStringExtra("nazivLinije");

        // layout manager koji dopusta listati fragmente lijevo/desno
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        // poziva instancu klase MyFragmentPageAdapterLinije
        MyFragmentPageAdapterLinije adapter = new MyFragmentPageAdapterLinije(this, getSupportFragmentManager(), nazivLinije);

        // postavlja view pageru adapter
        viewPager.setAdapter(adapter);

        // Layout koji sadrzi tabove
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    // metoda koja se poziva klikom na back
    // poziva aktivnost VrijemeLinije
    // prosljeduje vrijednost "forLinije" kao i u klasi Pocetna jer ce se klasa VrijemeLinije koristiti za polaske linija, ne za vrijeme
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LinijePolasci.this, VrijemeLinije.class);
        intent.putExtra("key", "forLinije");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }
}
