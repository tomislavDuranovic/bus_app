package hr.unipu.fipu.pulabus_v2.linije_button;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;

/**
 * MyFragmentPageAdapterLinije koji poziva fragmente
 */
public class MyFragmentPageAdapterLinije extends FragmentPagerAdapter {

    // varijable za spremanje mjesta polaska ovisno o liniji
    private String mjestoPolaska1;
    private String mjestoPolaska2;

    // varijabla za prosljeden naziv linije
    private String nazivLinije;

    private Database database;
    private Context context;

    // Konstruktor MyFragmentPageAdapterLinije
    public MyFragmentPageAdapterLinije(Context c, android.support.v4.app.FragmentManager fm, String nazivLinijeTmp){
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

        // ako je prvi tab, onda se poziva VrijemePolaska1, inace VrijemePolaska2
        if (position == 0){
            return new VrijemePolaska1(nazivLinije, mjestoPolaska1);
        } else {
            return new VrijemePolaska2(nazivLinije, mjestoPolaska2);
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
