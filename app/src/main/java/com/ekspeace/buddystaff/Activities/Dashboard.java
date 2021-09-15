package com.ekspeace.buddystaff.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.Client;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private CardView cvClients, cvPendingBookings, cvPendingPicks;
    private CardView cvBookings, cvPicks, cvDatabase, cvPassword;
    private TextView tvClientNum, tvPicksNum, tvBookingsNum;
    private LinearLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initView();
        loadStats();
        clickEvents();
    }
    private void initView(){
        Toolbar toolbar = findViewById(R.id.dashboard_toolbar);
        cvClients = findViewById(R.id.dashboard_user);
        cvPendingBookings = findViewById(R.id.dashboard_pending_bookings);
        cvPendingPicks = findViewById(R.id.dashboard_pending_picks);
        cvBookings =  findViewById(R.id.dashboard_approve_bookings);
        cvPicks = findViewById(R.id.dashboard_approve_picks);
        cvDatabase = findViewById(R.id.dashboard_modify_dashboard);
        cvPassword = findViewById(R.id.dashboard_change_password);
        tvPicksNum = findViewById(R.id.number_of_picks);
        tvClientNum = findViewById(R.id.number_of_users);
        tvBookingsNum = findViewById(R.id.number_of_bookings);
        loading = findViewById(R.id.dashboard_progressbar);

        setSupportActionBar(toolbar);
    }
    private void clickEvents(){
        cvClients.setOnClickListener(view -> {
            startActivity(new Intent(this, Clients.class));
        });
        cvPendingBookings.setOnClickListener(view -> {
            Intent intent = new Intent(this, Pending.class);
            Common.currentInfoName = "Booking";
            startActivity(intent);
        });
        cvPendingPicks.setOnClickListener(view -> {
            Common.currentInfoName = "Pick";
            startActivity(new Intent(this, Pending.class));
        });
        cvBookings.setOnClickListener(view -> {
            Intent intent = new Intent(this, Approve.class);
            Common.currentInfoName = "Booking";
            startActivity(intent);
        });
        cvPicks.setOnClickListener(view -> {
            Common.currentInfoName = "Pick";
            startActivity(new Intent(this, Approve.class));
        });
        cvDatabase.setOnClickListener(view -> {
            startActivity(new Intent(this, Database.class));
        });
        cvPassword.setOnClickListener(view -> {
            startActivity(new Intent(this, Password.class));
        });
    }
    private void loadStats(){
        loading.setVisibility(View.VISIBLE);
        if(Common.isOnline(this)){
             FirebaseFirestore.getInstance()
                    .collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            int clients = task.getResult().size();
                            tvClientNum.setText(String.format(Locale.getDefault(),"%02d", clients));
                        }
                    });
            FirebaseFirestore.getInstance()
                    .collection("Bookings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    List<BookingInformation> bookings = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        BookingInformation information = documentSnapshot.toObject(BookingInformation.class);
                        if(information.getVerified().contains("P"))
                            bookings.add(information);
                    }
                    tvBookingsNum.setText(String.format(Locale.getDefault(),"%02d", bookings.size()));
                }
            });
            FirebaseFirestore.getInstance()
                    .collection("Pick Ups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    List<PickInformation> picks = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        PickInformation information = documentSnapshot.toObject(PickInformation.class);
                        if(information.getVerified().contains("P"))
                            picks.add(information);
                    }
                    tvPicksNum.setText(String.format(Locale.getDefault(),"%02d", picks.size()));
                }
            });

            CollectionReference docRef = FirebaseFirestore.getInstance().collection("Password");
            docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Common.PasswordId = task.getResult().getDocuments().get(0).getId();
                        Common.Password = task.getResult().getDocuments().get(0).get("password").toString();
                        loading.setVisibility(View.GONE);
                    }else {
                        loading.setVisibility(View.GONE);
                    }
                }
            });
        }else {
            loading.setVisibility(View.GONE);
            PopUp.Network(this, "Connection", "Please check your internet connectivity and try again");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notification, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                startActivity(new Intent(this, Notification.class));
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
            loadStats();
        }
    }
}