package com.ekspeace.buddystaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.EventBus.DeleteEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.firebase.Timestamp;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class DeclineAdapter extends RecyclerView.Adapter<DeclineAdapter.MyViewHolder>{
    private Context context;
    private List<PickInformation> pickInformationList;
    private List<BookingInformation> bookingInformationList;

    public DeclineAdapter(Context context, List<PickInformation> pickInformations, List<BookingInformation> bookingInformations) {
        this.context = context;
        this.pickInformationList = pickInformations;
        this.bookingInformationList = bookingInformations;
    }
    @NonNull
    @NotNull
    @Override
    public DeclineAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_declined_recycler, parent, false);
        return new DeclineAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeclineAdapter.MyViewHolder holder, int position) {
        if(bookingInformationList == null){
            PickInformation pickInformation = pickInformationList.get(position);
            if(pickInformation.getVerified().equals("Declined")) {
                holder.decline_service_name.setText(pickInformation.getServiceName());
                holder.decline_service_specs.setText(pickInformation.getPickUpType().concat("\n").concat(pickInformation.getPickUpInfo()));
                holder.decline_service_date_time.setText(pickInformation.getDateTime());
                holder.decline_service_price.setText(pickInformation.getServicePrice());

                holder.decline_client_name.setText(pickInformation.getCustomerName());
                holder.decline_client_phone.setText(pickInformation.getCustomerPhone());
                holder.decline_client_address.setText(pickInformation.getCustomerAddress());
                
                holder.decline_delete.setOnClickListener(view -> {
                    EventBus.getDefault().postSticky(new DeleteEvent(pickInformation));
                });
            }else {
                pickInformationList.clear();
            }
        }else {
            BookingInformation bookingInformation = bookingInformationList.get(position);
            if(bookingInformation.getVerified().equals("Declined")) {
                holder.decline_service_name.setText(bookingInformation.getServiceName());
                holder.decline_service_specs.setText(bookingInformation.getCategoryName());
                holder.decline_service_date_time.setText(bookingInformation.getTime());
                holder.decline_service_price.setText(bookingInformation.getServicePrice());

                holder.decline_client_name.setText(bookingInformation.getCustomerName());
                holder.decline_client_phone.setText(bookingInformation.getCustomerPhone());
                holder.decline_client_address.setText(bookingInformation.getCustomerAddress());
                
                holder.decline_delete.setOnClickListener(view -> {
                    EventBus.getDefault().postSticky(new DeleteEvent(bookingInformation));
                });
            }
            else {
                bookingInformationList.clear();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(bookingInformationList == null){
            return pickInformationList.size();
        }else {
            return bookingInformationList.size();
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView decline_service_name, decline_service_specs, decline_service_date_time, decline_service_price;
        private TextView decline_client_name, decline_client_phone, decline_client_address;
        private ImageView decline_delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            decline_service_name = itemView.findViewById(R.id.decline_service_name);
            decline_service_specs = itemView.findViewById(R.id.decline_service_specs);
            decline_service_date_time = itemView.findViewById(R.id.decline_service_date_and_time);
            decline_service_price = itemView.findViewById(R.id.decline_service_price);

            decline_client_name = itemView.findViewById(R.id.decline_client_name);
            decline_client_phone = itemView.findViewById(R.id.decline_client_phone);
            decline_client_address = itemView.findViewById(R.id.decline_client_address);
            
            decline_delete = itemView.findViewById(R.id.decline_delete);

        }
    }
}