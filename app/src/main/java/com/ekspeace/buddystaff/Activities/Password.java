package com.ekspeace.buddystaff.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

public class Password extends AppCompatActivity {
    private LinearLayout loading;
    private Toolbar toolbar;
    private TextInputLayout enterPassword, confirmPassword;
    private Button buttonSend;
    private View layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        initView();
        Actionbar();
    }
    private void initView(){
        loading = findViewById(R.id.password_progressbar);
        toolbar = findViewById(R.id.password_toolbar);
        enterPassword = findViewById(R.id.password_new);
        confirmPassword = findViewById(R.id.password_confirm);
        buttonSend = findViewById(R.id.password_button);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        PopUp.EnterPassword(this, this);
        buttonSend.setOnClickListener(view -> {
            ChangePassword();
        });
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
    private void ChangePassword(){
        String Enter_Password = enterPassword.getEditText().getText().toString().trim();
        String Confirm_Password = confirmPassword.getEditText().getText().toString().trim();

        boolean error = false;
        if (!error) {
            if (TextUtils.isEmpty(Enter_Password)) {
                enterPassword.setError("Password is Required.");
                error = true;
            } else if (Enter_Password.length() < 6) {
                enterPassword.setError("Password Must be greater 5 Characters");
                error = true;
            } else if (!Enter_Password.equals(Confirm_Password)) {
                confirmPassword.setError("Password do not match, please try again");
                error = true;
            }
            if (error)
                return;
        }
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Password").document(Common.PasswordId);
            HashMap<String, Object> password = new HashMap<>();
            password.put("password", Enter_Password);
            docRef.update(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Common.Password = Enter_Password;
                    loading.setVisibility(View.GONE);
                    PopUp.Toast(Password.this,layout,R.drawable.success,"Successfully Changed password ..", Toast.LENGTH_SHORT);
                    finish();
                }
            });
        } else {
            loading.setVisibility(View.GONE);
            PopUp.Network(this, "Connection...", "Please check your internet connectivity and try again");
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
            ChangePassword();
        }
    }

}