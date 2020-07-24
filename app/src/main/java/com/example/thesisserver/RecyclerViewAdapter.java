package com.example.thesisserver;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<String> devices;
    private Context context;

    RecyclerViewAdapter(Context c, List<String> devices){
       this.context = c; this.devices = devices;

    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_devices,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final int p = i;
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(context,QueryDeviceActivity.class);
                    intent.putExtra("deviceName",devices.get(p));
                    context.startActivity(intent);
                }catch (Exception err){
                    err.printStackTrace();
                    Toast.makeText(context, "Unable to open device details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.deviceNameTxt.setText(devices.get(i));

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout ;

        public TextView deviceNameTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNameTxt = itemView.findViewById(R.id.deviceNameTxt);
            layout = itemView.findViewById(R.id.deviceItemLayout);
        }
    }

}
