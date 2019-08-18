package com.wiicoon.rubi.wicoon_ligh_controller.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Schedule;

import java.util.List;

/**
 * Created by Rubi on 10/31/2017.
 */

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> {


    private int layout;
    private List<Schedule> schedules;
    private OnClickListenerButtonDelete onClickListenerButtonDelete;
    private OnSwitchChangeListener onSwitchChangeListener;
    private OnItemClickListener onItemClickListener;


    public ScheduleRecyclerAdapter(int layout, List<Schedule> schedules, OnClickListenerButtonDelete onClickListenerButtonDelete,
                                    OnSwitchChangeListener onSwitchChangeListener, OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.schedules = schedules;
        this.onClickListenerButtonDelete = onClickListenerButtonDelete;
        this.onSwitchChangeListener = onSwitchChangeListener;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ScheduleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        setTxtMinutes(holder, position);
        setTxtHour(holder, position);
        setTypeAction(holder, position, holder.context);

        setRepeatIndicator(holder, position);
        setDaysOfWeef(holder, position);
        setSwitch(holder, position);

        holder.btnDeleteClickListener(onClickListenerButtonDelete);
        holder.switchListener(onSwitchChangeListener);
        holder.bind(onItemClickListener);
    }





    @Override
    public int getItemCount() {
        return schedules.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView txtVieHor;
        public TextView txtVieMin;
        public Switch swtActivate;
        public TextView txtVieRepInd;
        public TextView txtVieTypeAcc;
        public ImageButton imgBtnDelete;
        public TextView txtDayWeek;

        public Context context;



        public ViewHolder(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.imgBtnDelete = (ImageButton) itemView.findViewById(R.id.imageButtonDeleteHorario);
            this.swtActivate = (Switch) itemView.findViewById(R.id.switchAlarmOnOff);
            this.txtVieHor = (TextView) itemView.findViewById(R.id.textViewHour);
            this.txtVieMin = (TextView) itemView.findViewById(R.id.textViewMinutes);
            this.txtVieRepInd = (TextView) itemView.findViewById(R.id.textViewRepeatIndicator);
            this.txtVieTypeAcc = (TextView) itemView.findViewById(R.id.textViewAccion);
            this.txtDayWeek = (TextView) itemView.findViewById(R.id.textViewDaysOfWeek);


        }



        public void btnDeleteClickListener (final ScheduleRecyclerAdapter.OnClickListenerButtonDelete listener){

            imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBtnDeleteClick( schedules.get(getAdapterPosition()).getID() );
                }
            });


        }

        public void switchListener (final ScheduleRecyclerAdapter.OnSwitchChangeListener listener){

            swtActivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onSwitchChanged(isChecked, schedules.get(getAdapterPosition()).getID());
                }
            });
        }

        public void bind (final ScheduleRecyclerAdapter.OnItemClickListener listener){

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition(),schedules.get(getAdapterPosition()).getID());
                }
            });
        }
    }




    public interface OnClickListenerButtonDelete {
        void onBtnDeleteClick(int idSchedule);

    }

    public interface OnSwitchChangeListener {
        void onSwitchChanged(boolean isChecked , int idSch);

    }

    public interface OnItemClickListener  {
        void onItemClick(int position, int idSche);

    }

    private void setTypeAction(ViewHolder holder, int position, Context context) {
        if (schedules.get(position).getTypeTurn() == Schedule.TYPE_TURN_ON){
            holder.txtVieTypeAcc.setText("ON");
            holder.txtVieTypeAcc.setBackgroundColor(context.getResources().getColor(R.color.colorConnected));
        }
        else{
            holder.txtVieTypeAcc.setText("OFF");
            holder.txtVieTypeAcc.setBackgroundColor(context.getResources().getColor(R.color.colorButtonRedFocused));
        }
    }

    private void  setRepeatIndicator(ViewHolder holder, int position){
        if(schedules.get(position).getLifeCycle() == Schedule.REPEAT_ALWAYS){
            holder.txtVieRepInd.setText("SI");
        }
        else {
            holder.txtVieRepInd.setText("NO");
        }
    }

    private void setDaysOfWeef(ViewHolder holder, int position){

        String daysString = new String();
        for(int i = 0; i<7; i++){
            if(( schedules.get(position).getDays() & dosAlaN(i+1) ) > 0){
                switch(i){
                    case 0: daysString = daysString+" DOM";  break;
                    case 1: daysString = daysString+" LUN";  break;
                    case 2: daysString = daysString+" MAR";  break;
                    case 3: daysString = daysString+" MIE";  break;
                    case 4: daysString = daysString+" JUE";  break;
                    case 5: daysString = daysString+" VIE";  break;
                    case 6: daysString = daysString+" SAB";  break;
                    default: break;

                }
            }

        }
        holder.txtDayWeek.setText(daysString);
    }

    private void setSwitch(ViewHolder holder, int position){
        if(schedules.get(position).isActivate()){
            holder.swtActivate.setChecked(true);

        }
        else {
            holder.swtActivate.setChecked(false);

        }

    }
    private void setTxtHour(ViewHolder holder, int position){
        if(schedules.get(position).getHour()<10)
            holder.txtVieHor.setText("0"+schedules.get(position).getHour());
        else
            holder.txtVieHor.setText(""+schedules.get(position).getHour());
    }

    private void setTxtMinutes(ViewHolder holder, int position){
        if(schedules.get(position).getMinute()<10)
            holder.txtVieMin.setText("0"+schedules.get(position).getMinute());
        else
            holder.txtVieMin.setText(""+schedules.get(position).getMinute());
    }


    private int dosAlaN(int n){
        int result = 1;
        if(n == 0)
        return 1;

        for(int i=0;i<n;i++){
            result = result * 2;
        }
        return result;
    }

}