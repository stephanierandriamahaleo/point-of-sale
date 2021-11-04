package mg.nexthope.point_de_vente_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.adapters.ShopListAdapter;
import mg.nexthope.point_de_vente_app.api.Api;
import mg.nexthope.point_de_vente_app.api.JsonPlaceholderApi;
import mg.nexthope.point_de_vente_app.constants.Constant;
import mg.nexthope.point_de_vente_app.models.Shop;
import mg.nexthope.point_de_vente_app.services.ShopService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopListActivity extends AppCompatActivity implements ShopListAdapter.OnShopListListener {
    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ShopListAdapter shopListAdapter;
    private ArrayList<Shop> shops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_de_vente_list);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Points de vente");
    }

    private void initComponent() {
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);

        recyclerView = findViewById(R.id.shop_recycler_view);
        manager = new LinearLayoutManager(this);

        getUserShops();
    }

    private int getUserShops() {
        ProgressDialog progressDialog = new ProgressDialog(ShopListActivity.this);
        progressDialog.setTitle("Récupération des données");
        progressDialog.setMessage("Un instant...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);

        Call<ResponseBody> getShopsCall = jsonPlaceHolderApi.getUserShops(
                sharedPreferences.getString("token", "")
        );


        getShopsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String body = null;
                    try {
                        body = response.body().string();
                        JsonArray shopsJsonArray = new Gson().fromJson(body, JsonObject.class).get("shop").getAsJsonArray();

                        if(shopsJsonArray.size() != 0) {
                            ArrayList<Shop> shops = new ArrayList<>();

                            for(int i = 0; i < shopsJsonArray.size(); i++) {
                                JsonObject shopJsonObject = shopsJsonArray.get(i).getAsJsonObject();
                                Shop shop = ShopService.getShop(shopJsonObject);
                                shops.add(shop);
                            }

                            ShopListActivity.this.shops = shops;

                            shopListAdapter = new ShopListAdapter(shops, ShopListActivity.this);

                            recyclerView.setLayoutManager(manager);
                            recyclerView.setAdapter(shopListAdapter);
                            progressDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(ShopListActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
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
    public void onShopClick(int position) {
        Intent intent = new Intent(ShopListActivity.this, EventListActivity.class);
        intent.putExtra("shopId", shops.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShopListActivity.this, MainActivity.class);
        startActivity(intent);
    }
}