package hr.unipu.fipu.pulabus_v2.vrijeme_button;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.linije_button.LinijePolasci;
import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa VrijemeLinijeAdapter - definira item recycle view-a
 */
public class VrijemeLinijeAdapter extends RecyclerView.Adapter<VrijemeLinijeAdapter.MyViewHolder>{

    // varijabla i liste u koje se spremaju podaci iz prosljedenih argumenata prilikom poziva adaptera
    private List linijeList;
    private List duziNazivList;
    private String keyIntent;

    // Klasa MyViewHolder - uzorak za svaki item u view-u
    // ubrzava listanje itema smanjivanjem broja poziva metode findViewById
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView linije;
        public TextView duziNaziv;

        // konstruktor MyViewHoldera - povezuje TextView-e s onima iz layouta
        public MyViewHolder(View v) {
            super(v);
            linije = (TextView) v.findViewById(R.id.textView);
            duziNaziv = (TextView) v.findViewById(R.id.opis);
        }
    }

    // konstruktor klase VrijemeLinijeAdapter
    public VrijemeLinijeAdapter(List linijeLista, List duziNazivLista, String key){
        linijeList = linijeLista;
        duziNazivList = duziNazivLista;
        keyIntent = key;
    }

    // metoda onCreateViewHolder koja povezuje layout i klasu te prosljeduje view klasi MyViewHolder
    @NonNull
    @Override
    public VrijemeLinijeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vrijeme_linije_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    // metoda onBindViewHolder koja postavlja vrijednosti svakom itemu na njegovoj poziciji
    // ako je intent vrijednost "forVrijeme" otvara se aktivnost Vrijeme (VrijemeLinije je pozvano klikom na button imageButtonVrijeme)
    // inace, otvara se aktivnost LinijePolasci (VrijemeLinije je pozvano klikom na button imageButtonLinije)
    // u oba slucaja se prosljeduje varijabla nazivLinije odabrane linije
    @Override
    public void onBindViewHolder(@NonNull VrijemeLinijeAdapter.MyViewHolder holder, final int position) {
        holder.linije.setText((CharSequence) linijeList.get(position));
        holder.duziNaziv.setText((CharSequence) duziNazivList.get(position));

        if (keyIntent.equals("forVrijeme")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Vrijeme.class);
                    intent.putExtra("nazivLinije", String.valueOf(linijeList.get(position)));
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), LinijePolasci.class);
                    intent.putExtra("nazivLinije", String.valueOf(linijeList.get(position)));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    // vraca broj itema u listi
    @Override
    public int getItemCount() {
        return linijeList.size();
    }
}

