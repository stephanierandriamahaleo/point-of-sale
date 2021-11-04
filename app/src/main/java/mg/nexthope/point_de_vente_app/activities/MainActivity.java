package mg.nexthope.point_de_vente_app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.api.Api;
import mg.nexthope.point_de_vente_app.api.JsonPlaceholderApi;
import mg.nexthope.point_de_vente_app.constants.Constant;
import mg.nexthope.point_de_vente_app.models.UserRequest;
import mg.nexthope.point_de_vente_app.services.UtilsService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private TextInputEditText email;
    private TextInputEditText password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
    }

    private void initComponent() {
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        if(!sharedPreferences.getString("email", "").isEmpty()) {
            email.setText(sharedPreferences.getString("email", ""));
            password.setText(sharedPreferences.getString("password", ""));
        }

        loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilsService.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    checklogin(email.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Vérifiez votre connexion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checklogin(String email, String password) {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Connexion");
        progressDialog.setMessage("Connexion en cours...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Authentification nom / mot de passe
        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);
        UserRequest user = new UserRequest(email, password, "ROLE_USER_SHOP");
        Call<ResponseBody> loginCall = jsonPlaceHolderApi.login(user);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 201) {
                    String body = null;
                    try {
                        body = response.body().string();
                        JsonObject jsonBody = new Gson().fromJson(body, JsonObject.class);

                        saveSettings(
                                Constant.URL,
                                jsonBody.get("token").getAsJsonObject().get("value").getAsString(),
                                jsonBody.get("user").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt(),
                                email,
                                password
                        );

                        Intent intent = new Intent(MainActivity.this, ShopListActivity.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Vérifiez les informations saisies", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("Failure");
                t.printStackTrace();
            }
        });
        return false;
    }

    private void saveSettings(String url, String token, int idUser, String email, String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("login_url");
        editor.remove("api_url");
        editor.remove("url");
        editor.remove("token");
        editor.remove("idUser");
        editor.remove("email");
        editor.remove("password");
        editor.putString("login_url", url + "/api/auth-tokens");
        editor.putString("api_url", url + "/api/android/event/billet/check");
        editor.putString("url", url);
        editor.putString("token", token);
        editor.putInt("idUser", idUser);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}