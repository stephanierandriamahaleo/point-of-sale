package mg.nexthope.point_de_vente_app.api;

import mg.nexthope.point_de_vente_app.models.CheckoutRequest;
import mg.nexthope.point_de_vente_app.models.TicketRequest;
import mg.nexthope.point_de_vente_app.models.UserRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceholderApi {
    @POST("api/auth-tokens")
    Call<ResponseBody> login(@Body UserRequest userRequest);

    @GET("api/android/user-shop/front-event/shop/list")
    Call<ResponseBody> getUserShops(@Header("X-Auth-Token") String authorization);

    @GET("/api/android/user-shop/front-event/eventByShop/list/{shop_id}")
    Call<ResponseBody> getShopEvents(@Path("shop_id") int shopId, @Header("X-Auth-Token") String authorization);

    @GET("api/android/user-shop/event/ticket/list/{shop_id}/{event_id}")
    Call<ResponseBody> getEventTickets(@Path("shop_id") int shopId, @Path("event_id") int eventId, @Header("X-Auth-Token") String authorization);

    @POST("api/android/user-shop/checkout/confirm")
    Call<ResponseBody> confirmCheckout(@Body CheckoutRequest checkoutRequest, @Header("X-Auth-Token") String authorization);

    @POST("api/android/user-shop/checkout/printTicket")
    Call<ResponseBody> printTicket(@Body TicketRequest ticketRequest, @Header("X-Auth-Token") String authorization);
}
