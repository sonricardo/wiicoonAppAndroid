package com.wiicoon.rubi.wicoon_ligh_controller.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;

import java.util.List;

/**
 * Created by Rubi on 8/30/2017.
 */

    public class AddRecyclerAdapter extends RecyclerView.Adapter<AddRecyclerAdapter.ViewHolder>  {



    private List<Foco> focos;
    private int layout;
    private OnClickListener buttonTestListener;
    private OnCheckedChangeListener checkedChangeListener;
    private OnTextChangedListener textWatcher;

    public AddRecyclerAdapter(List<Foco> focos, int layout, OnClickListener onClickListener, OnCheckedChangeListener onCheckedChangeListener,
                             OnTextChangedListener textWatcher) {
        this.focos = focos;
        this.layout = layout;
        this.buttonTestListener = onClickListener;
        this.checkedChangeListener = onCheckedChangeListener;
        this.textWatcher = textWatcher;
    }



    @Override
    public AddRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AddRecyclerAdapter.ViewHolder holder, int position) {

        if( focos.get(position).getName() != null) {
            holder.edtTxtFoco.setText(focos.get(position).getName());
            holder.checkBox.setChecked(true);
            checkedChangeListener.onCheckedChange(focos.get(position).getNumDeSalida(), holder.checkBox.isChecked());
        }
        else {
            holder.edtTxtFoco.setHint("name_foco_" + focos.get(position).getNumDeSalida());
        }

        holder.btnSetOnclickListener(buttonTestListener);
        holder.checkOnClickListener(checkedChangeListener);
        holder.txtProgressListener(textWatcher);


    }

    @Override
    public int getItemCount() {
        return focos.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        public EditText edtTxtFoco;
        public CheckBox checkBox;
        public Button   buttonTest;
        public Context context;


        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            this.edtTxtFoco = (EditText) itemView.findViewById(R.id.editTextNameNew);
            this.buttonTest = (Button) itemView.findViewById(R.id.buttonTest);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.checkBoxFoco);
        }




       public void btnSetOnclickListener (final OnClickListener listener){

           buttonTest.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                listener.onBtnClick( focos.get(getAdapterPosition()).getNumDeSalida() );
               }
           });


       }

        public void checkOnClickListener(final OnCheckedChangeListener listener){

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    listener.onCheckedChange(focos.get(getAdapterPosition()).getNumDeSalida(),checkBox.isChecked());
                }
            });
        }

        public void txtProgressListener(final OnTextChangedListener listener){

            edtTxtFoco.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                   // listener.onProgressChangedListener(focos.get(getAdapterPosition()).getNumDeSalida(), checkBox.isChecked());

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(edtTxtFoco.getText().toString().length() <= 0) {
                        checkBox.setChecked(false);
                    }
                    else {
                        checkBox.setChecked(true);
                    }
                    listener.onTextChangedListener(focos.get(getAdapterPosition()).getNumDeSalida(), edtTxtFoco.getText().toString());
                }
            });

        }





    }

    public interface OnClickListener{
        void onBtnClick(int numSalida);

    }

    public interface OnCheckedChangeListener{
        void onCheckedChange(int position, boolean checkedPosition);

    }

    public interface OnTextChangedListener{
        void onTextChangedListener(int position, String focoName);
        void onProgressChangedListener(int position ,  boolean checkedPosition);


    }


}
