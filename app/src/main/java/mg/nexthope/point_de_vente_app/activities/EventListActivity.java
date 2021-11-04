package mg.nexthope.point_de_vente_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.adapters.EventListAdapter;
import mg.nexthope.point_de_vente_app.adapters.ShopListAdapter;
import mg.nexthope.point_de_vente_app.api.Api;
import mg.nexthope.point_de_vente_app.api.JsonPlaceholderApi;
import mg.nexthope.point_de_vente_app.constants.Constant;
import mg.nexthope.point_de_vente_app.models.Event;
import mg.nexthope.point_de_vente_app.models.Shop;
import mg.nexthope.point_de_vente_app.services.EventService;
import mg.nexthope.point_de_vente_app.services.ShopService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivity extends AppCompatActivity implements EventListAdapter.OnEventListListener {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private EventListAdapter eventListAdapter;
    int shopId;
    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        shopId = getIntent().getExtras().getInt("shopId");

        initToolbar();
        initComponent();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EventListActivity.this, ShopListActivity.class);
        startActivity(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true); */
        getSupportActionBar().setTitle("Evènements");
    }

    private void initComponent() {
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.event_recycler_view);
        manager = new LinearLayoutManager(this);

        getShopEvents();
    }

    private int getShopEvents() {
        ProgressDialog progressDialog = new ProgressDialog(EventListActivity.this);
        progressDialog.setTitle("Récupération des données");
        progressDialog.setMessage("Un instant...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);

        Call<ResponseBody> getEventsCall = jsonPlaceHolderApi.getShopEvents(
                shopId,
                sharedPreferences.getString("token", "")
        );


        getEventsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String body = null;
                    try {
                        body = response.body().string();
                        JsonArray eventsJsonArray = new Gson().fromJson(body, JsonObject.class).get("events").getAsJsonArray();

                        if(eventsJsonArray.size() != 0) {
                            ArrayList<Event> events = new ArrayList<>();

                            for(int i = 0; i < eventsJsonArray.size(); i++) {
                                JsonObject eventJsonObject = eventsJsonArray.get(i).getAsJsonObject();
                                Event event = EventService.getEvent(eventJsonObject);
                                events.add(event);
                            }
                            EventListActivity.this.events = events;
                            eventListAdapter = new EventListAdapter(events, EventListActivity.this, EventListActivity.this);

                            recyclerView.setLayoutManager(manager);
                            recyclerView.setAdapter(eventListAdapter);
                            progressDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(EventListActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("Failure");
                t.printStackTrace();
            }
        });
        return 0;
    }

    @Override
    public void onEventClick(int position) {
        Intent intent = new Intent(EventListActivity.this, TicketListActivity.class);
        intent.putExtra("eventId", events.get(position).getId());
        intent.putExtra("shopId", shopId);
        startActivity(intent);
    }
}