package com.example.magnetmonitorclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MagMonCardAdapter extends RecyclerView.Adapter<MagMonCardAdapter.MagMonViewHolder> implements View.OnCreateContextMenuListener  {

    private ArrayList<MagMonRec> magmons;
    private Context context;

    MagMonCardAdapter(ArrayList<MagMonRec> magmons, Context context){
        this.magmons = magmons;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public MagMonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.magmon_card, parent, false);
        return new MagMonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MagMonViewHolder magmonViewHolder, final int i) {
        final MagMonRec node = magmons.get(i);
        magmonViewHolder.magmonName.setText(magmons.get(i).getName());
        magmonViewHolder.HePress.setText(magmons.get(i).getHePress());
        magmonViewHolder.HeLevel.setText(magmons.get(i).getHeLevel());
        magmonViewHolder.WaterTemp.setText(magmons.get(i).getWaterTemp1());
        magmonViewHolder.WaterFlow.setText(magmons.get(i).getWaterFlow1());
        magmonViewHolder.LastUpdate.setText(magmons.get(i).getLastTime());

        if (magmons.get(i).getMonitoringEnabled()) {
            magmonViewHolder.notifiImage.setImageResource(R.drawable.notifications_active);
        } else {
            magmonViewHolder.notifiImage.setImageResource(R.drawable.notifications_off);
        }

        magmonViewHolder.notifiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (magmons.get(i).getMonitoringEnabled()) {
                    magmons.get(i).setMonitoringEnabled(false);
                    MainActivity.magmonUpdateBase(node);
                    magmonViewHolder.notifiImage.setImageResource(R.drawable.notifications_off);
                } else {
                    magmons.get(i).setMonitoringEnabled(true);
                    MainActivity.magmonUpdateBase(node);
                    magmonViewHolder.notifiImage.setImageResource(R.drawable.notifications_active);
                }
            }
        });

        magmonViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MagMonPanel.class);
                intent.putExtra("magmonid",magmons.get(i).getId());
                try {
                        context.startActivity(intent);
                }catch (Exception e){
                    MainActivity.print("errorrr = "+e);
                }
            }
        });

        magmonViewHolder.menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, magmonViewHolder.menuicon);
                popupMenu.inflate(R.menu.recycler_adapter_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.itemSetting:
                            {
                                Intent intent = new Intent(context,AddMagMonServer.class);
                                intent.putExtra("method","edit");
                                intent.putExtra("serverid",magmons.get(i).getId());
                                context.startActivity(intent);

//                                if(servers.get(i).connect) {
//                                    context.startActivity(intent);
//                                }else{
//                                    Toast toast = Toast.makeText(context, R.string.connectionerror, Toast.LENGTH_SHORT); toast.show();
//                                }
                            }
                            break;
                            case R.id.itemDelete:
                            {
                                MainActivity.magmonDelBase(magmons.get(i));
                                magmons.remove(i);
                                notifyDataSetChanged();
                            }
                            break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return magmons.size();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    static class MagMonViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView magmonName;
        TextView HePress;
        RelativeLayout layot;
        ImageView menuicon;
        TextView HeLevel;
        TextView WaterTemp;
        TextView WaterFlow;
        TextView LastUpdate;
        ImageView notifiImage;

        MagMonViewHolder(View itemView)  {
            super(itemView);
            layot = itemView.findViewById(R.id.cardLayot);
            cardView = itemView.findViewById(R.id.cardView);
            magmonName = itemView.findViewById(R.id.magmonName);
            HePress = itemView.findViewById(R.id.HePress);
            HeLevel = itemView.findViewById(R.id.HeLevel);
            WaterTemp = itemView.findViewById(R.id.WaterTemp);
            WaterFlow = itemView.findViewById(R.id.WaterFlow);
            LastUpdate = itemView.findViewById(R.id.LastUpdate);
            menuicon = itemView.findViewById(R.id.editIcon);
            notifiImage = itemView.findViewById(R.id.notifiIcon);
        }
    }
}