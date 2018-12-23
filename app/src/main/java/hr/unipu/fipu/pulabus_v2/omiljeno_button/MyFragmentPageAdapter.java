package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;

/**
 * FragmentPageAdapter koji poziva fragmente
 */
public class MyFragmentPageAdapter extends FragmentPagerAdapter {

    private Context context;

    // varijabla za prosljeden naziv linije
    private String nazivLinije;

    // varijable za spremanje mjesta polaska ovisno o liniji
    private String mjestoPolaska1;
    private String mjestoPolaska2;

    private Database database;

    // liste za popis stanica ovisno i liniji i mjestu polaska
    private List<String> listaStanica1;
    private List<String> listaStanica2;

    // Konstruktor MyFragmentPageAdaptera
    public MyFragmentPageAdapter(Context c, android.support.v4.app.FragmentManager fm, String nazivLinijeTmp){
        super(fm);
        context = c;
        nazivLinije = nazivLinijeTmp;
    }

    // metoda za svaki item u klasi
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        database = new Database(context);

        // mjesta polazaka iz baze za odabranu liniju
        mjestoPolaska1 = database.getMjestoPolaska1(nazivLinije);
        mjestoPolaska2 = database.getMjestoPolaska2(nazivLinije);

        // popis stanica za istu liniju
        listaStanica1 = database.getStanice(nazivLinije, mjestoPolaska1);
        listaStanica2 = database.getStanice(nazivLinije, mjestoPolaska2);

        // ako je prvi tab, onda se poziva MjestoPolaska1, inace MjestoPolaska2
        if (position == 0){
            return new MjestoPolaska1(listaStanica1, nazivLinije, mjestoPolaska1);
        } else {
            return new MjestoPolaska2(listaStanica2, nazivLinije, mjestoPolaska2);
        }
    }

    // vraca broj tabova
    @Override
    public int getCount() {
        return 2;
    }

    // metod koja postavlja naziv taba
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        database = new Database(context);

        mjestoPolaska1 = database.getMjestoPolaska1(nazivLinije);
        mjestoPolaska2 = database.getMjestoPolaska2(nazivLinije);

        if (position == 0){
            return mjestoPolaska1;
        } else {
            return mjestoPolaska2;
        }
    }
}
