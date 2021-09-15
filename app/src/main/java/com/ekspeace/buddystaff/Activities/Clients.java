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

import com.ekspeace.buddystaff.Adapter.AcceptedAdapter;
import com.ekspeace.buddystaff.Adapter.ClientAdapter;
import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Interface.IAcceptLoadListener;
import com.ekspeace.buddystaff.Interface.IClientLoadListener;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.Client;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Clients extends AppCompatActivity implements IClientLoadListener{
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout loading;
    private IClientLoadListener iClientLoadListener;
    private View layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        initView();
        Actionbar();
        loadClients();
    }
    private void initView(){
        toolbar = findViewById(R.id.clients_toolbar);
        recyclerView = findViewById(R.id.clients_recycler_view);
        loading = findViewById(R.id.clients_progressbar);
        iClientLoadListener = this;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
    private void loadClients(){
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            List<Client> clients = new ArrayList<>();
            CollectionReference reference = FirebaseFirestore.getInstance().collection("Users");
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                    Client client = documentSnapshot.toObject(Client.class);
                                    clients.add(client);
                                }
                                iClientLoadListener.onClientLoadSuccess(clients);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   iClientLoadListener.onClientLoadFailed(e.getMessage());
                }
            });

        }else {
            PopUp.Network(this, "Connection...", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClientLoadSuccess(List<Client> clients) {
        ClientAdapter adapter = new ClientAdapter(this, clients);
        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onClientLoadFailed(String message) {
        PopUp.Toast(this,layout,R.drawable.error,message, Toast.LENGTH_SHORT);
        loading.setVisibility(View.GONE);
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
            loadClients();
        }
    }
}