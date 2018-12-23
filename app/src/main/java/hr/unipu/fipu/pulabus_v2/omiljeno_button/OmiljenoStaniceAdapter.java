package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;
import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa OmiljenoStaniceAdapter - definira item recycle view-a
 */
public class OmiljenoStaniceAdapter extends RecyclerView.Adapter<OmiljenoStaniceAdapter.MyViewHolder> {

    // varijabl i lista u koje se spremaju podaci prosljedeni pozivom klase
    private List<String> listaStanica;
    private String nazivLinije;
    private String mjestoPolaska;

    private Database database;
    private Context mContext;

    // Klasa MyViewHolder - uzorak za svaki item u view-u
    // ubrzava listanje itema smanjivanjem broja poziva metode findViewById
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // text view za prikaz naziva stanice
        // toggle button za spremanje stanice pod "omiljeno"
        public TextView stanica;
        public ToggleButton toggleButton;

        // konstruktor MyViewHoldera - povezuje TextView i ToggleButton s onima iz layouta
        public MyViewHolder(View v) {
            super(v);
            stanica = (TextView) v.findViewById(R.id.textView);
            toggleButton = (ToggleButton) v.findViewById(R.id.toggleBtn);
        }
    }

    // konstruktor klase OmiljenoStaniceAdapter
    public OmiljenoStaniceAdapter(List staniceLista, String linija, String mjesto){
        listaStanica = staniceLista;
        nazivLinije = linija;
        mjestoPolaska = mjesto;
    }

    // metoda onCreateViewHolder koja povezuje layout i klasu te prosljeduje view klasi MyViewHolder
    @NonNull
    @Override
    public OmiljenoStaniceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.omiljeno_stanice_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        mContext = parent.getContext();
        return viewHolder;
    }

    // metoda onBindViewHolder koja postavlja vrijednosti svakom itemu na njegovoj poziciji
    // klikom na toggle button dodaje stanicu pod "omiljeno", odznacivanjem brise
    @Override
    public void onBindViewHolder(@NonNull final OmiljenoStaniceAdapter.MyViewHolder holder, final int position) {

        database = new Database(mContext);

        // liste koje pozivaju iz baze trenutne "omiljene" stanice za oznacivanje toggle button
        List<String> linije;
        List<String> nazivStanice;
        List<String> mjestoPolaskaList;

        linije = database.getOmiljenoLinije();
        nazivStanice = database.getOmiljenoStanice();
        mjestoPolaskaList = database.getOmiljenoMjestoPolaska();

        // za svaki item iz trenutne liste omiljenih stanice provjerava da li na i-toj poziciji sadrzi isti naziv kao i
        // item na poziciji position, ako da, oznacava toggle button
        for (int i = 0; i < linije.size(); i++) {
            if (linije.get(i).contains(nazivLinije) && nazivStanice.get(i).contains(listaStanica.get(position)) && mjestoPolaskaList.get(i).contains(mjestoPolaska)) {
                holder.toggleButton.setChecked(true);
            }
        }

        // postavlja naziv stanice
        holder.stanica.setText((CharSequence) listaStanica.get(position));

        // metoda na oznacivanje/odznacivanje toggle buttona
        // ako se oznaci - dodaje u bazu podatke odabranog itema liste
        // ako se odznaci  - brise iz baze podatke odabranog itema liste
        holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("Status: " + isChecked);
                String stanica = listaStanica.get(position);
                if (isChecked){
                    database.insertToOmiljenoStanice(nazivLinije, stanica, mjestoPolaska);
                    System.out.println("Dodano - " + "Linija: " + nazivLinije + "Stanica: " + listaStanica.get(position) + "Mjesto polaska: " + mjestoPolaska);
                } else {
                    database.deleteFromOmiljenoStanice(nazivLinije, stanica, mjestoPolaska);
                    System.out.println("Obrisano");
                }
            }
        });
    }

    // vraca broj stanica
    @Override
    public int getItemCount() {
        return listaStanica.size();
    }
}
