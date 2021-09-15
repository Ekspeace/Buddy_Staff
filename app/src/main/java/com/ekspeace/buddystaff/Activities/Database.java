package com.ekspeace.buddystaff.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.ekspeace.buddystaff.Adapter.CategoryAdapter;
import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Interface.ICategoryLoadListener;
import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.Category;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Database extends AppCompatActivity implements ICategoryLoadListener {
    public static final String EXTRA_PRICE = "price";
    public static final String EXTRA_NAME = "name";
    private Toolbar toolbar;
    private RecyclerView recyclerViewCarWash, recyclerViewCleaning,recyclerViewGardening;
    private LinearLayout linearLayoutCarWash, linearLayoutCleaning, linearLayoutGardening;
    private LinearLayout linearLayoutAddCarWash, linearLayoutAddCleaning, linearLayoutAddGardening, loading;
    private ImageView buttonAddCarWash, buttonAddCleaning, buttonAddGardening;
    private Button buttonSaveCarWash, buttonSaveCleaning, buttonSaveGardening;
    private CardView cardViewCarWash, cardViewCleaning, cardViewGardening;
    private TextInputLayout textLayoutCarWashCategoryName, textLayoutCarWashCategoryPrice, textLayoutCleaningCategoryName;
    private TextInputLayout  textLayoutCleaningCategoryPrice, textLayoutGardeningCategoryName, textLayoutGardeningCategoryPrice;
    private View layout;
    private ICategoryLoadListener iCategoryLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        initView();
        Actionbar();
        LoadCategory();
        AddCategory();
    }
    private void initView(){
        toolbar = findViewById(R.id.database_toolbar);
        recyclerViewCarWash = findViewById(R.id.database_recycler_car_wash);
        recyclerViewCleaning = findViewById(R.id.database_recycler_cleaning);
        recyclerViewGardening = findViewById(R.id.database_recycler_gardening);

        loading = findViewById(R.id.database_progressbar);
        linearLayoutCarWash = findViewById(R.id.database_car_wash_layout);
        linearLayoutCleaning = findViewById(R.id.database_cleaning_layout);
        linearLayoutGardening = findViewById(R.id.database_gardening_layout);
        linearLayoutAddCarWash = findViewById(R.id.button_add_car_wash_layout);
        linearLayoutAddCleaning = findViewById(R.id.button_add_cleaning_layout);
        linearLayoutAddGardening = findViewById(R.id.button_add_gardening_layout);

        buttonAddCarWash = findViewById(R.id.database_add_car_wash_service);
        buttonAddCleaning = findViewById(R.id.database_add_cleaning_service);
        buttonAddGardening = findViewById(R.id.database_add_gardening_service);
        buttonSaveCarWash = findViewById(R.id.database_save_car_wash_service);
        buttonSaveCleaning = findViewById(R.id.database_save_cleaning_service);
        buttonSaveGardening = findViewById(R.id.database_save_gardening_service);

        cardViewCarWash = findViewById(R.id.database_cv_car_wash);
        cardViewCleaning = findViewById(R.id.database_cv_cleaning);
        cardViewGardening = findViewById(R.id.database_cv_gardening);

        textLayoutCarWashCategoryName = findViewById(R.id.database_add_cw_category);
        textLayoutCarWashCategoryPrice = findViewById(R.id.database_add_cw_category_price);
        textLayoutCleaningCategoryName = findViewById(R.id.database_add_cl_category);
        textLayoutCleaningCategoryPrice = findViewById(R.id.database_add_cl_category_price);
        textLayoutGardeningCategoryName = findViewById(R.id.database_add_g_category);
        textLayoutGardeningCategoryPrice = findViewById(R.id.database_add_g_category_price);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        iCategoryLoadListener = this;
        PopUp.EnterPassword(this, this);
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
    private void AddCategory(){
        AddCarWashCategory();
        AddCleaningCategory();
        AddGardeningCategory();
    }
    private void AddCarWashCategory(){
        buttonAddCarWash.setOnClickListener((View.OnClickListener) v -> {
            buttonAddCarWash.setVisibility(View.GONE);
            linearLayoutAddCarWash.setVisibility(View.VISIBLE);

            buttonSaveCarWash.setOnClickListener((View.OnClickListener) v1 -> {
                final String CarWash_Category_Name = textLayoutCarWashCategoryName.getEditText().getText().toString().trim();
                final String CarWash_Category_Price = textLayoutCarWashCategoryPrice.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(CarWash_Category_Name)) {
                    textLayoutCarWashCategoryName.setError("Category Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(CarWash_Category_Price)) {
                    textLayoutCarWashCategoryPrice.setError("Category Price is Required.");
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                DocumentReference ref = FirebaseFirestore.getInstance()
                        .collection("Category")
                        .document("Car Wash")
                        .collection("Sub Category")
                        .document(CarWash_Category_Name);
                Category category = new Category("R"+CarWash_Category_Price);
                ref.set(category);
                loading.setVisibility(View.GONE);
                PopUp.Toast(Database.this, layout, R.drawable.success, "Category Successfully added... ", Toast.LENGTH_SHORT);
                buttonAddCarWash.setVisibility(View.VISIBLE);
                linearLayoutAddCarWash.setVisibility(View.GONE);
            });
        });
    }
    private void AddCleaningCategory(){
        buttonAddCleaning.setOnClickListener(v -> {
            buttonAddCleaning.setVisibility(View.GONE);
            linearLayoutAddCleaning.setVisibility(View.VISIBLE);


            buttonSaveCleaning.setOnClickListener((View.OnClickListener) v1 -> {
                final String Cleaning_Category_Name = textLayoutCleaningCategoryName.getEditText().getText().toString().trim();
                final String Cleaning_Category_Price = textLayoutCleaningCategoryPrice.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(Cleaning_Category_Name)) {
                    textLayoutCleaningCategoryName.setError("Category Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(Cleaning_Category_Price)) {
                    textLayoutCleaningCategoryPrice.setError("Category Price is Required.");
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                DocumentReference ref = FirebaseFirestore.getInstance()
                        .collection("Category")
                        .document("Cleaning")
                        .collection("Sub Category")
                        .document(Cleaning_Category_Name);
                Category category = new Category("R"+Cleaning_Category_Price);
                ref.set(category);
                loading.setVisibility(View.GONE);
                PopUp.Toast(Database.this, layout, R.drawable.success, "Category Successfully added... ", Toast.LENGTH_SHORT);
                buttonAddCleaning.setVisibility(View.VISIBLE);
                linearLayoutAddCleaning.setVisibility(View.GONE);
            });
        });
    }
    private void AddGardeningCategory(){
        buttonAddGardening.setOnClickListener((View.OnClickListener) v -> {
            buttonAddGardening.setVisibility(View.GONE);
            linearLayoutAddGardening.setVisibility(View.VISIBLE);

            buttonSaveGardening.setOnClickListener((View.OnClickListener) v1 -> {
                final String PickUp_Category_Name = textLayoutGardeningCategoryName.getEditText().getText().toString().trim();
                final String PickUp_Category_Price = textLayoutGardeningCategoryPrice.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(PickUp_Category_Name)) {
                    textLayoutGardeningCategoryName.setError("Category Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(PickUp_Category_Price)) {
                    textLayoutGardeningCategoryPrice.setError("Category Price is Required.");
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                DocumentReference ref = FirebaseFirestore.getInstance()
                        .collection("Category")
                        .document("Gardening")
                        .collection("Sub Category")
                        .document(PickUp_Category_Name);
                Category category = new Category("R"+PickUp_Category_Price);
                ref.set(category);
                loading.setVisibility(View.GONE);
                PopUp.Toast(Database.this, layout, R.drawable.success, "Category Successfully added... ", Toast.LENGTH_SHORT);
                buttonAddGardening.setVisibility(View.VISIBLE);
                linearLayoutAddGardening.setVisibility(View.GONE);
            });
        });
    }
    private void LoadCategory(){
        loading.setVisibility(View.VISIBLE);
        if(Common.isOnline(this)) {
            CarWash();
            Cleaning();
            Gardening();
            loading.setVisibility(View.GONE);
        }else {
            loading.setVisibility(View.GONE);
            PopUp.Network(this, "Connection", "Please check your internet connectivity and try again");
        }
    }
    private void CarWash(){
        cardViewCarWash.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            Common.Service = "Car Wash";
            CollectionReference ref = FirebaseFirestore.getInstance()
                    .collection("Category")
                    .document("Car Wash")
                    .collection("Sub Category");

            ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Category> categoryList = new ArrayList<>();
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Category category = snapshot.toObject(Category.class);
                        category.setName(snapshot.getId());
                        category.setPrice(snapshot.get("price").toString());
                        categoryList.add(category);
                    }
                    linearLayoutCarWash.setVisibility(View.VISIBLE);
                    linearLayoutCleaning.setVisibility(View.GONE);
                    linearLayoutGardening.setVisibility(View.GONE);
                    iCategoryLoadListener.onCategoryLoadSuccessCarWash(categoryList);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iCategoryLoadListener.onCategoryLoadFailed(e.getMessage());
                }
            });
        });
    }
    private void Cleaning(){
        cardViewCleaning.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            Common.Service = "Cleaning";
            CollectionReference ref = FirebaseFirestore.getInstance()
                    .collection("Category")
                    .document("Cleaning")
                    .collection("Sub Category");

            ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Category> categoryList = new ArrayList<>();
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Category category = snapshot.toObject(Category.class);
                        category.setName(snapshot.getId());
                        categoryList.add(category);
                    }
                    linearLayoutCarWash.setVisibility(View.GONE);
                    linearLayoutCleaning.setVisibility(View.VISIBLE);
                    linearLayoutGardening.setVisibility(View.GONE);
                    iCategoryLoadListener.onCategoryLoadSuccessCleaning(categoryList);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iCategoryLoadListener.onCategoryLoadFailed(e.getMessage());
                }
            });
        });
    }
    private void Gardening(){
        cardViewGardening.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            Common.Service = "Gardening";
            CollectionReference ref = FirebaseFirestore.getInstance()
                    .collection("Category")
                    .document("Gardening")
                    .collection("Sub Category");

            ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Category> categoryList = new ArrayList<>();
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        Category category = snapshot.toObject(Category.class);
                        category.setName(snapshot.getId());
                        categoryList.add(category);
                    }
                    linearLayoutCarWash.setVisibility(View.GONE);
                    linearLayoutCleaning.setVisibility(View.GONE);
                    linearLayoutGardening.setVisibility(View.VISIBLE);
                    iCategoryLoadListener.onCategoryLoadSuccessGardening(categoryList);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iCategoryLoadListener.onCategoryLoadFailed(e.getMessage());
                }
            });
        });
    }
    private void DeleteCarWashCategory(String name){
        loading.setVisibility(View.VISIBLE);
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("Category")
                .document("Car Wash")
                .collection("Sub Category")
                .document(name);
        ref.delete();
        loading.setVisibility(View.GONE);
        PopUp.Toast(this, layout, R.drawable.success, "Category Successfully deleted... ", Toast.LENGTH_SHORT);

    }
    private void DeleteCleaningCategory(String name){
        loading.setVisibility(View.VISIBLE);
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("Category")
                .document("Cleaning")
                .collection("Sub Category")
                .document(name);
        ref.delete();
        loading.setVisibility(View.GONE);
        PopUp.Toast(this, layout, R.drawable.success, "Category Successfully deleted... ", Toast.LENGTH_SHORT);
    }
    private void DeleteGardeningCategory(String name){
        loading.setVisibility(View.VISIBLE);
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("Category")
                .document("Gardening")
                .collection("Sub Category")
                .document(name);
        ref.delete();
        loading.setVisibility(View.GONE);
        PopUp.Toast(this, layout, R.drawable.success, "Category Successfully deleted... ", Toast.LENGTH_SHORT);
    }
    private void DeleteCategory(String name){
        final Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.pop_up);

        TextView tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextView tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        ImageView imIcon = alertDialog.findViewById(R.id.dialog_icon);
        Button btnClose = alertDialog.findViewById(R.id.dialog_close);
        Button btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


        tvTitle.setText("Delete Category");
        tvDesc.setText("Do you really want to delete this Category?");
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
                if(Common.Service.contains("Car")){
                    DeleteCarWashCategory(name);
                }
                else if(Common.Service.contains("Cleaning")){
                    DeleteCleaningCategory(name);
                }
                else {
                    DeleteGardeningCategory(name);
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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
    public void onCategoryLoadSuccessCarWash(List<Category> categoryList) {
        CategoryAdapter adapter = new CategoryAdapter(this, categoryList);
        recyclerViewCarWash.setHasFixedSize(true);
        recyclerViewCarWash.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCarWash.setAdapter(adapter);
        loading.setVisibility(View.GONE);

        adapter.setOnItemClickListener(new CategoryAdapter.ClickListener() {
            @Override
            public void onClick(View v, int pos) {
                Intent intent = new Intent(Database.this, Price.class);
                intent.putExtra(EXTRA_NAME, categoryList.get(pos).getName());
                intent.putExtra(EXTRA_PRICE, categoryList.get(pos).getPrice());
                startActivity(intent);
            }
        });
    }
    @Override
    public void onCategoryLoadSuccessCleaning(List<Category> categoryList) {
        CategoryAdapter adapter = new CategoryAdapter(this, categoryList);
        recyclerViewCleaning.setHasFixedSize(true);
        recyclerViewCleaning.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCleaning.setAdapter(adapter);
        loading.setVisibility(View.GONE);

        adapter.setOnItemClickListener(new CategoryAdapter.ClickListener() {
            @Override
            public void onClick(View v, int pos) {
                Intent intent = new Intent(Database.this, Price.class);
                intent.putExtra(EXTRA_NAME, categoryList.get(pos).getName());
                intent.putExtra(EXTRA_PRICE, categoryList.get(pos).getPrice());
                startActivity(intent);
            }
        });
    }
    @Override
    public void onCategoryLoadSuccessGardening(List<Category> categoryList) {
        CategoryAdapter adapter = new CategoryAdapter(this, categoryList);
        recyclerViewGardening.setHasFixedSize(true);
        recyclerViewGardening.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGardening.setAdapter(adapter);
        loading.setVisibility(View.GONE);

        adapter.setOnItemClickListener(new CategoryAdapter.ClickListener() {
            @Override
            public void onClick(View v, int pos) {
                Intent intent = new Intent(Database.this, Price.class);
                intent.putExtra(EXTRA_NAME, categoryList.get(pos).getName());
                intent.putExtra(EXTRA_PRICE, categoryList.get(pos).getPrice());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        PopUp.Toast(this, layout,R.drawable.error,message, Toast.LENGTH_SHORT);
        loading.setVisibility(View.GONE);
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
            LoadCategory();
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void deleteCategory(Category category) {
        if (category != null) {
            DeleteCategory(category.getName());
        }
    }
}