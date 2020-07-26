package com.example.weather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.models.Days;
import com.example.weather.R;

import java.util.ArrayList;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ItemDaysHolder> {
    Context context;
    ArrayList<Days> daysArrayList;

    public DaysAdapter(Context context, ArrayList<Days> daysArrayList) {
        this.context = context;
        this.daysArrayList = daysArrayList;
    }

    @NonNull
    @Override
    public ItemDaysHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_next_days, parent, false);
        return new ItemDaysHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDaysHolder holder, int position) {
        holder.txtDate.setText(daysArrayList.get(position).getDate());
        holder.txtStatus.setText(daysArrayList.get(position).getStatus());
        holder.imgImage.setImageResource(daysArrayList.get(position).getImage());
        holder.txtMaxTemp.setText(daysArrayList.get(position).getMaxTemp());
        holder.txtMinTemp.setText(daysArrayList.get(position).getMinTemp());
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return daysArrayList.size();
    }

    public class ItemDaysHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtStatus, txtMaxTemp, txtMinTemp;
        ImageView imgImage;

        public ItemDaysHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.item_date);
            txtStatus = itemView.findViewById(R.id.item_status);
            imgImage = itemView.findViewById(R.id.item_image);
            txtMaxTemp = itemView.findViewById(R.id.item_max_temp);
            txtMinTemp = itemView.findViewById(R.id.item_min_temp);
        }

        void bindData() {
            RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (getAdapterPosition() == 0) {
                params.topMargin = itemView.getResources().getDimensionPixelOffset(R.dimen._3sdp);
            } else {
                params.topMargin = 0;
            }
            itemView.setLayoutParams(params);
        }
    }
}
