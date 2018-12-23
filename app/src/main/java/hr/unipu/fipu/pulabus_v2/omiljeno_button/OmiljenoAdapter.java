package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.ostalo.Database;
import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa OmiljenoAdapter - definira item recycle view-a
 */
public class OmiljenoAdapter extends RecyclerView.Adapter<OmiljenoAdapter.MyViewHolder> {

    // liste u koje se spremaju podaci iz prosljedenih argumenata prilikom poziva adaptera
    private List listLinije;
    private List listStanice;
    private List listMjestoPolaska;

    // Klasa MyViewHolder - uzorak za svaki item u view-u
    // ubrzava listanje itema smanjivanjem broja poziva metode findViewById
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // svaki TextView za ispis podataka "omiljene" stanice
        public TextView linije;
        public TextView stanice;
        public TextView vrijeme;
        public TextView polazak;

        // varijable za prosljedivanje vrijednosti argumenata za brisanje stavke iz baze
        public String linijaTmp;
        public String stanicaTmp;
        public String mjestoPolaskaTmp;
        public Context context;

        // instanca baze
        public Database database;

        // konstruktor MyViewHoldera - povezuje TextView-e s onima iz layouta
        public MyViewHolder(View v){
            super(v);
            linije = (TextView) v.findViewById(R.id.nazivLinije);
            stanice = (TextView) v.findViewById(R.id.nazivStanice);
            vrijeme = (TextView) v.findViewById(R.id.vrijeme);
            polazak = (TextView) v.findViewById(R.id.mjestoPolaska);

            // metoda koja se poziva na duzi klik na odredeni item
            // sluzi za brisanje itema
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                    dialog.setTitle("Jeste li sigurni da zelite izbrisat stanicu " + stanicaTmp + "?");
                    dialog.setNegativeButton("Odustani", null);
                    dialog.setPositiveButton("Obrisi", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database = new Database(context);
                        database.deleteFromOmiljenoStanice(linijaTmp, stanicaTmp, mjestoPolaskaTmp);
                        Toast.makeText(v.getContext(), "Obrisano", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });
        }
    }

    // konstruktor klase OmiljenoAdapter
    public OmiljenoAdapter(List listaLinije, List listaStanice, List mjestoPolaska) {
        listLinije = listaLinije;
        listStanice = listaStanice;
        listMjestoPolaska = mjestoPolaska;
    }

    // metoda onCreateViewHolder koja povezuje layout i klasu te prosljeduje view klasi MyViewHolder
    @NonNull
    @Override
    public OmiljenoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.omiljeno_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        viewHolder.context = parent.getContext();
        return viewHolder;
    }

    // metoda onBindViewHolder koja postavlja vrijednosti svakom itemu na njegovoj poziciji
    @Override
    public void onBindViewHolder(@NonNull OmiljenoAdapter.MyViewHolder holder, int position) {
        holder.linije.setText((CharSequence) listLinije.get(position));
        holder.stanice.setText((CharSequence) listStanice.get(position));
        holder.polazak.setText((CharSequence) listMjestoPolaska.get(position));
        holder.vrijeme.setText("10:30");

        // za Alert dialog (brisanje iz baze)
        // prosljedivanje vrijednosti varijablama iz klasa MyViewHolder
        holder.stanicaTmp = listStanice.get(position).toString();
        holder.linijaTmp = listLinije.get(position).toString();
        holder.mjestoPolaskaTmp = listMjestoPolaska.get(position).toString();
    }

    // metoda koja vraca broj itema
    @Override
    public int getItemCount() {
        return listLinije.size();
    }
}
