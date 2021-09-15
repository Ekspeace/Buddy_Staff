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

import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.EventBus.DeleteEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.firebase.Timestamp;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AcceptedAdapter extends RecyclerView.Adapter<AcceptedAdapter.MyViewHolder>{
    private Context context;
    private List<PickInformation> pickInformationList;
    private List<BookingInformation> bookingInformationList;

    public AcceptedAdapter(Context context, List<PickInformation> pickInformations, List<BookingInformation> bookingInformations) {
        this.context = context;
        this.pickInformationList = pickInformations;
        this.bookingInformationList = bookingInformations;
    }
    @NonNull
    @NotNull
    @Override
    public AcceptedAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_accept_recycler, parent, false);
        return new AcceptedAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AcceptedAdapter.MyViewHolder holder, int position) {
        if(bookingInformationList == null){
            PickInformation pickInformation = pickInformationList.get(position);
            if(pickInformation.getVerified().equals("Confirmed")) {
                holder.accept_service_name.setText(pickInformation.getServiceName());
                holder.accept_service_specs.setText(pickInformation.getPickUpType().concat("\n").concat(pickInformation.getPickUpInfo()));
                holder.accept_service_date_time.setText(pickInformation.getDateTime());
                holder.accept_service_price.setText(pickInformation.getServicePrice());

                holder.accept_client_name.setText(pickInformation.getCustomerName());
                holder.accept_client_phone.setText(pickInformation.getCustomerPhone());
                holder.accept_client_address.setText(pickInformation.getCustomerAddress());

                if(pickInformation.getDateTime().contains("Right")){
                    if(pickInformation.getTimestamp().toDate().getTime() < Timestamp.now().toDate().getTime())
                        holder.accept_done.setText("Yes");
                    else
                        holder.accept_done.setText("No");
                }else {
                    String[] splitDate = pickInformation.getDateTime().split("at");
                    String Date = splitDate[1];
                    String currentDate = Common.simpleDataFormat.format(Calendar.getInstance().getTime());
                    if (currentDate.compareTo(Date) > 0)
                        holder.accept_done.setText("No");
                    else
                        holder.accept_done.setText("Yes");
                }
                holder.accept_delete.setOnClickListener(view -> {
                    EventBus.getDefault().postSticky(new DeleteEvent(pickInformation));
                });
            }else {
                pickInformationList.clear();
            }
        }else {
            BookingInformation bookingInformation = bookingInformationList.get(position);
            if(bookingInformation.getVerified().equals("Confirmed")) {
                holder.accept_service_name.setText(bookingInformation.getServiceName());
                holder.accept_service_specs.setText(bookingInformation.getCategoryName());
                holder.accept_service_date_time.setText(bookingInformation.getTime());
                holder.accept_service_price.setText(bookingInformation.getServicePrice());

                holder.accept_client_name.setText(bookingInformation.getCustomerName());
                holder.accept_client_phone.setText(bookingInformation.getCustomerPhone());
                holder.accept_client_address.setText(bookingInformation.getCustomerAddress());

                String[] splitDate = bookingInformation.getTime().split("at");
                String Date = splitDate[1];
                String currentDate = Common.simpleDataFormat.format(Calendar.getInstance().getTime());
                if(currentDate.compareTo(Date) > 0)
                  holder.accept_done.setText("No");
                else
                    holder.accept_done.setText("Yes");
                holder.accept_delete.setOnClickListener(view -> {
                    EventBus.getDefault().postSticky(new DeleteEvent(bookingInformation));
                });
            }else {
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
        private TextView accept_service_name, accept_service_specs, accept_service_date_time, accept_service_price;
        private TextView accept_client_name, accept_client_phone, accept_client_address;
        private ImageView accept_delete;
        private TextView accept_done;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            accept_service_name = itemView.findViewById(R.id.accept_service_name);
            accept_service_specs = itemView.findViewById(R.id.accept_service_specs);
            accept_service_date_time = itemView.findViewById(R.id.accept_service_date_and_time);
            accept_service_price = itemView.findViewById(R.id.accept_service_price);

            accept_client_name = itemView.findViewById(R.id.accept_client_name);
            accept_client_phone = itemView.findViewById(R.id.accept_client_phone);
            accept_client_address = itemView.findViewById(R.id.accept_client_address);

            accept_done = itemView.findViewById(R.id.accept_done);
            accept_delete = itemView.findViewById(R.id.accept_delete);

        }
    }
}