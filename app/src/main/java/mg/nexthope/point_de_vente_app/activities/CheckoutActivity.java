package mg.nexthope.point_de_vente_app.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.adapters.CartListAdapter;
import mg.nexthope.point_de_vente_app.adapters.EventListAdapter;
import mg.nexthope.point_de_vente_app.adapters.TicketListAdapter;
import mg.nexthope.point_de_vente_app.api.Api;
import mg.nexthope.point_de_vente_app.api.JsonPlaceholderApi;
import mg.nexthope.point_de_vente_app.constants.Constant;
import mg.nexthope.point_de_vente_app.models.CartItem;
import mg.nexthope.point_de_vente_app.models.CheckoutRequest;
import mg.nexthope.point_de_vente_app.models.CheckoutSummary;
import mg.nexthope.point_de_vente_app.models.Event;
import mg.nexthope.point_de_vente_app.models.Ticket;
import mg.nexthope.point_de_vente_app.services.CheckoutSummaryService;
import mg.nexthope.point_de_vente_app.services.EventService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RadioGroup paymentRadioGroup;
    private RadioButton paymentRadioButton;
    private TextView name;
    private TextView firstname;
    private TextView phone;
    private TextView email;
    private AppCompatCheckBox prePrint;
    private MaterialRippleLayout confirmButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;

    private CartListAdapter cartListAdapter;
    private HashMap<Ticket, Integer> checkouts;
    int shopId;
    int eventId;
    double total;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initToolbar();
        initComponent();

        shopId = getIntent().getExtras().getInt("shopId");
        eventId = getIntent().getExtras().getInt("eventId");
        total = getIntent().getExtras().getDouble("total");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.get("cartItems") != null) {
                checkouts = (HashMap<Ticket, Integer>) extras.get("cartItems");
                cartListAdapter = new CartListAdapter(CheckoutActivity.this, checkouts);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(cartListAdapter);
            } else {
                System.out.println("b");
            }

        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true); */
        getSupportActionBar().setTitle("Confirmation de la commande");
    }

    private void initComponent() {
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.checkout_list_recycler_view);
        recyclerView = findViewById(R.id.checkout_list_recycler_view);
        manager = new LinearLayoutManager(this);

        name = findViewById(R.id.checkout_name);
        firstname = findViewById(R.id.checkout_first_name);
        phone = findViewById(R.id.checkout_phone);
        email = findViewById(R.id.checkout_email);
        prePrint = findViewById(R.id.checkout_pre_print);
        paymentRadioGroup = findViewById(R.id.checkout_payment_radio_group);
        paymentRadioButton = findViewById(R.id.checkout_cash_radio);
        confirmButton = findViewById(R.id.checkout_confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCheckout();
            }
        });
    }

    private void confirmCheckout() {
        ArrayList<CartItem> items = new ArrayList<>();
        for (Map.Entry ticket : checkouts.entrySet()) {
            CartItem item = new CartItem();
            item.setTicketId(((Ticket)(ticket.getKey())).getId());
            item.setQuantity((Integer) ticket.getValue());
            items.add(item);
        }
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setCartItems(items);
        checkoutRequest.setEmail(email.getText().toString());
        checkoutRequest.setFirstname(firstname.getText().toString());
        checkoutRequest.setLastname(name.getText().toString());
        checkoutRequest.setPhone(phone.getText().toString());
        checkoutRequest.setShopId(shopId);
        checkoutRequest.setUserId(sharedPreferences.getInt("idUser", 0));
        checkoutRequest.setEventId(eventId);
        checkoutRequest.setPayment(paymentRadioButton.getText().toString());
        checkoutRequest.setPrePrint(prePrint.isChecked());
        checkoutRequest.setTotal(total);

        // if (checkoutRequest.isValid()) {
            JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);

            Call<ResponseBody> confirmCheckoutCall = jsonPlaceHolderApi.confirmCheckout(
                    checkoutRequest,
                    sharedPreferences.getString("token", "")
            );

            confirmCheckoutCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200) {
                        String body = null;
                        try {
                            body = response.body().string();
                            JsonObject checkoutSummaryJson = new Gson().fromJson(body, JsonObject.class);
                            CheckoutSummary checkoutSummary = CheckoutSummaryService.getCheckoutSummary(checkoutSummaryJson);

                            Intent intent = new Intent(CheckoutActivity.this, CheckoutSummaryActivity.class);
                            intent.putExtra("shopId", shopId);
                            intent.putExtra("eventId", eventId);
                            intent.putExtra("checkoutSummary", checkoutSummary);

                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(CheckoutActivity.this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CheckoutActivity.this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                }
            });
        /*} else {
            Toast.makeText(CheckoutActivity.this, "Veuillez remplir les champs", Toast.LENGTH_SHORT).show();
        }*/
    }
}