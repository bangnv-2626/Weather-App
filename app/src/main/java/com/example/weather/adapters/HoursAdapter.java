package com.example.weather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.models.Hours;
import com.example.weather.R;

import java.util.ArrayList;

public class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.ItemHoursHolder> {
    Context context;
    ArrayList<Hours> hoursArrayList;

    public HoursAdapter(Context context, ArrayList<Hours> hoursArrayList) {
        this.context = context;
        this.hoursArrayList = hoursArrayList;
    }

    @NonNull
    @Override
    public ItemHoursHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_hours, parent, false);
//        Log.d("Ketqua hourAdapter", "onCreateViewHolder: called");
        return new ItemHoursHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHoursHolder holder, int position) {

        holder.txtHour.setText(hoursArrayList.get(position).getHour());
        holder.imgImage.setImageResource(hoursArrayList.get(position).getImage());
        holder.txtTemp.setText(hoursArrayList.get(position).getTemp());
        holder.bindData();

//        Log.d("Ketqua hourAdapter", "onBindViewHolder: called");

    }

    @Override
    public int getItemCount() {
        return hoursArrayList.size();
    }

    public class ItemHoursHolder extends RecyclerView.ViewHolder {
        TextView txtHour, txtTemp;
        ImageView imgImage;

        public ItemHoursHolder(@NonNull View itemView) {
            super(itemView);
            txtHour = itemView.findViewById(R.id.item_hour);
            txtTemp = itemView.findViewById(R.id.item_temp);
            imgImage = itemView.findViewById(R.id.item_image);
        }

        void bindData() {
            RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (getAdapterPosition() == 0) {
                params.leftMargin = itemView.getResources().getDimensionPixelOffset(R.dimen._8sdp);
            } else {
                params.leftMargin = 0;
            }
            itemView.setLayoutParams(params);
        }
    }
}
