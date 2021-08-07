package com.example.example1.Actitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.example.example1.Adapters.CustomListViewAdapter;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.Dialogs.SuccessDialog;
import com.example.example1.R;
import com.example.example1.Url;
import com.example.example1.api.APIConnection;
import com.example.example1.modal.Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class report extends AppCompatActivity {

    ListView listView;
    private CustomListViewAdapter adapter;
    private ArrayList<Items> list = new ArrayList<Items>();
    String mId;
    int id;
    Items items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        listView = findViewById(R.id.reportitems);

        Intent i = getIntent();

        mId = i.getStringExtra("userId");

        id = Integer.parseInt(mId);
        APIRequest(report.this, mId);
    }

    private HashMap<String, String> HeadersUserIdAndProdId(String userId) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("userId", userId);
        return headers;
    }

    private void APIRequest(final Context context, String userId) {
        final SuccessDialog dialog = new SuccessDialog();
        final ErrorDialog errorDialog = new ErrorDialog();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        APIConnection connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.getRentedItems, HeadersUserIdAndProdId(userId), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    if (result.equals("Failed")) {
                        progressDialog.dismiss();
                        dialog.showDialog(report.this, "There is no items for this user, check the Rents");
                        return;
                    }
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("items");
                    boolean cleaned = false;

                    for (int i = 0; i < array.length(); i++) {
                        if (adapter != null && !adapter.isEmpty() && !cleaned) {
                            adapter.clear();
                            cleaned = true;
                        }
                        items = new Items();

                        items.setProd_name(array.getJSONObject(i).getString("prod_name"));
                        items.setProd_address(array.getJSONObject(i).getString("prod_address"));
                        items.setRate(array.getJSONObject(i).getString("rate"));
                        items.setDescription(array.getJSONObject(i).getString("description"));
                        items.setPrice(array.getJSONObject(i).getString("price"));

                        list.add(items);

                        progressDialog.dismiss();
                    }
                    adapter = new CustomListViewAdapter(context, R.layout.report, list);

                    listView.setAdapter(adapter);

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    errorDialog.showDialog(report.this, "Error occurred, please try again");
                }
            }
        });
    }
}
