package com.example.example1.Actitvity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Cart extends AppCompatActivity {
    ListView listView;
    private CustomListViewAdapter adapter;
    private ArrayList<Items> list = new ArrayList<Items>();
    String mId;
    int id;
    int pricesall=0;
    Items items;
    TextView prices,itemsCounter;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        listView = findViewById(R.id.Cart);
        prices= findViewById(R.id.totalprices);
        itemsCounter= findViewById(R.id.counterCartItems);
        Intent i = getIntent();

        mId = i.getStringExtra("userId");

        id = Integer.parseInt(mId);
        APIRequest(this, mId);
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
        connections.servicesConnection(Request.Method.POST, Url.getItemsCarts, HeadersUserIdAndProdId(userId), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    if (result.equals("Failed")) {
                        progressDialog.dismiss();
                        dialog.showDialog(Cart.this, "There is no items for this user, check the Categories");
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
                        items.setCat_name(array.getJSONObject(i).getString("cat_name"));
                        items.setUser_id(array.getJSONObject(i).getString("user_id"));
                        items.setProd_id(array.getJSONObject(i).getString("prod_id"));
                        list.add(items);

                        for (int ia = 0; ia < list.size(); ia++) {

                            pricesall+=Integer.parseInt(items.getPrice());


                        }

                        progressDialog.dismiss();
                    }
                    adapter = new CustomListViewAdapter(context, R.layout.listview_row, list);

                    listView.setAdapter(adapter);
                  int x = listView.getAdapter().getCount();
                   itemsCounter.setText(x+"");


                    prices.setText(pricesall+"");
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            startActivity(new Intent(context, ViewItem.class)
                                    .putExtra("prod_name", list.get(position).getProd_name())
                                    .putExtra("prod_address", list.get(position).getProd_address())
                                    .putExtra("description", list.get(position).getDescription())
                                    .putExtra("price", list.get(position).getPrice())
                                    .putExtra("rate", list.get(position).getRate())
                                    .putExtra("prod_name", list.get(position).getProd_name())
                                    .putExtra("user_id", list.get(position).getUser_id())
                                    .putExtra("prod_id", list.get(position).getProd_id()));

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    errorDialog.showDialog(Cart.this, "Error occurred, please try again");
                }
            }
        });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchView);

        return true;

    }
}