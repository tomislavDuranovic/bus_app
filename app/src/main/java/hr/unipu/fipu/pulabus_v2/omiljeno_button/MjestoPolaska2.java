package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.R;

/**
 * Fragment za mjesto polaska 2
 */
@SuppressLint("ValidFragment")
public class MjestoPolaska2 extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // varijable i lista za prosljedenih vrijednosti pozivom fragmenta
    private List<String> listaStanica;
    private String mjestoPolaska;
    private String nazivLinije;
    private Context mContext;

    // Konstruktor koji postavlja vrijednosti
    @SuppressLint("ValidFragment")
    public MjestoPolaska2(List<String> stanice, String linija, String mjesto) {
        listaStanica = stanice;
        nazivLinije = linija;
        mjestoPolaska = mjesto;
    }

    // pri otvaranju postavti kontekst trenutne aktivnosti
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // metoda koja poziva adapter za iteme liste stanice za mjesto polaska 2
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.omiljeno_stanice_list, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OmiljenoStaniceAdapter(listaStanica, nazivLinije, mjestoPolaska);
        recyclerView.setAdapter(adapter);

        return v;
    }
}
