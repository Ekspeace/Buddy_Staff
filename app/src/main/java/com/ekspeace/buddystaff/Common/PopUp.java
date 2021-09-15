package com.ekspeace.buddystaff.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.ekspeace.buddystaff.Model.BookingInformation;
import com.ekspeace.buddystaff.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddystaff.Model.PickInformation;
import com.ekspeace.buddystaff.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PopUp extends AppCompatActivity {

    static TextView tvTitle, tvDesc;
    static ImageView imIcon;
    static Button btnClose, btnConfirm;
    static LinearLayout linearLayout;
    static CardView cardView;
    static View layout;
    public static void Toast(Context context, View layout, int icon, String message, int duration) {

        tvDesc = layout.findViewById(R.id.toast_message);
        imIcon = layout.findViewById(R.id.toast_img);
        linearLayout = layout.findViewById(R.id.custom_toast_container);

        if(icon == R.drawable.error)
        {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.error));
            tvDesc.setTextColor(context.getResources().getColor(R.color.error_200));
        }else {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.green_100));
            tvDesc.setTextColor(context.getResources().getColor(R.color.green_500));
        }

        tvDesc.setText(message);
        imIcon.setImageResource(icon);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 40);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
    public static void Network(Context context, String title, String message) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);
        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        cardView = alertDialog.findViewById(R.id.pop_up_cardView);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);

        tvTitle.setText(title);
        tvDesc.setText(message);
        imIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.internet));


        btnConfirm.setText("Try again");
        btnClose.setText("Close");
        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

      btnConfirm.setOnClickListener(v -> {
          alertDialog.cancel();
          alertDialog.dismiss();
          EventBus.getDefault().postSticky(new NetworkConnectionEvent(true));

      });
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
            alertDialog.onBackPressed();
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if(!((Activity) context).isFinishing())
        {
            alertDialog.show();
        }


    }
    public static void pickPrice(Context context, PickInformation pickInformation, LinearLayout loading){
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);
        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        cardView = alertDialog.findViewById(R.id.pop_up_cardView);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);
        TextInputLayout textInputLayout = alertDialog.findViewById(R.id.pending_enter_price);
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, alertDialog.findViewById(R.id.custom_toast_container));

        tvTitle.setText("Provide pick up price");
        tvDesc.setVisibility(View.GONE);
        imIcon.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.VISIBLE);


        btnConfirm.setText("Proceed");
        btnClose.setText("Close");
        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

        btnConfirm.setOnClickListener(v -> {
            String price = textInputLayout.getEditText().getText().toString().trim();
            if (TextUtils.isEmpty(price)) {
                textInputLayout.setError("Price is Required.");
                return;
            }
            loading.setVisibility(View.VISIBLE);
            if (Common.isOnline(context)) {
                DocumentReference pickRef = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(pickInformation.getCustomerId())
                        .collection("Pick Ups")
                        .document(pickInformation.getServiceId());
                DocumentReference reference = FirebaseFirestore.getInstance()
                        .collection("Pick Ups")
                        .document(pickInformation.getServiceId());
                HashMap<String, Object> pick = new HashMap<>();
                pick.put("verified", "Confirmed");
                pick.put("servicePrice", "R"+price);
                pickRef.update(pick).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.update(pick);
                        Notify("Hey! your pick up has been confirmed", pickInformation.getCustomerPlayerId());
                        Toast(context, layout, R.drawable.success, "Successfully updated price", Toast.LENGTH_SHORT);
                        loading.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        loading.setVisibility(View.GONE);
                        Toast(context, layout, R.drawable.error, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
                alertDialog.cancel();
                alertDialog.dismiss();
            }else {
                loading.setVisibility(View.GONE);
                Network(context, "Connection", "Please check your internet connectivity and try again");
                alertDialog.cancel();
                alertDialog.dismiss();
            }
        });
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
            alertDialog.onBackPressed();
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if(!((Activity) context).isFinishing())
        {
            alertDialog.show();
        }

    }
    public static void bookingVerify(Context context, BookingInformation bookingInformation, LinearLayout loading){
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);LayoutInflater inflater = alertDialog.getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, alertDialog.findViewById(R.id.custom_toast_container));
        loading.setVisibility(View.VISIBLE);
            if (Common.isOnline(context)) {
                DocumentReference bookRef = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(bookingInformation.getCustomerId());
                if(bookingInformation.getServiceName().contains("Car"))
                    bookRef.collection("Booking_Car_Wash").document(bookingInformation.getServiceId());
                else if(bookingInformation.getServiceName().contains("Cleaning"))
                    bookRef.collection("Booking_Cleaning").document(bookingInformation.getServiceId());
                else
                    bookRef.collection("Booking_Gardening").document(bookingInformation.getServiceId());
                DocumentReference reference = FirebaseFirestore.getInstance()
                        .collection("Bookings")
                        .document(bookingInformation.getServiceId());
                HashMap<String, Object> book = new HashMap<>();
                book.put("verified", "Confirmed");
                bookRef.update(book).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.update(book);
                        Notify("Hey! your booking has been confirmed", bookingInformation.getCustomerPlayerId());
                        Toast(context, layout, R.drawable.success, "Successfully confirmed", Toast.LENGTH_SHORT);
                        loading.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        loading.setVisibility(View.GONE);
                        Toast(context, layout, R.drawable.error, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
            }else {
                loading.setVisibility(View.GONE);
                Network(context, "Connection", "Please check your internet connectivity and try again");
            }

    }
    public static void declineReason(Context context, LinearLayout loading, String customerId, String customerPlayerId, String serviceId, String serviceName, View vi){
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);
        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        cardView = alertDialog.findViewById(R.id.pop_up_cardView);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);
        TextInputLayout title = alertDialog.findViewById(R.id.pending_notification_title);
        TextInputLayout content = alertDialog.findViewById(R.id.pending_notification_content);
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, alertDialog.findViewById(R.id.custom_toast_container));

        tvTitle.setText("Provide reason for the decline");
        tvDesc.setVisibility(View.GONE);
        imIcon.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        content.setVisibility(View.VISIBLE);



        btnConfirm.setText("Proceed");
        btnClose.setText("Close");
        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

        btnConfirm.setOnClickListener(v -> {
            String Title = title.getEditText().getText().toString().trim();
            String Content = content.getEditText().getText().toString().trim();
            if (TextUtils.isEmpty(Title)) {
                title.setError("Title is Required.");
                return;
            }
            if (TextUtils.isEmpty(Content)) {
                content.setError("Content is Required.");
                return;
            }
            loading.setVisibility(View.VISIBLE);
            if (Common.isOnline(context)) {
                DocumentReference reference;
                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(customerId);
                if(serviceName.contains("Car")) {
                    docRef.collection("Booking_Car_Wash").document(serviceId);
                    reference = FirebaseFirestore.getInstance()
                            .collection("Bookings")
                            .document(serviceId);
                }
                else if(serviceName.contains("Cleaning")) {
                    docRef.collection("Booking_Cleaning").document(serviceId);
                    reference = FirebaseFirestore.getInstance()
                            .collection("Bookings")
                            .document(serviceId);
                }
                else if(serviceName.contains("Pick")) {
                    docRef.collection("Pick Ups").document(serviceId);
                    reference = FirebaseFirestore.getInstance()
                            .collection("Pick Ups")
                            .document(serviceId);
                }
                else{
                    docRef.collection("Booking_Gardening").document(serviceId);
                reference = FirebaseFirestore.getInstance()
                        .collection("Bookings")
                        .document(serviceId);}
                HashMap<String, Object> pick = new HashMap<>();
                pick.put("verified", "Declined");
                docRef.update(pick).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.update(pick);
                        CollectionReference colRef = FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(customerId)
                                .collection("Notifications");
                        HashMap<String, Object> notify = new HashMap<>();
                        notify.put("title", Title);
                        notify.put("content", Content);
                        notify.put("serverTimestamp", Timestamp.now());
                        String Id = colRef.document().getId();
                        notify.put("uid", Id);
                        colRef.document(Id).set(notify);
                        Notify("Hey! we regret to info you that your request for ".concat(serviceName).concat(" was declined, check your notifications for more information"), customerPlayerId);
                        Toast(context, vi, R.drawable.success, "Successfully declined", Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast(context, vi, R.drawable.error, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
                alertDialog.cancel();
                alertDialog.dismiss();
            }else {
                loading.setVisibility(View.GONE);
                Network(context, "Connection", "Please check your internet connectivity and try again");
                alertDialog.cancel();
                alertDialog.dismiss();
            }
        });
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
            alertDialog.onBackPressed();
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if(!((Activity) context).isFinishing())
        {
            alertDialog.show();
        }

    }
    private static void Notify( String message, String playerId){
        AsyncTask.execute(() -> {
            int SDK_INT = Build.VERSION.SDK_INT;
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
                            +   "\"included_segments\": [\""+ playerId + "\"],"
                            +   "\"data\": {\"foo\": \"bar\"},"
                            +   "\"contents\": {\"en\": \""+message+"\"}"
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

        });
    }
    public static void EnterPassword(final Context context, AppCompatActivity appCompatActivity) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);
        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextInputLayout password = alertDialog.findViewById(R.id.enter_password_dialog);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);

        tvTitle.setText("Provide Password Please");
        tvDesc.setVisibility(View.GONE);
        password.setVisibility(View.VISIBLE);
        imIcon.setVisibility(View.GONE);
        btnClose.setText("Close");
        btnConfirm.setText("Ok");
        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);



        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appCompatActivity.finish();
                alertDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User_Password = password.getEditText().getText().toString();
                boolean error = false;
                if (!error) {
                    if (TextUtils.isEmpty(User_Password)) {
                        password.setError("Password is Required.");
                        error = true;
                    } else if (User_Password.length() < 6) {
                        password.setError("Password Must be greater 5 Characters");
                        error = true;
                    }else if(!User_Password.equals(Common.Password)){
                        password.setError("Incorrect Password, try again ");
                        error = true;
                    }
                    if (error)
                        return;
                }

                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

}
