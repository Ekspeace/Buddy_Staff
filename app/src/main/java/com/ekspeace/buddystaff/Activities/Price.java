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
import android.widget.TextView;
import android.widget.Toast;

import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Model.Category;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ekspeace.buddystaff.Activities.Database.EXTRA_NAME;
import static com.ekspeace.buddystaff.Activities.Database.EXTRA_PRICE;

public class Price extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView displayPrice, headerText;
    private LinearLayout linearLayoutChangePrice, loading;
    private TextInputLayout inputLayoutPrice;
    private Button buttonChange, buttonSave;
    private String Price, Name;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        Intent intent = getIntent();
        Price = intent.getStringExtra(EXTRA_PRICE);
        Name = intent.getStringExtra(EXTRA_NAME);

        initView();
        Actionbar();
        ChangePrice();
    }
    private void initView(){
        toolbar = findViewById(R.id.price_toolbar);
        displayPrice = findViewById(R.id.price);
        headerText = findViewById(R.id.price_header_text);
        linearLayoutChangePrice = findViewById(R.id.price_change_layout);
        loading = findViewById(R.id.price_progressbar);
        inputLayoutPrice = findViewById(R.id.price_new);
        buttonChange = findViewById(R.id.price_btn_change);
        buttonSave = findViewById(R.id.price_btn_save);

        headerText.setText(Name.concat(" price"));
        displayPrice.setText(Price);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
    }
    private void ChangePrice(){
        buttonChange.setOnClickListener(v -> {
            linearLayoutChangePrice.setVisibility(View.VISIBLE);
            buttonChange.setVisibility(View.GONE);
            buttonSave.setOnClickListener(v1 -> {
                String Price = inputLayoutPrice.getEditText().getText().toString();
                if (TextUtils.isEmpty(Price)) {
                    inputLayoutPrice.setError("Price is Required.");
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                buttonChange.setVisibility(View.VISIBLE);
                ChangePrice(Name, Price);

            });
        });
    }
    private void ChangePrice(String Name, String Price){
        if(Common.isOnline(this)) {
            DocumentReference ref = FirebaseFirestore.getInstance()
                    .collection("Category")
                    .document(Common.Service)
                    .collection("Sub Category")
                    .document(Name);
            Category category = new Category("R" + Price);
            ref.set(category);
            linearLayoutChangePrice.setVisibility(View.GONE);
            String price = "R" + Price;
            displayPrice.setText(price);
            loading.setVisibility(View.GONE);
            PopUp.Toast(this, layout, R.drawable.success, "Price Successfully Edited... ", Toast.LENGTH_SHORT);
        }else {
            PopUp.Network(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
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
            ChangePrice();
        }
    }
}