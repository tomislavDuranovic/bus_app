package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa OmiljenoStanice - poziva FragmentPageAdapter za prikaz popisa stanica
 */
public class OmiljenoStanice extends AppCompatActivity {

    // varijabla prosljedenog naziva linije
    private String nazivLinije;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omiljeno_stanice);

        // postavi naziv linije
        nazivLinije = getIntent().getStringExtra("nazivLinije");

        // layout manager koji dopusta listati fragmente lijevo/desno
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        // poziva instancu klase MyFragmentPageAdapter
        MyFragmentPageAdapter adapter = new MyFragmentPageAdapter(this, getSupportFragmentManager(), nazivLinije);

        // postavlja view pageru adapter
        viewPager.setAdapter(adapter);

        // Layout koji sadrzi tabove
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
