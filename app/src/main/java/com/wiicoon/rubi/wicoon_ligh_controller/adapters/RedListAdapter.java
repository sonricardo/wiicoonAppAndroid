package com.wiicoon.rubi.wicoon_ligh_controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wiicoon.rubi.wicoon_ligh_controller.R;

import java.util.List;

/**
 * Created by Rubi on 10/26/2017.
 */

public class RedListAdapter extends BaseAdapter {



    private Context context;
    private int layout;
    private List<String> redes;




    public RedListAdapter(Context context, int layout, List<String> redes){
        this.context = context;
        this.layout = layout;
        this.redes = redes;


    }

    @Override
    public int getCount() {
        return this.redes.size();
    }

    @Override
    public Object getItem(int position) { return this.redes.get(position);}

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(layout, null);
            holder = new ViewHolder();

            holder.textViewMacStatic = (TextView) convertView.findViewById(R.id.textViewMacStatic);
            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.textViewMacStatic.setText(redes.get(position));

        return convertView;
    }




    static class ViewHolder{
        private TextView textViewMacStatic;


    }

}