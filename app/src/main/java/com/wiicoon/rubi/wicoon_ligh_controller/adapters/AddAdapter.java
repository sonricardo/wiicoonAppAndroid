package com.wiicoon.rubi.wicoon_ligh_controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;

import java.util.List;

/**
 * Created by Rubi on 8/29/2017.
 */

public class AddAdapter extends BaseAdapter {


    private List<Foco> focos;
    private Context context;
    private int layout;






    public AddAdapter(Context context, int layout, List<Foco> focos){
        this.context = context;
        this.layout = layout;
        this.focos = focos;


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
    public View getView(int position, View convertView, final ViewGroup viewGroup) {

        final AddAdapter.ViewHolder holder;
        Foco currentFoco = new Foco();
        currentFoco = focos.get(position);




        if (convertView == null) {


            convertView = LayoutInflater.from(context).inflate(layout, null);
            holder = new AddAdapter.ViewHolder();



            holder.nameText = (EditText) convertView.findViewById(R.id.editTextNameNew);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxFoco);
            holder.checkBox.setOnCheckedChangeListener(mCheckedChanceChangeListener);
            holder.btnTest = (Button) convertView.findViewById(R.id.buttonTest);
            holder.btnTest.setOnClickListener(btnTestListener);
            convertView.setTag(holder);

        }
        else{
            holder = (AddAdapter.ViewHolder) convertView.getTag();
        }





          holder.nameText.setText(currentFoco.getName());
       // holder.checkBox.setChecked(currentFoco.getCreado());





        return convertView;
    }




    static class ViewHolder  {
        private EditText nameText;
        private Button   btnTest;
        private CheckBox checkBox;

    }


  private View.OnClickListener btnTestListener = new View.OnClickListener()
  {
      @Override
      public void onClick(View v) {
          Toast.makeText(context, "estamos aqui....",Toast.LENGTH_LONG).show();

      }
  };

  private CompoundButton.OnCheckedChangeListener mCheckedChanceChangeListener = new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          Toast.makeText(context, "en lo que es mexico....",Toast.LENGTH_LONG).show();
      }
  };

}


