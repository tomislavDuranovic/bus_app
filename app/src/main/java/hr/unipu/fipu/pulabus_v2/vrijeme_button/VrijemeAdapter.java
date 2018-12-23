package hr.unipu.fipu.pulabus_v2.vrijeme_button;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hr.unipu.fipu.pulabus_v2.R;

/**
 * Klasa VrijemeAdapter - definira item recycle view-a
 */
public class VrijemeAdapter extends RecyclerView.Adapter<VrijemeAdapter.MyViewHolder> {

    // liste u koje se spremaju podaci iz prosljedenih argumenata prilikom poziva adaptera
    private List listaStanice;
    private List listaVrijeme;

    // Klasa MyViewHolder - uzorak za svaki item u view-u
    // ubrzava listanje itema smanjivanjem broja poziva metode findViewById
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView stanica;
        public TextView vrijeme;

        // konstruktor MyViewHoldera - povezuje TextView-e s onima iz layouta
        public MyViewHolder(View v) {
            super(v);
            stanica = (TextView) v.findViewById(R.id.stanica);
            vrijeme = (TextView) v.findViewById(R.id.vrijeme);
        }
    }

    // konstruktor klase VrijemeAdapter
    public VrijemeAdapter(List stanice, List vrijeme){
        listaStanice = stanice;
        listaVrijeme = vrijeme;
    }


    // metoda onCreateViewHolder koja povezuje layout i klasu te prosljeduje view klasi MyViewHolder
    @NonNull
    @Override
    public VrijemeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vrijeme_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VrijemeAdapter.MyViewHolder holder, int position) {
        holder.stanica.setText((CharSequence) listaStanice.get(position));
        holder.vrijeme.setText((CharSequence) listaVrijeme.get(position));
    }

    @Override
    public int getItemCount() {
        return listaStanice.size();
    }
}
