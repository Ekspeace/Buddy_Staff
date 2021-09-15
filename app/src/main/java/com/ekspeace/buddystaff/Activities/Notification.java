package com.ekspeace.buddystaff.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekspeace.buddystaff.Common.Common;
import com.ekspeace.buddystaff.Common.PopUp;
import com.ekspeace.buddystaff.Model.Client;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Notification extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout loading;
    private MaterialSpinner spinner;
    private CheckBox checkBox;
    private TextInputLayout inputLayoutTitle, inputLayoutContent;
    private Button buttonSend;
    private View layout;
    private List<Client> clients;
    private List<String> items;
    private String playerId;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initView();
        Actionbar();
        loadClients();
    }
    private void initView(){
        toolbar = findViewById(R.id.notification_toolbar);
        loading = findViewById(R.id.notification_progressbar);
        spinner = findViewById(R.id.notification_spinner);
        checkBox = findViewById(R.id.notification_send_all);
        inputLayoutTitle = findViewById(R.id.notification_title);
        inputLayoutContent = findViewById(R.id.notification_content);
        buttonSend = findViewById(R.id.notification_btn_send);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        buttonSend.setOnClickListener(view -> sendNotification());
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
            CollectionReference reference = FirebaseFirestore.getInstance().collection("Users");
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                             items = new ArrayList<>();
                            clients = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                    Client client = documentSnapshot.toObject(Client.class);
                                    clients.add(client);
                                    items.add(client.getName().concat(" - ").concat(client.getPhone()));
                                }
                            }
                            spinner.setItems(items);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    PopUp.Toast(Notification.this,layout,R.drawable.error,e.getMessage(), Toast.LENGTH_SHORT);
                    loading.setVisibility(View.GONE);
                }
            });
            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                       playerId = clients.get(position).getPlayerId();
                       clientId = clients.get(position).getId();
                }
            });
            loading.setVisibility(View.GONE);
        }else
        {
            PopUp.Network(this, "Connection...", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void sendNotification(){
        String title = inputLayoutTitle.getEditText().getText().toString().trim();
        String content = inputLayoutContent.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            inputLayoutTitle.setError("Title is Required.");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            inputLayoutContent.setError("Please enter a message to client/s");
            return;
        }
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            if(checkBox.isChecked()) {
                CollectionReference colRef = FirebaseFirestore.getInstance().collection("Notifications");
                HashMap<String, Object> notification = new HashMap<>();
                String id = colRef.document().getId();
                notification.put("uid", id);
                notification.put("title", title);
                notification.put("content", content);
                notification.put("serverTimestamp", Timestamp.now());
                colRef.document(id).set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        NotifyAll();
                        PopUp.Toast(Notification.this,layout,R.drawable.success,"Notification successfully sent", Toast.LENGTH_SHORT);
                        loading.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        PopUp.Toast(Notification.this,layout,R.drawable.error,e.getMessage(), Toast.LENGTH_SHORT);
                        loading.setVisibility(View.GONE);
                    }
                });
            }else {
                if(clientId != null) {
                    CollectionReference colRef = FirebaseFirestore.getInstance().collection("Users")
                            .document(clientId).collection("Notifications");
                    HashMap<String, Object> notification = new HashMap<>();
                    String id = colRef.document().getId();
                    notification.put("uid", id);
                    notification.put("title", title);
                    notification.put("content", content);
                    notification.put("serverTimestamp", Timestamp.now());
                    colRef.document(id).set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            NotifySpecificClient(playerId);
                            PopUp.Toast(Notification.this,layout,R.drawable.success,"Notification successfully sent", Toast.LENGTH_SHORT);
                            loading.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            PopUp.Toast(Notification.this,layout,R.drawable.error,e.getMessage(), Toast.LENGTH_SHORT);
                            loading.setVisibility(View.GONE);
                        }
                    });
                }else {
                    PopUp.Toast(Notification.this,layout,R.drawable.error,"Please choose one option", Toast.LENGTH_SHORT);
                    loading.setVisibility(View.GONE);
                }
            }
        } else {
            loading.setVisibility(View.GONE);
            PopUp.Network(this, "Connection...", "Please check your internet connectivity and try again");
        }
    }
    private void NotifyAll(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                int SDK_INT = android.os.Build.VERSION.SDK_INT;

                if (SDK_INT > 8) {

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                            .permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MzAwYTc2NDYtNjYxZi00NWY1LWJhMDMtZWIzZDMzZjEyYTZi");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                +   "\"app_id\": \"3aaff8eb-5e80-4244-906a-8ba91f576f45\","
                                +   "\"included_segments\": [\"All\"],"
                                +   "\"data\": {\"foo\": \"bar\"},"
                                +   "\"contents\": {\"en\": \"Hey! you have a new notification\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (  httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch(Throwable t) {
                        t.printStackTrace();
                    }

                }

            }

        });
    }
    private void NotifySpecificClient(String playerId){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                int SDK_INT = android.os.Build.VERSION.SDK_INT;

                if (SDK_INT > 8) {

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                            .permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MzAwYTc2NDYtNjYxZi00NWY1LWJhMDMtZWIzZDMzZjEyYTZi");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                +   "\"app_id\": \"3aaff8eb-5e80-4244-906a-8ba91f576f45\","
                                +   "\"include_player_ids\": [\"" + playerId + "\"],"
                                +   "\"data\": {\"foo\": \"bar\"},"
                                +   "\"contents\": {\"en\": \"Hey! you have a new notification\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (  httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch(Throwable t) {
                        t.printStackTrace();
                    }

                }

            }

        });
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
           sendNotification();
        }
    }


}