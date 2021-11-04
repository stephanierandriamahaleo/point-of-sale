package mg.nexthope.point_de_vente_app.services;

import com.google.gson.JsonObject;

import mg.nexthope.point_de_vente_app.models.Shop;

public class ShopService {
    public static Shop getShop(JsonObject shopJsonObject) {
        Shop shop = new Shop();

        try {
            shop.setId(Integer.parseInt(shopJsonObject.get("shop_id").getAsString()));
            shop.setName(shopJsonObject.get("nom").getAsString());
            shop.setAddress(shopJsonObject.get("adresse").getAsString());
            shop.setPhone(!shopJsonObject.get("telephone").isJsonNull()
                    ? shopJsonObject.get("telephone").getAsString()
                    : ""
            );
            shop.setOpening(!shopJsonObject.get("heure_ouverture").isJsonNull()
                    ? shopJsonObject.get("heure_ouverture").getAsString()
                    : ""
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return shop;
    }
}
