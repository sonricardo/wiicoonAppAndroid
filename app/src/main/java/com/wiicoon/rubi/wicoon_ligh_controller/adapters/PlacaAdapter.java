package com.wiicoon.rubi.wicoon_ligh_controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.util.List;

import static com.wiicoon.rubi.wicoon_ligh_controller.R.color.colorDisconnected;

/**
 * Created by Rubi on 8/22/2017.
 */

public class PlacaAdapter extends BaseAdapter {



        private Context context;
        private int layout;
        private List<Placa> placas;




        public PlacaAdapter(Context context, int layout, List<Placa> placas){
            this.context = context;
            this.layout = layout;
            this.placas = placas;


        }

        @Override
        public int getCount() {
            return this.placas.size();
        }

        @Override
        public Object getItem(int position) { return this.placas.get(position);}

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            ViewHolder holder;

            Placa currentPlaca = placas.get(position);


            if (convertView == null) {


                convertView = LayoutInflater.from(context).inflate(layout, null);
                holder = new ViewHolder();


                holder.textViewMacStatic = (TextView) convertView.findViewById(R.id.textViewMacStatic);
                holder.textViewMac = (TextView) convertView.findViewById(R.id.textViewMac);

                holder.textViewFocosNames = (TextView) convertView.findViewById(R.id.textViewFocosNames);
               // holder.textViewDispStatic = (TextView) convertView.findViewById(R.id.textViewDispStatic);
                holder.textViewDisp = (TextView) convertView.findViewById(R.id.textViewDisp);

                convertView.setTag(holder);

            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            String focosNamesLine = getFocosName(currentPlaca);

            holder.textViewMacStatic.setText("MAC: ");
            holder.textViewMac.setText(currentPlaca.getMacAddress());

            holder.textViewFocosNames.setText(focosNamesLine);
            //holder.textViewDispStatic.setText("SAL_DISP: ");
            holder.textViewDisp.setText(currentPlaca.getNumSalidas()- currentPlaca.getFocos().size()+"");


            switch(currentPlaca.getStatus()){
                case Placa.CONNECTED:    convertView.setBackgroundColor( context.getResources().getColor(R.color.colorConnected)); break;
                case Placa.AP_MODE:   convertView.setBackgroundColor( context.getResources().getColor(R.color.colorApMode)); break;
                default:  convertView.setBackgroundColor( context.getResources().getColor(colorDisconnected)); break;
            }



            return convertView;
        }




        static class ViewHolder{
            private TextView textViewMacStatic;
            private TextView textViewMac;
            private TextView textViewSSIDstatic;
            private TextView textViewSSID;
            private TextView textViewFocosNames;
           // private TextView textViewDispStatic;
            private TextView textViewDisp;

        }


        private String getFocosName(Placa placa){

            String names = " ";

            for(int i=0;  i < placa.getFocos().size(); i++){
                names = names+" | "+ placa.getFocos().get(i).getName()+" | "+"   ";

            }

            return names;

        }


    }



