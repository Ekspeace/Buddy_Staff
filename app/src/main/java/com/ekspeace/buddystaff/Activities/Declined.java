package com.ekspeace.buddystaff.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekspeace.buddystaff.Adapter.AcceptedAdapter;
import com.ekspeace.buddystaff.Adapter.DeclineAdapter;
import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Interface.IAcceptLoadListener;
import com.ekspeace.buddystaff.Interface.IDeclineLoadListener;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Declined extends AppCompatActivity implements IDeclineLoadListener {
    private Toolbar toolbar;
    private TextView headerText;
    private RecyclerView recyclerView;
    private LinearLayout loading;
    private IDeclineLoadListener iDeclineLoadListener;
    private BookingInformation bookingInformation;
    private PickInformation pickInformation;
    private View layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declined);
        initView();
        Actionbar();
        loadDeclinedServices();
    }
    private void initView(){
        toolbar = findViewById(R.id.declined_toolbar);
        headerText = findViewById(R.id.declined_header_text);
        recyclerView = findViewById(R.id.declined_recycler_view);
        loading = findViewById(R.id.declined_progressbar);
        iDeclineLoadListener = this;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
    }
    private void loadDeclinedServices(){
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            if(Common.currentInfoName == "Booking") {
                headerText.setText("Booking declined requests");
                CollectionReference bookings = FirebaseFirestore.getInstance()
                        .collection("Bookings");
                List<BookingInformation> bookingInformationList = new ArrayList<>();
                bookings
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                            bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                            bookingInformationList.add(bookingInformation);
                                        }
                                        iDeclineLoadListener.onDeclineLoadSuccess(bookingInformationList, null);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(e -> iDeclineLoadListener.onDeclineLoadFail(e.getMessage()));
            }else {
                headerText.setText("Pick up declined requests");
                CollectionReference pickUps = FirebaseFirestore.getInstance()
                        .collection("Pick Ups");
                List<PickInformation> pickInformationList = new ArrayList<>();
                pickUps
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                            pickInformation = queryDocumentSnapshot.toObject(PickInformation.class);
                                            pickInformationList.add(pickInformation);
                                        }
                                        iDeclineLoadListener.onDeclineLoadSuccess(null, pickInformationList);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(e -> iDeclineLoadListener.onDeclineLoadFail(e.getMessage()));
            }
        } else {
            PopUp.Network(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void DeleteBooking(BookingInformation bookingInformation) {
        Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.pop_up);

        TextView tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextView tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        ImageView imIcon = alertDialog.findViewById(R.id.dialog_icon);
        Button btnClose = alertDialog.findViewById(R.id.dialog_close);
        Button btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


        tvTitle.setText("Delete booking");
        tvDesc.setText("Do you really want to delete this booking information?");
        imIcon.setImageResource(R.drawable.delete);

        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                deleteBookingFromSlot(bookingInformation);
                alertDialog.cancel();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    public void DeletePickUp(PickInformation pickInformation) {
        Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.pop_up);

        TextView tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextView tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        ImageView imIcon = alertDialog.findViewById(R.id.dialog_icon);
        Button btnClose = alertDialog.findViewById(R.id.dialog_close);
        Button btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


        tvTitle.setText("Delete Pick up");
        tvDesc.setText("Do you really want to delete this pick up information?");
        imIcon.setImageResource(R.drawable.delete);

        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                deletePickUpFromUser(pickInformation);
                alertDialog.cancel();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    private void deleteBookingFromSlot(BookingInformation bookingInformation) {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            DocumentReference BookingInfo = FirebaseFirestore.getInstance()
                    .collection(BookingSlot(bookingInformation.getServiceName()))
                    .document("Slot")
                    .collection(Common.convertTimeStampToStringKey(bookingInformation.getTimestamp()))
                    .document(bookingInformation.getSlot().toString());
            BookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    PopUp.Toast(Declined.this, layout, R.drawable.error,e.getMessage(),Toast.LENGTH_SHORT);
                    loading.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deleteBookingFromUser(bookingInformation);
                }
            });
        } else {
            PopUp.Network(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void deleteBookingFromUser(BookingInformation bookingInformation) {
        if (Common.isOnline(this)) {
            DocumentReference userBookingInfo1 = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(bookingInformation.getCustomerId())
                    .collection(BookingService(bookingInformation.getServiceName()))
                    .document(bookingInformation.getServiceId());

            userBookingInfo1.delete().addOnFailureListener(e -> {
                PopUp.Toast(Declined.this, layout, R.drawable.error, e.getMessage(),Toast.LENGTH_SHORT);
                loading.setVisibility(View.GONE);
            }).addOnSuccessListener(aVoid -> {
                loading.setVisibility(View.GONE);
                PopUp.Toast(Declined.this, layout, R.drawable.success,"Successfully deleted the booking !",Toast.LENGTH_SHORT);
                DocumentReference userOrder = FirebaseFirestore.getInstance()
                        .collection("Bookings").document(bookingInformation.getServiceId());
                userOrder.delete();
                loadDeclinedServices();

            });
        }else{
            PopUp.Network(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void deletePickUpFromUser(PickInformation pickInformation) {
        if (Common.isOnline(this)) {
            DocumentReference userPick = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(pickInformation.getCustomerId())
                    .collection("Pick Up")
                    .document(pickInformation.getServiceId());

            userPick.delete().addOnFailureListener(e -> {
                PopUp.Toast(Declined.this, layout, R.drawable.error, e.getMessage(),Toast.LENGTH_SHORT);
                loading.setVisibility(View.GONE);
            }).addOnSuccessListener(aVoid -> {
                loading.setVisibility(View.GONE);
                PopUp.Toast(Declined.this, layout, R.drawable.success,"Successfully deleted a pick up !",Toast.LENGTH_SHORT);
                DocumentReference pick = FirebaseFirestore.getInstance()
                        .collection("Pick Ups").document(pickInformation.getServiceId());
                pick.delete();
                loadDeclinedServices();

            });
        }else{
            PopUp.Network(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    public String BookingService(String name) {
        if(name.contains("Car"))
            return "Booking_Car_Wash";
        else if(name.contains("Cleaning"))
            return "Booking_Cleaning";
        else
            return "Booking_Gardening";

    }
    public String BookingSlot(String name) {
        if(name.contains(" "))
            return "Time_Slot_Car_Wash";
        else if(name.contains("Cleaning"))
            return "Time_Slot_Cleaning";
        else
            return "Time_Slot_Gardening";

    }

    @Override
    public void onDeclineLoadSuccess(List<BookingInformation> bookingInformations, List<PickInformation> pickInformations) {
        DeclineAdapter adapter = new DeclineAdapter(this, pickInformations, bookingInformations);
        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onDeclineLoadFail(String message) {
        PopUp.Toast(this, layout, R.drawable.error,message, Toast.LENGTH_SHORT);
        loading.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Dashboard.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            loadDeclinedServices();
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void deleteBooking(BookingInformation bookingInformation) {
        if (bookingInformation != null) {
            deleteBooking(bookingInformation);
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void deletePickUp(PickInformation pickInformation) {
        if (pickInformation != null) {
            deletePickUp(pickInformation);
        }
    }
}