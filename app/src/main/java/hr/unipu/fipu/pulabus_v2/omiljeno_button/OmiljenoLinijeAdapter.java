package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa OmiljenoAdapter - definira item recycle view-a
 */
public class OmiljenoLinijeAdapter extends RecyclerView.Adapter<OmiljenoLinijeAdapter.MyViewHolder> {

    // liste u koje se spremaju podaci iz prosljedenih argumenata prilikom poziva adaptera
    private List linijeList;
    private List duziNazivList;

    // Klasa MyViewHolder - uzorak za svaki item u view-u
    // ubrzava listanje itema smanjivanjem broja poziva metode findViewById
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // svaki TextView za ispis podataka svake linije
        public TextView linije;
        public TextView duziNaziv;

        // konstruktor MyViewHoldera - povezuje TextView-e s onima iz layouta
        public MyViewHolder(View v) {
            super(v);
            linije = (TextView) v.findViewById(R.id.textView);
            duziNaziv = (TextView) v.findViewById(R.id.opis);
        }
    }

    // konstruktor klase OmiljenoLinijeAdapter
    public OmiljenoLinijeAdapter(List linijeLista, List duziNazivLista){
        linijeList = linijeLista;
        duziNazivList = duziNazivLista;
    }

    // metoda onCreateViewHolder koja povezuje layout i klasu te prosljeduje view klasi MyViewHolder
    @NonNull
    @Override
    public OmiljenoLinijeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.omiljeno_linije_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    // metoda onBindViewHolder koja postavlja vrijednosti svakom itemu na njegovoj poziciji
    // klikom na item, poziva se aktivnost OmiljenoStanice koja prikazuje sve stanice na odabranoj liniji
    // prosljeduje naziv linije
    @Override
    public void onBindViewHolder(@NonNull OmiljenoLinijeAdapter.MyViewHolder holder, final int position) {
        holder.linije.setText((CharSequence) linijeList.get(position));
        holder.duziNaziv.setText((CharSequence) duziNazivList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OmiljenoStanice.class);
                intent.putExtra("nazivLinije", String.valueOf(linijeList.get(position)));
                v.getContext().startActivity(intent);
            }
        });
    }

    // metoda koja vraca broj itema
    @Override
    public int getItemCount() {
        return linijeList.size();
    }
}
