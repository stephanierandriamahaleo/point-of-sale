package mg.nexthope.point_de_vente_app.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static Retrofit INSTANCE;

    public static Retrofit getApi(String base_url) {
        if (INSTANCE == null) {
        INSTANCE = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        return INSTANCE;
    }
}
