package com.ekspeace.buddystaff.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ekspeace.buddystaff.R;
import com.onesignal.OneSignal;

public class Approve extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView approvePending, approveAccept, approveDecline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);

        initView();
        Actionbar();
        clickEvent();
        OneSignal.sendTag("User_ID", "Admin");
    }
    private void initView(){
        toolbar = findViewById(R.id.approve_toolbar);
        approvePending = findViewById(R.id.approve_pending);
        approveAccept = findViewById(R.id.approve_accepted);
        approveDecline = findViewById(R.id.approve_declined);
    }
    private void clickEvent(){
        approvePending.setOnClickListener(view -> {
            startActivity(new Intent(this, Pending.class));
        });
        approveAccept.setOnClickListener(view -> {
            startActivity(new Intent(this, Accepted.class));
        });
        approveDecline.setOnClickListener(view -> {
            startActivity(new Intent(this, Declined.class));
        });
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(view -> finish());
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
}