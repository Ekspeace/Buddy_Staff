package com.ekspeace.buddystaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder>{
    private Context context;
    private List<PickInformation> pickInformationList;
    private List<BookingInformation> bookingInformationList;
    private LinearLayout loading;

    public PendingAdapter(Context context, List<PickInformation> pickInformations, List<BookingInformation> bookingInformations,  LinearLayout loading) {
        this.context = context;
        this.pickInformationList = pickInformations;
        this.bookingInformationList = bookingInformations;
        this.loading = loading;
    }
    @NonNull
    @NotNull
    @Override
    public PendingAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_pending_recycler, parent, false);
        return new PendingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PendingAdapter.MyViewHolder holder, int position) {
        if(bookingInformationList == null){
            PickInformation pickInformation = pickInformationList.get(position);
            if(pickInformation.getVerified().equals("Pending")) {
                holder.pending_service_name.setText(pickInformation.getServiceName());
                holder.pending_service_specs.setText(pickInformation.getPickUpType().concat("\n").concat(pickInformation.getPickUpInfo()));
                holder.pending_service_date_time.setText(pickInformation.getDateTime());
                holder.pending_service_price.setText(pickInformation.getServicePrice());

                holder.pending_client_name.setText(pickInformation.getCustomerName());
                holder.pending_client_phone.setText(pickInformation.getCustomerPhone());
                holder.pending_client_address.setText(pickInformation.getCustomerAddress());

                holder.pending_accept.setOnClickListener(view -> {
                    PopUp.pickPrice(context, pickInformation, loading);
                });
                holder.pending_decline.setOnClickListener(view -> {
                    PopUp.declineReason(context, loading, pickInformation.getCustomerId(), pickInformation.getCustomerPlayerId() ,pickInformation.getServiceId(), pickInformation.getServiceName(), view);
                });
            }else {
                pickInformationList.clear();
            }
        }else {
            BookingInformation bookingInformation = bookingInformationList.get(position);
            if(bookingInformation.getVerified().equals("Pending")) {
                holder.pending_service_name.setText(bookingInformation.getServiceName());
                holder.pending_service_specs.setText(bookingInformation.getCategoryName());
                holder.pending_service_date_time.setText(bookingInformation.getTime());
                holder.pending_service_price.setText(bookingInformation.getServicePrice());

                holder.pending_client_name.setText(bookingInformation.getCustomerName());
                holder.pending_client_phone.setText(bookingInformation.getCustomerPhone());
                holder.pending_client_address.setText(bookingInformation.getCustomerAddress());

                holder.pending_accept.setOnClickListener(view -> {
                    PopUp.bookingVerify(context, bookingInformation, loading);
                });
                holder.pending_decline.setOnClickListener(view -> {
                    PopUp.declineReason(context, loading, bookingInformation.getCustomerId(), bookingInformation.getCustomerPlayerId(), bookingInformation.getServiceId(), bookingInformation.getServiceName(), view);
                });
            }else{
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
        private TextView pending_service_name, pending_service_specs, pending_service_date_time, pending_service_price;
        private TextView pending_client_name, pending_client_phone, pending_client_address;
        private ImageView pending_accept, pending_decline;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pending_service_name = itemView.findViewById(R.id.pending_service_name);
            pending_service_specs = itemView.findViewById(R.id.pending_service_specs);
            pending_service_date_time = itemView.findViewById(R.id.pending_service_date_and_time);
            pending_service_price = itemView.findViewById(R.id.pending_service_price);

            pending_client_name = itemView.findViewById(R.id.pending_client_name);
            pending_client_phone = itemView.findViewById(R.id.pending_client_phone);
            pending_client_address = itemView.findViewById(R.id.pending_client_address);

            pending_accept = itemView.findViewById(R.id.pending_accept);
            pending_decline = itemView.findViewById(R.id.pending_decline);

        }
    }
}
