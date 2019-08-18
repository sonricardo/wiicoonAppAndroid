package com.wiicoon.rubi.wicoon_ligh_controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;

import java.util.List;

/**
 * Created by Rubi on 11/3/2017.
 */

public class FocosDialogAdapter extends BaseAdapter {


    private List<Foco> focos;
    private Context context;
    private int layout;



    public FocosDialogAdapter(Context context, int layout, List<Foco> focos) {
        this.context = context;
        this.layout = layout;
        this.focos = focos;



    }

    @Override
    public int getCount() {
        return this.focos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.focos.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        FocosDialogAdapter.ViewHolder holder;
        Foco currentFoco = new Foco();
        currentFoco = focos.get(position);



        if (convertView == null) {


            convertView = LayoutInflater.from(context).inflate(layout, null);
            holder = new FocosDialogAdapter.ViewHolder();
            holder.nameText = (TextView) convertView.findViewById(R.id.textViewName);
            convertView.setTag(holder);

        } else {
            holder = (FocosDialogAdapter.ViewHolder) convertView.getTag();
        }


        holder.nameText.setText(currentFoco.getName());


         return convertView;
    }


    static class ViewHolder {
        private TextView nameText;


    }


}