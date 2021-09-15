package com.ekspeace.buddystaff.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekspeace.buddystaff.Adapter.PendingAdapter;
import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Interface.IPendingLoadListener;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

public class Pending extends AppCompatActivity implements IPendingLoadListener {
    private Toolbar toolbar;
    private TextView headerText;
    private RecyclerView recyclerView;
    private LinearLayout loading;
    private IPendingLoadListener iPendingLoadListener;
    private BookingInformation bookingInformation;
    private PickInformation pickInformation;
    private View layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        initView();
        Actionbar();
        loadPendingServices();
    }
    private void initView(){
        toolbar = findViewById(R.id.pending_toolbar);
        headerText = findViewById(R.id.pending_header_text);
        recyclerView = findViewById(R.id.pending_recycler_view);
        loading = findViewById(R.id.pending_progressbar);
        iPendingLoadListener = this;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

    }

    private void loadPendingServices(){
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            if(Common.currentInfoName == "Booking") {
                headerText.setText("Booking pending requests");
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
                                        iPendingLoadListener.onPendingLoadSuccess(bookingInformationList, null);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(e -> iPendingLoadListener.onPendingLoadFail(e.getMessage()));
        }else {
                headerText.setText("Pick up pending requests");
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
                                        iPendingLoadListener.onPendingLoadSuccess(null, pickInformationList);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(e -> iPendingLoadListener.onPendingLoadFail(e.getMessage()));
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
    @Override
    public void onPendingLoadSuccess(List<BookingInformation> bookingInformations, List<PickInformation> pickInformations) {
        PendingAdapter adapter = new PendingAdapter(this, pickInformations, bookingInformations, loading);
        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onPendingLoadFail(String message) {
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
            loadPendingServices();
        }
    }
}