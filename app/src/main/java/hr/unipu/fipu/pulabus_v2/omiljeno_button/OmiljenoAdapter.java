package hr.unipu.fipu.pulabus_v2.omiljeno_button;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

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

    private Database database;
    private Context mContext;

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
                    dialog.setIcon(R.drawable.round_warning_black_24dp);
                    dialog.setTitle("Jeste li sigurni da zelite izbrisat stanicu " + stanicaTmp + "?");
                    dialog.setNegativeButton("Odustani", null);
                    dialog.setPositiveButton("Obrisi", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database = new Database(context);
                        database.deleteFromOmiljenoStanice(linijaTmp, stanicaTmp, mjestoPolaskaTmp);
                        Toast.makeText(v.getContext(), "Obrisano", Toast.LENGTH_SHORT).show();
                        // obrisani item se ne prikazuje vise
                        itemView.setVisibility(View.GONE);
                        // postavlja dimenzije obrisanog itema na 0
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
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
        mContext = parent.getContext();
        return viewHolder;
    }

    // metoda onBindViewHolder koja postavlja vrijednosti svakom itemu na njegovoj poziciji
    @Override
    public void onBindViewHolder(@NonNull final OmiljenoAdapter.MyViewHolder holder, final int position) {
        holder.linije.setText((CharSequence) listLinije.get(position));
        holder.stanice.setText((CharSequence) listStanice.get(position));
        holder.polazak.setText((CharSequence) listMjestoPolaska.get(position));

        // od tu je racunanje vremena za koliko ce bus doc na stanicu

        // dan -> trenutni dan u tjednu
        final Calendar calendar = Calendar.getInstance();
        final int dan = calendar.get(Calendar.DAY_OF_WEEK);
        final String danIme;

        if (dan == 2 || dan == 3 || dan == 4 || dan == 5 || dan == 6){
            danIme = "radni";
        } else if(dan == 7){
            danIme = "subota";
        } else {
            danIme = "nedjelja";
        }

        database = new Database(mContext);

        // listaVrijemePolaska -> za odabranu liniju, mjesto polaska i trenutni dan
        List listaVrijemePolaska = database.getVrijemePolaska(listLinije.get(position).toString(), listMjestoPolaska.get(position).toString(), danIme);
        // vrijeme koje treba toj liniji do stanice
        String vrijemeDoStanice = database.getVrijemeDoslskaForRealTime(listLinije.get(position).toString(),listMjestoPolaska.get(position).toString(), listStanice.get(position).toString());

        // lista za rezultat izracunatog vremena dolaska
        final List konacnoVrijemeDoslakaNaStanicu = new ArrayList();

        // sat od vremena polaska
        String sat;

        // minuta od vremena polaska
        String minuta;

        // sat kao int
        int satTmp;

        // minuta kao int
        int minutaTmp;

        // izracunata minuta
        int minutaIzracunato;

        // minute kao int do stanice
        final int minutaDoStanice = Integer.valueOf(vrijemeDoStanice);

        // satZadnje
        String satIzr;

        // minutaZadnje
        String minIzr;

        // provjera ako nema polazaka za taj dan uopce
        // ako nema, uzmi vrijeme za sutra da se moze izracunat
        // drugacija for petlja, ne uzima prvo vrijeme vece od trenuntnog nego prvo u listi
        // jer nas zanima kad ide prvi bus sutra, posto danas ne ide
        if (listaVrijemePolaska.isEmpty() == true){
            int danTmp = dan+1;
            String danImeTmp;
            if (danTmp == 2 || danTmp == 3 || danTmp == 4 || danTmp == 5 || danTmp == 6){
                danImeTmp = "radni";
            } else if(danTmp == 7){
                danImeTmp = "subota";
            } else {
                danImeTmp = "nedjelja";
            }
            listaVrijemePolaska = database.getVrijemePolaska(listLinije.get(position).toString(), listMjestoPolaska.get(position).toString(), danImeTmp);

            for (int i = 0; i < listaVrijemePolaska.size(); i++){
                sat = listaVrijemePolaska.get(i).toString().substring(0,2);
                minuta = listaVrijemePolaska.get(i).toString().substring(3,5);

                satTmp = Integer.valueOf(sat);
                minutaTmp = Integer.valueOf(minuta);

                minutaIzracunato = minutaTmp + minutaDoStanice;

                if (minutaIzracunato >= 60){
                    minutaIzracunato = minutaIzracunato%60;
                    satTmp += 1 ;
                }

                satIzr = String.valueOf(satTmp);
                minIzr = String.valueOf(minutaIzracunato);

                // spremanje rezultata u listu
                if (satTmp < 10){
                    satIzr = "0" + String.valueOf(satTmp);
                }

                if (minutaIzracunato < 10){
                    minIzr = "0" + String.valueOf(minutaIzracunato);
                }

                konacnoVrijemeDoslakaNaStanicu.add(satIzr + ":" + minIzr);
            }

            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    calendar.setTime(new Date());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int calendarSekunde = calendar.get(Calendar.SECOND);

                    int finalSat = 0;
                    int finalMinuta = 0;
                    int finalSekundeStanica;
                    int finalSekundeTrenunto;

                    double razlikaSekunde;

                    int sat = Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(0).toString().substring(0, 2));
                    int minuta = Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(0).toString().substring(3, 5));
                    finalSat = sat * 60;
                    finalMinuta = minuta;
                    finalSekundeStanica = (finalSat + finalMinuta) * 60;

                    hour = hour * 60;
                    finalSekundeTrenunto = (hour + minute) * 60;
                    finalSekundeTrenunto = finalSekundeTrenunto + calendarSekunde;

                    razlikaSekunde = finalSekundeStanica - finalSekundeTrenunto;

                    int satFinalIpis = 0;

                    long m = TimeUnit.SECONDS.toMinutes((long) razlikaSekunde) - (TimeUnit.SECONDS.toHours((long) razlikaSekunde)*60);
                    long s = TimeUnit.SECONDS.toSeconds((long) razlikaSekunde) - (TimeUnit.SECONDS.toMinutes((long) razlikaSekunde) * 60);


                    if (m >= 60){
                        satFinalIpis = (int)m/60;
                        m = m%60;
                        m = m*60;
                    }

                    if (satFinalIpis > 0){
                        if (m < 1 || m < 10){
                            if (s < 10){
                                holder.vrijeme.setText(satFinalIpis + ":0" + m + ":0" + s);
                            } else {
                                holder.vrijeme.setText(satFinalIpis + ":0" + m + ":" + s);
                            }
                        } else {
                            if (s < 10){
                                holder.vrijeme.setText(satFinalIpis + ":" + m + ":0" + s);
                            } else {
                                holder.vrijeme.setText(satFinalIpis + ":" + m + ":" + s);
                            }
                        }

                    } else {
                        if (m < 1 || m < 10) {
                            if (s < 10) {
                                holder.vrijeme.setText("0" + m + ":0" + s);
                            } else {
                                holder.vrijeme.setText("0" + m + ":" + s);
                            }
                        } else {
                            if (s < 10) {
                                holder.vrijeme.setText(m + ":0" + s);
                            } else {
                                holder.vrijeme.setText(m + ":" + s);
                            }
                        }
                    }

                    handler.postDelayed(this, 1000);
                }
            }, 10);

        // else - ako za danas ima polazaka, treba nac prvi sljedeci, ako takvog nema, nadi prvi za sutra
        } else {
            for (int i = 0; i < listaVrijemePolaska.size(); i++) {
                sat = listaVrijemePolaska.get(i).toString().substring(0, 2);
                minuta = listaVrijemePolaska.get(i).toString().substring(3, 5);

                satTmp = Integer.valueOf(sat);
                minutaTmp = Integer.valueOf(minuta);

                minutaIzracunato = minutaTmp + minutaDoStanice;

                if (minutaIzracunato >= 60) {
                    minutaIzracunato = minutaIzracunato % 60;
                    satTmp += 1;
                }

                satIzr = String.valueOf(satTmp);
                minIzr = String.valueOf(minutaIzracunato);

                // spremanje rezultata u listu
                if (satTmp < 10) {
                    satIzr = "0" + String.valueOf(satTmp);
                }

                if (minutaIzracunato < 10) {
                    minIzr = "0" + String.valueOf(minutaIzracunato);
                }

                konacnoVrijemeDoslakaNaStanicu.add(satIzr + ":" + minIzr);
            }

            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    calendar.setTime(new Date());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int calendarSekunde = calendar.get(Calendar.SECOND);

                    int finalSat = 0;
                    int finalMinuta = 0;
                    int finalSekundeStanica;
                    int finalSekundeTrenunto;

                    double razlikaSekunde;

                    for (int i = 0; i < konacnoVrijemeDoslakaNaStanicu.size(); i++) {
                        int sat = Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(i).toString().substring(0, 2));
                        int minuta = Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(i).toString().substring(3, 5));
                        if (hour == sat && minuta > minute) {
                            finalSat = hour;
                            finalMinuta = minuta;
                            break;
                        } else if (sat > hour && konacnoVrijemeDoslakaNaStanicu.size() > i+1) {
                            // ako za ovaj sat nema polazaka, nadi prvi slj. sat kad vozi AKO ima, ne finalSat = hour + 1 jer mozda slj ide za 2 ili 3 sata
                            finalSat = hour + 1;
                            finalMinuta = Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(i).toString().substring(3, 5));
                            break;
                        } else if (Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(konacnoVrijemeDoslakaNaStanicu.size() - 1).toString().substring(0, 2)) < hour) {
                            // TUUUUUU

                            String vrijemeDoStanice = database.getVrijemeDoslskaForRealTime(listLinije.get(position).toString(), listMjestoPolaska.get(position).toString(), listStanice.get(position).toString());

                            // lista za rezultat izracunatog vremena dolaska
                            final List konacnoVrijemeDoslakaNaStanicu = new ArrayList();

                            // sat od vremena polaska
                            String sat1;

                            // minuta od vremena polaska
                            String minuta1;

                            // sat kao int
                            int satTmp;

                            // minuta kao int
                            int minutaTmp;

                            // izracunata minuta
                            int minutaIzracunato;

                            // minute kao int do stanice
                            final int minutaDoStanice = Integer.valueOf(vrijemeDoStanice);

                            // satZadnje
                            String satIzr;

                            // minutaZadnje
                            String minIzr;

                            int danTmp = dan + 1;
                            String danImeTmp;
                            if (danTmp == 2 || danTmp == 3 || danTmp == 4 || danTmp == 5 || danTmp == 6) {
                                danImeTmp = "radni";
                            } else if (danTmp == 7) {
                                danImeTmp = "subota";
                            } else {
                                danImeTmp = "nedjelja";
                            }
                            List listaVrijemePolaska1 = new ArrayList<>();
                            listaVrijemePolaska1 = database.getVrijemePolaska(listLinije.get(position).toString(), listMjestoPolaska.get(position).toString(), danImeTmp);

                            for (int i1 = 0; i1 < listaVrijemePolaska1.size(); i1++) {
                                sat1 = listaVrijemePolaska1.get(i1).toString().substring(0, 2);
                                minuta1 = listaVrijemePolaska1.get(i1).toString().substring(3, 5);

                                satTmp = Integer.valueOf(sat1);
                                minutaTmp = Integer.valueOf(minuta1);

                                minutaIzracunato = minutaTmp + minutaDoStanice;

                                if (minutaIzracunato >= 60) {
                                    minutaIzracunato = minutaIzracunato % 60;
                                    satTmp += 1;
                                }

                                satIzr = String.valueOf(satTmp);
                                minIzr = String.valueOf(minutaIzracunato);

                                // spremanje rezultata u listu
                                if (satTmp < 10) {
                                    satIzr = "0" + String.valueOf(satTmp);
                                }

                                if (minutaIzracunato < 10) {
                                    minIzr = "0" + String.valueOf(minutaIzracunato);
                                }

                                konacnoVrijemeDoslakaNaStanicu.add(satIzr + ":" + minIzr);
                            }


                            int sat2 = Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(0).toString().substring(0, 2));
                            int minuta2 = Integer.valueOf(konacnoVrijemeDoslakaNaStanicu.get(0).toString().substring(3, 5));
                            finalSat = sat2;
                            finalMinuta = minuta2;

                            //////////



                            break;
                        }
                    }
                    finalSat = finalSat * 60;
                    finalSekundeStanica = (finalSat + finalMinuta) * 60;

                    hour = hour * 60;
                    finalSekundeTrenunto = (hour + minute) * 60;
                    finalSekundeTrenunto = finalSekundeTrenunto + calendarSekunde;

                    razlikaSekunde = finalSekundeStanica - finalSekundeTrenunto;

                    int satFinalIpis = 0;

                    // prvo vrijeme za sutra je manje od trenutnog npr 7:20 sutra, 20:20 danas -> ne moze vrijeme sutra-danas
                    // racunaj 24:00 - vrijeme sad + vrijeme kad ide sutra bus

                    int satOdPonociDoSad;
                    int minutaOdPonociDoSad;
                    int ukupnoSekundiOdPonociDoSad;

                    satOdPonociDoSad = 23 - hour;
                    minutaOdPonociDoSad = 60 - minute;
                    ukupnoSekundiOdPonociDoSad = ((satOdPonociDoSad * 60) + minutaOdPonociDoSad) * 60;
                    int ukupnoSekundi = finalSekundeStanica + ukupnoSekundiOdPonociDoSad;

                    //ukupnoSekudni -> ok
                    long m = TimeUnit.SECONDS.toMinutes((long) razlikaSekunde) - (TimeUnit.SECONDS.toHours((long) razlikaSekunde) * 60);
                    long s = TimeUnit.SECONDS.toSeconds((long) razlikaSekunde) - (TimeUnit.SECONDS.toMinutes((long) razlikaSekunde) * 60);


                    if (m >= 60) {
                        satFinalIpis = (int) m / 60;
                        m = m % 60;
                        m = m * 60;
                    }

                    if (satFinalIpis > 0) {
                        if (m < 1 || m < 10) {
                            if (s < 10) {
                                holder.vrijeme.setText(satFinalIpis + ":0" + m + ":0" + s);
                            } else {
                                holder.vrijeme.setText(satFinalIpis + ":0" + m + ":" + s);
                            }
                        } else {
                            if (s < 10) {
                                holder.vrijeme.setText(satFinalIpis + ":" + m + ":0" + s);
                            } else {
                                holder.vrijeme.setText(satFinalIpis + ":" + m + ":" + s);
                            }
                        }

                    } else {
                        if (m < 1 || m < 10){
                            if (s < 10) {
                                holder.vrijeme.setText("0" + m + ":0" + s);
                            } else {
                                holder.vrijeme.setText("0" + m + ":" + s);
                            }
                        } else {
                            if (s < 10) {
                                holder.vrijeme.setText(m + ":0" + s);
                            } else {
                                holder.vrijeme.setText(m + ":" + s);
                            }
                        }

                    }

                    handler.postDelayed(this, 1000);
                }
            }, 10);
        }

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
