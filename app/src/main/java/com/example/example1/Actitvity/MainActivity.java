package com.example.example1.Actitvity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.example1.Adapters.CustomListViewAdapter;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.FirebasePushNotificationClass;
import com.example.example1.R;
import com.example.example1.Url;
import com.example.example1.api.APIConnection;
import com.example.example1.modal.Items;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ListView listView;
    String mId;
    ProgressDialog progressDialog;
    private APIConnection connections;
    int id;
    private CustomListViewAdapter adapter;
    private List<Items> list = new ArrayList<Items>();
    NavigationView mNavigationView;
    View header;
    Items items;
    FloatingActionButton add_fab;
    int f = 0;
    RadioGroup sort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        listView = findViewById(R.id.listView);
        add_fab = findViewById(R.id.add_fab);
        sort = findViewById(R.id.sort);
        Intent intentBackgroundService = new Intent(this, FirebasePushNotificationClass.class);
        startService(intentBackgroundService);
        Intent i = getIntent();
        mId = i.getStringExtra("userId");
        id = Integer.parseInt(mId);

        f = 0;
        mNavigationView = findViewById(R.id.mNavigationView);
        header = mNavigationView.getHeaderView(0);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                String title = String.valueOf(item.getTitle());

                if (id == R.id.nav_logout) {
                    getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().clear().apply();
                    startActivity(new Intent(getApplicationContext(), LogIn.class));
                    finish();
                } else if (id == R.id.EditProfile) {
                    startActivity(new Intent(getApplicationContext(), EditProfile.class)
                            .putExtra("userId", mId));


                } else if (id == R.id.reportitems) {

                    startActivity(new Intent(getApplicationContext(), report.class)
                            .putExtra("userId", mId));
                } else if (id == R.id.ItemsAtMyLocation) {
                    APIRequestForLocation(MainActivity.this, mId);

                } else if (id == R.id.CartItems) {

                    startActivity(new Intent(getApplicationContext(), Cart.class)
                            .putExtra("userId", mId));
                }  else if (id == R.id.AllItems) {
                    APIRequestAllItems(MainActivity.this);
                }
                else {

                    APIRequestCategories(MainActivity.this, title);

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

        });
        if (mNavigationView.getMenu().findItem(R.id.ItemsAtMyLocation).isChecked()) {
        }
        sort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = sort.getCheckedRadioButtonId();
                switch (selectedId) {
                    case R.id.Asc:
                        Collections.reverse(list);
                        adapter = new CustomListViewAdapter(MainActivity.this, R.layout.listview_row, list);

                        listView.setAdapter(adapter);
                         break;
                    case R.id.Decs:
                        Collections.reverse(list);
                        adapter = new CustomListViewAdapter(MainActivity.this, R.layout.listview_row, list);

                        listView.setAdapter(adapter);
                        break;
                    default:
                        APIRequestAllItems(MainActivity.this);


                }
            }
        });
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.listView);

        listView.setTextFilterEnabled(true);
        APIRequest(this, mId, "ASC", "No");
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNewItem.class));
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        APIRequest(this, mId, "ASC", "No");

    }

    private HashMap<String, String> getHeaders(String id, String sort, String location) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("id", id);
        headers.put("sort", sort);
        headers.put("location", location);

        return headers;
    }

    private HashMap<String, String> getHeadersCategories(String Categories) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("catagory", Categories);
           return headers;
    }

    private HashMap<String, String> getHeadersAllItems() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
    private HashMap<String, String> getHeaders(String id) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("id", id);
        headers.put("location", "Yes");


        return headers;
    }

    private void APIRequestForLocation(final Context context, String id) {

        final ErrorDialog dialog = new ErrorDialog();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.getItemsByIdUrl, getHeaders(id), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    if (result.equals("Failed")) {
                        progressDialog.dismiss();
                        dialog.showDialog(MainActivity.this, "There is no items for this user, check the Categories");
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
                        items.setDescription(array.getJSONObject(i).getString("description"));
                        items.setPrice(array.getJSONObject(i).getString("price"));
                        items.setCat_name(array.getJSONObject(i).getString("cat_name"));
                        items.setJob_name(array.getJSONObject(i).getString("job_name"));
                        items.setRate(array.getJSONObject(i).getString("rate"));
                        items.setUser_id(array.getJSONObject(i).getString("user_id"));
                        items.setProd_id(array.getJSONObject(i).getString("prod_id"));

                        list.add(items);

                        //adapter.clear();

                        progressDialog.dismiss();
                    }
                    adapter = new CustomListViewAdapter(context, R.layout.listview_row, list);

                    listView.setAdapter(adapter);

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
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    dialog.showDialog(MainActivity.this, "Error occurred, please try again");
                }
            }
        });
    }

    private void APIRequest(final Context context, String id, String sort, String location) {

        final ErrorDialog dialog = new ErrorDialog();
        f = 0;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.getItemsByIdUrl, getHeaders(id, sort, location), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    if (result.equals("Failed")) {
                        progressDialog.dismiss();
                        dialog.showDialog(MainActivity.this, "There is no items for this user, check the Categories");
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
                        items.setDescription(array.getJSONObject(i).getString("description"));
                        items.setPrice(array.getJSONObject(i).getString("price"));
                        items.setCat_name(array.getJSONObject(i).getString("cat_name"));
                        items.setJob_name(array.getJSONObject(i).getString("job_name"));
                        items.setRate(array.getJSONObject(i).getString("rate"));
                        items.setUser_id(array.getJSONObject(i).getString("user_id"));
                        items.setProd_id(array.getJSONObject(i).getString("prod_id"));

                        list.add(items);


                        progressDialog.dismiss();
                    }
                    adapter = new CustomListViewAdapter(context, R.layout.listview_row, list);

                    listView.setAdapter(adapter);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    dialog.showDialog(MainActivity.this, "Error occurred, please try again");
                }
            }
        });
    }

    private void APIRequestCategories(final Context context, String Categories) {
        final ErrorDialog dialog = new ErrorDialog();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        f = 0;
        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.getItemsByCategoriesUrl, getHeadersCategories(Categories), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("items");
                    boolean cleaned = false;
                    for (int i = 0; i < array.length(); i++) {
                        if (adapter != null && !adapter.isEmpty() && !cleaned) {
                            cleaned = true;
                            adapter.clear();
                        }
                        items = new Items();
                        items.setProd_name(array.getJSONObject(i).getString("prod_name"));
                        items.setProd_address(array.getJSONObject(i).getString("prod_address"));
                        items.setDescription(array.getJSONObject(i).getString("description"));
                        items.setPrice(array.getJSONObject(i).getString("price"));
                        items.setCat_name(array.getJSONObject(i).getString("cat_name"));
                        items.setRate(array.getJSONObject(i).getString("rate"));
                        items.setProd_id(array.getJSONObject(i).getString("prod_id"));


                        list.add(items);

                        progressDialog.dismiss();
                    }
                    adapter = new CustomListViewAdapter(context, R.layout.listview_row, list);
                    listView.setAdapter(adapter);
                    cleaned = false;
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
                                    .putExtra("prod_name", list.get(position).getProd_name())
                                    .putExtra("prod_name", list.get(position).getProd_name())
                                    .putExtra("user_id", mId)
                                    .putExtra("prod_id", list.get(position).getProd_id())
                            );
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    dialog.showDialog(MainActivity.this, "Error occurred, please try again");
                }
            }
        });
    }

    private void APIRequestAllItems(final Context context) {
        final ErrorDialog dialog = new ErrorDialog();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        if(!adapter.isEmpty())
        adapter.clear();
        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.getallitems, getHeadersAllItems(), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("items");
                    boolean cleaned = false;
                    for (int i = 0; i < array.length(); i++) {
                        if (adapter != null && !adapter.isEmpty() && !cleaned) {
                            cleaned = true;
                            adapter.clear();
                        }

                        items = new Items();
                        items.setProd_name(array.getJSONObject(i).getString("prod_name"));
                        items.setProd_address(array.getJSONObject(i).getString("prod_address"));
                        items.setDescription(array.getJSONObject(i).getString("description"));
                        items.setPrice(array.getJSONObject(i).getString("price"));
                        items.setCat_name(array.getJSONObject(i).getString("cat_name"));
                        items.setRate(array.getJSONObject(i).getString("rate"));
                        items.setProd_id(array.getJSONObject(i).getString("prod_id"));


                        list.add(items);

                        progressDialog.dismiss();
                    }

                    adapter = new CustomListViewAdapter(context, R.layout.listview_row, list);
                    listView.setAdapter(adapter);
                    cleaned = false;
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
                                    .putExtra("prod_name", list.get(position).getProd_name())
                                    .putExtra("prod_name", list.get(position).getProd_name())
                                    .putExtra("user_id", mId)
                                    .putExtra("prod_id", list.get(position).getProd_id())
                            );
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    dialog.showDialog(MainActivity.this, "Error occurred, please try again");
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

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (f == 0) {
                    f += 1;
                    APIRequestAllItems(MainActivity.this);
                }
                Log.e("Main", "data search" + newText);
                adapter= (CustomListViewAdapter) listView.getAdapter();
                 adapter.getFilter().filter(newText);

                return true;
            }
        });


        return true;

    }


}



