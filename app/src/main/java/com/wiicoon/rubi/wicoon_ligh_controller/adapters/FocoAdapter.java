package com.wiicoon.rubi.wicoon_ligh_controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.util.List;

import static com.wiicoon.rubi.wicoon_ligh_controller.R.color.colorDisconnected;

/**
 * Created by Rubi on 8/22/2017.
 */

public class FocoAdapter extends BaseAdapter {


    private List<Foco> focos;
    private Context context;
    private int layout;
    private List<Placa> placas;




    public FocoAdapter(Context context, int layout, List<Foco> focos, List<Placa> placas){
        this.context = context;
        this.layout = layout;
        this.focos = focos;
        this.placas = placas;


    }

    @Override
    public int getCount() {
        return this.focos.size();
    }

    @Override
    public Object getItem(int position) { return this.focos.get(position);}

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        Foco currentFoco = new Foco();
        currentFoco = focos.get(position);
        int status = findStatus(currentFoco.getIDplaca(),placas);



        if (convertView == null) {


            convertView = LayoutInflater.from(context).inflate(layout, null);
            holder = new ViewHolder();


            holder.nameText = (TextView) convertView.findViewById(R.id.textViewName);
            holder.imgViewLight = (ImageView) convertView.findViewById(R.id.imageViewLight);
            holder.imgClock = (ImageView) convertView.findViewById(R.id.ImageClock);
            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }



        holder.nameText.setText(currentFoco.getName());
        if(currentFoco.getNumDeSalida()<4) {
            if (currentFoco.getEstado() == Foco.ON)
                holder.imgViewLight.setImageResource(R.mipmap.ic_light_on);
            else {
                holder.imgViewLight.setImageResource(R.mipmap.ic_light_off);
            }
        }

        else{
            if(currentFoco.getEstado()== Foco.ON)
                holder.imgViewLight.setImageResource(R.mipmap.ic_socket_play);
            else{
                holder.imgViewLight.setImageResource(R.mipmap.ic_socket_stop);
            }

        }

           // holder.imgClock.setImageResource(R.mipmap.ic_clock);

        if( currentFoco.getSchedules()!= null) {
            if( currentFoco.getSchedules().size()>0) {
                holder.imgClock.setVisibility(View.VISIBLE);
            }
            else
                holder.imgClock.setVisibility(View.INVISIBLE);
        }
        else
            holder.imgClock.setVisibility(View.INVISIBLE);

        switch(status){
            case Placa.CONNECTED:    convertView.setBackgroundColor( context.getResources().getColor(R.color.colorConnected)); break;
            case Placa.AP_MODE:   convertView.setBackgroundColor( context.getResources().getColor(R.color.colorApMode)); break;
            case Placa.INTERNET_REMOTE: convertView.setBackgroundColor( context.getResources().getColor(R.color.colorInternetRemote)); break;
            default:  convertView.setBackgroundColor( context.getResources().getColor(colorDisconnected)); break;
        }



        return convertView;
    }




    static class ViewHolder{
        private TextView nameText;
        private ImageView imgViewLight;
        private ImageView imgClock;

    }

    private int findStatus(int ID, List<Placa> placas){

        int status = -1;

        for (int i=0; i < placas.size(); i++) {
            if (placas.get(i).getID() == ID)
                status = placas.get(i).getStatus();

        }
        return status;
    }



}

