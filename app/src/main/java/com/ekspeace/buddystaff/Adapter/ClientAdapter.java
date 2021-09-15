package com.ekspeace.buddystaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddystaff.Model.Category;
import com.ekspeace.buddystaff.Model.Client;
import com.ekspeace.buddystaff.Model.EventBus.DeleteEvent;
import com.ekspeace.buddystaff.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyViewHolder> {
    private Context context;
    private List<Client> clientList;

    public ClientAdapter(Context context, List<Client> clientList) {
        this.context = context;
        this.clientList = clientList;
    }

    @NonNull
    @NotNull
    @Override
    public ClientAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_clients_recycler, parent, false);
        return new ClientAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ClientAdapter.MyViewHolder holder, int position) {
        Client client = clientList.get(position);
        holder.client_name.setText(client.getName());
        holder.client_phone.setText(client.getPhone());
        holder.client_email.setText(client.getEmail());
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView client_name;
        private TextView client_phone;
        private TextView client_email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            client_name = itemView.findViewById(R.id.client_name);
            client_phone = itemView.findViewById(R.id.client_phone);
            client_email = itemView.findViewById(R.id.client_email);
        }
        

    }

}
