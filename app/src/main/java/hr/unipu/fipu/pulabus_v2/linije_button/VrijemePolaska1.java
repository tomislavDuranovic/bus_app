package hr.unipu.fipu.pulabus_v2.linije_button;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;
import hr.unipu.fipu.pulabus_v2.R;

/**
 * Fragment za vrijeme polaska 1
 */
@SuppressLint("ValidFragment")
public class VrijemePolaska1 extends Fragment {

    // varijable za prosljedene vrijednosti pozivom fragmenta
    private String mjestoPolaska;
    private String nazivLinije;

    private Context mContext;
    private Database database;

    // liste vremena polazaka za odredene dane
    private List<String> polasciRadni;
    private List<String> polasciSubota;
    private List<String> polasciNedjelja;
    private List<String> razlicitiSatiPolaska;

    // tablica za prikaz vremena
    private TableLayout mTable;

    // buttoni za odabir dana
    private Button mButtonRadni;
    private Button mButtonSubota;
    private Button mButtonNedjelja;

    // Konstruktor koji postavlja vrijednosti
    @SuppressLint("ValidFragment")
    public VrijemePolaska1(String linija, String mjesto) {
        nazivLinije = linija;
        mjestoPolaska = mjesto;
    }

    // pri otvaranju postaviti kontekst trenutne aktivnosti
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // metoda koja poziva postavljanje tablice s vremenima polazaka ovisno o odabiru danu
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.linije_polasci_list, container, false);

        database = new Database(mContext);

        // vremena polazaka ovisno o danu
        polasciRadni = database.getVrijemePolaska(nazivLinije, mjestoPolaska, "radni");
        polasciSubota = database.getVrijemePolaska(nazivLinije, mjestoPolaska, "subota");
        polasciNedjelja = database.getVrijemePolaska(nazivLinije, mjestoPolaska, "nedjelja");

        // lista koja sadrzi sve razlicite sate (bez ponavljanja) vremena polazaka svih dana
        razlicitiSatiPolaska = satiPolazaka(polasciRadni, polasciSubota, polasciNedjelja);

        // tablica za prikaz
        mTable = (TableLayout) v.findViewById(R.id.table);

        // buttoni za odabir dana
        mButtonRadni = (Button) v.findViewById(R.id.btnRadni);
        mButtonSubota = (Button) v.findViewById(R.id.btnSubota);
        mButtonNedjelja = (Button) v.findViewById(R.id.btnNedjelja);

        // odma prikazuje za radni dan tablicu
        mButtonRadni.setTextColor(Color.BLACK);
        mButtonSubota.setTextColor(Color.GRAY);
        mButtonNedjelja.setTextColor(Color.GRAY);
        mTable.removeAllViews();
        mTable = makeTable(mTable, polasciRadni);


        // ako je odabran radni dan, ocisti se view od prethodnih podataka i poziva se metoda makeTable
        mButtonRadni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonRadni.setTextColor(Color.BLACK);
                mButtonSubota.setTextColor(Color.GRAY);
                mButtonNedjelja.setTextColor(Color.GRAY);
                mTable.removeAllViews();
                mTable = makeTable(mTable, polasciRadni);
            }
        });

        // ako je odabran dan subota, ocisti se view od prethodnih podataka i poziva se metoda makeTable
        mButtonSubota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonSubota.setTextColor(Color.BLACK);
                mButtonRadni.setTextColor(Color.GRAY);
                mButtonNedjelja.setTextColor(Color.GRAY);
                mTable.removeAllViews();
                mTable = makeTable(mTable, polasciSubota);
            }
        });

        // ako je odabran dan nedjelja, ocisti se view od prethodnih podataka i poziva se metoda makeTable
        mButtonNedjelja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonNedjelja.setTextColor(Color.BLACK);
                mButtonRadni.setTextColor(Color.GRAY);
                mButtonSubota.setTextColor(Color.GRAY);
                mTable.removeAllViews();
                mTable = makeTable(mTable, polasciNedjelja);
            }
        });

        return v;
    }

    // metoda za izracun razlicitih sata polazaka svih dana
    public List<String> satiPolazaka(List<String> listaRadni, List<String> listaSubota, List<String> listaNedjelja) {
        List<String> listaNovo = new ArrayList<>();

        for (int i = 0; i < listaRadni.size(); i++) {

            String vrijemePolaska = listaRadni.get(i);
            String sat = vrijemePolaska.substring(0, 2);

            if (listaNovo.contains(sat) == false) {
                listaNovo.add(sat);
            }
        }

        for (int i = 0; i < listaSubota.size(); i++) {

            String vrijemePolaska = listaRadni.get(i);
            String sat = vrijemePolaska.substring(0, 2);

            if (listaNovo.contains(sat) == false) {
                listaNovo.add(sat);
            }
        }

        for (int i = 0; i < listaNedjelja.size(); i++) {

            String vrijemePolaska = listaRadni.get(i);
            String sat = vrijemePolaska.substring(0, 2);

            if (listaNovo.contains(sat) == false) {
                listaNovo.add(sat);
            }
        }

        return listaNovo;
    }

    // metoda za izracun razlicitih sata polazaka odabranog dana
    public List<String> satiPolazaka(List<String> lista) {
        List<String> listaNovo = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {

            String vrijemePolaska = lista.get(i);
            String sat = vrijemePolaska.substring(0, 2);

            if (listaNovo.contains(sat) == false) {
                listaNovo.add(sat);
            }
        }
        return listaNovo;
    }

    // metoda koja definira strukturu tablicu
    public TableLayout makeTable(TableLayout tableLayout, List<String> polasciList) {
        if (polasciList.size() == 0) {
            Toast.makeText(mContext, "Nema polazaka!", Toast.LENGTH_SHORT).show();
        } else {

            // prvi red
            TableRow row01 = new TableRow(mContext);
            row01.setWeightSum(4);

            // parametri za polozaj text view-a
            TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams();
            layoutParams1.weight = 1;

            // dodavanje text view-a u tablicu za prvi red
            TextView textView10 = new TextView(mContext);
            textView10.setText("h");
            textView10.setTypeface(null, Typeface.BOLD);
            textView10.setLayoutParams(layoutParams1);
            row01.addView(textView10);

            TextView textView11 = new TextView(mContext);
            textView11.setText("");
            textView11.setLayoutParams(layoutParams1);
            row01.addView(textView11);

            TextView textView12 = new TextView(mContext);
            textView12.setText("min");
            textView12.setTypeface(null, Typeface.BOLD);
            textView12.setLayoutParams(layoutParams1);
            row01.addView(textView12);

            TextView textView13 = new TextView(mContext);
            textView13.setText("");
            textView13.setLayoutParams(layoutParams1);
            row01.addView(textView13);

            tableLayout.addView(row01);

            //

            razlicitiSatiPolaska = satiPolazaka(polasciList);

            // ispis sata polazaka, te za taj sat sve minute polazaka
            for (int i = 0; i < razlicitiSatiPolaska.size(); i++) {
                TableRow row = new TableRow(mContext);

                // sat
                TextView textView1 = new TextView(mContext);
                textView1.setText(razlicitiSatiPolaska.get(i));
                row.addView(textView1);

                // minute
                // pomocne liste za rezultate
                List<String> polasciRadniSat = new ArrayList();
                List<String> polasciRadniMinuta = new ArrayList();

                // varijabla koja sadrzi trenutni sat
                String razlicitiSatPolazaka = razlicitiSatiPolaska.get(i);

                // za sva vremena polazaka u prosljedenoj listi uzima prva dva znaka (sat) i zadnja dva znaka(minuta)
                for (int a = 0; a < polasciList.size(); a++) {
                    String vrijemePolaska = polasciList.get(a);
                    String sat = vrijemePolaska.substring(0, 2);
                    String minuta = vrijemePolaska.substring(3, 5);

                    // ako je sat isti onaj u varijabli trenutnog sata, u privremene liste se dodaje vrijeme koje treba ispisati
                    if (sat.equals(razlicitiSatPolazaka)) {
                        polasciRadniSat.add(sat);
                        polasciRadniMinuta.add(minuta);
                    }
                }

                // ispisivanje odabranih vremena polazaka u tablicu
                for (int b = 0; b < polasciRadniSat.size(); b++) {
                    if (polasciRadniSat.size() == 1){
                        if (Integer.valueOf(polasciRadniMinuta.get(b)) <= 20){
                            TextView textView3 = new TextView(mContext);
                            textView3.setText(polasciRadniMinuta.get(b));
                            row.addView(textView3);
                        } else if (Integer.valueOf(polasciRadniMinuta.get(b)) > 20 &&
                                Integer.valueOf(polasciRadniMinuta.get(b)) <= 40){
                            TextView textView2 = new TextView(mContext);
                            textView2.setText("");
                            row.addView(textView2);

                            TextView textView3 = new TextView(mContext);
                            textView3.setText(polasciRadniMinuta.get(b));
                            row.addView(textView3);
                        } else {
                            TextView textView2 = new TextView(mContext);
                            textView2.setText("");
                            row.addView(textView2);

                            TextView textView3 = new TextView(mContext);
                            textView3.setText("");
                            row.addView(textView3);

                            TextView textView4 = new TextView(mContext);
                            textView4.setText(polasciRadniMinuta.get(b));
                            row.addView(textView4);
                        }

                    } else if (polasciRadniSat.size() == 2){
                        if (Integer.valueOf(polasciRadniMinuta.get(0)) <= 20 &&
                                Integer.valueOf(polasciRadniMinuta.get(1)) <= 40){
                            TextView textView3 = new TextView(mContext);
                            textView3.setText(polasciRadniMinuta.get(0));
                            row.addView(textView3);

                            TextView textView4 = new TextView(mContext);
                            textView4.setText(polasciRadniMinuta.get(1));
                            row.addView(textView4);
                            break;
                        } else if (Integer.valueOf(polasciRadniMinuta.get(0)) <= 20 &&
                                Integer.valueOf(polasciRadniMinuta.get(1)) > 40){
                            TextView textView2 = new TextView(mContext);
                            textView2.setText(polasciRadniMinuta.get(0));
                            row.addView(textView2);

                            TextView textView3 = new TextView(mContext);
                            textView3.setText("");
                            row.addView(textView3);

                            TextView textView4 = new TextView(mContext);
                            textView4.setText(polasciRadniMinuta.get(1));
                            row.addView(textView4);
                            break;
                        } else {
                            TextView textView2 = new TextView(mContext);
                            textView2.setText("");
                            row.addView(textView2);

                            TextView textView3 = new TextView(mContext);
                            textView3.setText(polasciRadniMinuta.get(0));
                            row.addView(textView3);

                            TextView textView4 = new TextView(mContext);
                            textView4.setText(polasciRadniMinuta.get(1));
                            row.addView(textView4);
                            break;
                        }

                    } else {
                        TextView textView2 = new TextView(mContext);
                        textView2.setText(polasciRadniMinuta.get(b));
                        row.addView(textView2);
                    }
                }
                tableLayout.addView(row);
            }
        }
        return tableLayout;
    }
}
