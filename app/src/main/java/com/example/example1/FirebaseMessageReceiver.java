package com.example.example1;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.example1.Actitvity.AddNewItem;
import com.example.example1.Actitvity.MainActivity;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.Dialogs.RegisterDialog;
import com.example.example1.api.APIConnection;
import com.example.example1.modal.Categories;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.example1.Config.TAG_cat_Id;
import static com.example.example1.Config.TAG_cat_name;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService {

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    public static final String Id = "Id";
    boolean isRented;
    String id;

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        id = sharedpreferences.getString(Id, null);
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        /*if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }*/

        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            getRentData(id, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

            System.out.println("getTitle(): " + remoteMessage.getNotification().getTitle());
        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.activity_notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.home);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title,
                                 String message) {

        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, ItemsExpired.class).putExtra("userId", id);
        // Assign channel ID
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.home)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(
                    getCustomDesign(title, message));
        } // If Android Version is lower than Jelly Beans,
        // customized layout cannot be used and thus the
        // layout is set as follows
        else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.home);
        }
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());

    }

    private HashMap<String, String> getHeaders(String id) {
        HashMap<String, String> headers = new HashMap<String, String>();

        headers.put("user_id", id);

        return headers;
    }

    private void getRentData(String u_id, String title, String body) {

        APIConnection connections = new APIConnection(this);

        connections.servicesConnection(Request.Method.POST, Url.getItemsAndRented, getHeaders(u_id), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                if (result.trim().toLowerCase().equals("yes")) {
                    showNotification(
                            title,
                            body);
                } else {

                }


                System.out.println("response" + result);
                System.out.println("isRented" + isRented);
            }
        });
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}