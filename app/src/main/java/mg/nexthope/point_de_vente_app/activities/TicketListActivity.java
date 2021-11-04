package mg.nexthope.point_de_vente_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import mg.nexthope.point_de_vente_app.R;
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
import mg.nexthope.point_de_vente_app.services.TicketService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class TicketListActivity extends AppCompatActivity implements TicketListAdapter.OnTicketListListener {
    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerViewTicket;
    private LinearLayoutManager managerTicket;
    private TextInputEditText name;
    private TextInputEditText firstname;
    private TextInputEditText phone;
    private TextInputEditText email;
    private AppCompatCheckBox prePrint;
    private RadioButton paymentRadioButton;

    private TicketListAdapter ticketListAdapter;
    private ArrayList<Ticket> tickets;
    private String currency;

    int eventId;
    int shopId;
    public TextView total;
    public TextView currencyTextView;
    public MaterialRippleLayout buyButton;
    public MaterialRippleLayout confirmButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        eventId = getIntent().getExtras().getInt("eventId");
        shopId = getIntent().getExtras().getInt("shopId");

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true); */
        getSupportActionBar().setTitle("Billets");
    }

    private void initComponent() {

        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);

        name = findViewById(R.id.checkout_name);
        firstname = findViewById(R.id.checkout_first_name);
        phone = findViewById(R.id.checkout_phone);
        email = findViewById(R.id.checkout_email);
        prePrint = findViewById(R.id.checkout_pre_print);
        paymentRadioButton = findViewById(R.id.checkout_cash_radio);

        recyclerViewTicket = findViewById(R.id.ticket_list_recycler_view);
        managerTicket = new LinearLayoutManager(this);
        total = findViewById(R.id.ticket_list_total_amount);
        currencyTextView = findViewById(R.id.ticket_list_currency);
        // buyButton = findViewById(R.id.ticket_list_buy_button);
        confirmButton = findViewById(R.id.ticket_checkout_confirm_button);
        getEventTickets();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCheckout();
            }
        });

    }

    private void confirmCheckout() {
        if(Double.parseDouble(total.getText().toString()) == 0) {
            Toast.makeText(TicketListActivity.this, "Le panier est encore vide", Toast.LENGTH_LONG).show();
        } else {
            ArrayList<CartItem> items = new ArrayList<>();
            for (Map.Entry ticket : ticketListAdapter.cartItems.entrySet()) {
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
            checkoutRequest.setTotal(Double.parseDouble(total.getText().toString()));

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

                            Intent intent = new Intent(TicketListActivity.this, CheckoutSummaryActivity.class);
                            intent.putExtra("shopId", shopId);
                            intent.putExtra("eventId", eventId);
                            intent.putExtra("checkoutSummary", checkoutSummary);
                            intent.putExtra("currency", currency);

                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(TicketListActivity.this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(TicketListActivity.this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                }
            });
        }

        /*} else {
            Toast.makeText(CheckoutActivity.this, "Veuillez remplir les champs", Toast.LENGTH_SHORT).show();
        }*/
    }

    private int getEventTickets() {
        ProgressDialog progressDialog = new ProgressDialog(TicketListActivity.this);
        progressDialog.setTitle("Récupération des données");
        progressDialog.setMessage("Un instant...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);

        Call<ResponseBody> getTicketsCall = jsonPlaceHolderApi.getEventTickets(
                shopId,
                eventId,
                sharedPreferences.getString("token", "")
        );


        getTicketsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String body = null;
                    try {
                        body = response.body().string();
                        JsonArray ticketsJsonArray = new Gson().fromJson(body, JsonObject.class).get("billets").getAsJsonArray();

                        if(ticketsJsonArray.size() != 0) {
                            ArrayList<Ticket> tickets = new ArrayList<>();

                            for(int i = 0; i < ticketsJsonArray.size(); i++) {
                                JsonObject ticketJsonObject = ticketsJsonArray.get(i).getAsJsonObject();
                                Ticket ticket = TicketService.getTicket(ticketJsonObject);
                                currency = ticket.getCurrency();
                                tickets.add(ticket);
                            }
                            currencyTextView.setText(currency);
                            TicketListActivity.this.tickets = tickets;
                            ticketListAdapter = new TicketListAdapter(TicketListActivity.this, TicketListActivity.this, tickets, TicketListActivity.this);

                            recyclerViewTicket.setLayoutManager(managerTicket);
                            recyclerViewTicket.setAdapter(ticketListAdapter);
                            /*buyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Double.parseDouble(total.getText().toString()) == 0) {
                                        Toast.makeText(TicketListActivity.this, "Votre panier est vide", Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent intent = new Intent(TicketListActivity.this, CheckoutActivity.class);
                                        intent.putExtra("cartItems", ticketListAdapter.cartItems);
                                        intent.putExtra("shopId", shopId);
                                        intent.putExtra("eventId", eventId);
                                        intent.putExtra("total", Double.parseDouble(total.getText().toString()));
                                        startActivity(intent);
                                    }

                                }
                            });*/
                            progressDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(TicketListActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        Intent intent = new Intent(TicketListActivity.this, EventListActivity.class);
        intent.putExtra("shopId", shopId);
        startActivity(intent);
    }

    @Override
    public void onTicketListListener(int position) {
    }
}