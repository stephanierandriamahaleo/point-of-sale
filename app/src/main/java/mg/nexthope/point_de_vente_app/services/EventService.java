package mg.nexthope.point_de_vente_app.services;

import com.google.gson.JsonObject;

import mg.nexthope.point_de_vente_app.models.Event;

public class EventService {
    public static Event getEvent(JsonObject eventJsonObject) {
        Event event = new Event();

        event.setId(Integer.parseInt(eventJsonObject.get("id").getAsString()));
        event.setTitle(eventJsonObject.get("titre_evenement").getAsString());
        event.setDate(eventJsonObject.get("date_debut_event").getAsString());
        event.setPlace(eventJsonObject.get("adresse").getAsString());
        if (!eventJsonObject.get("image_event").isJsonNull()) {
            event.setPictureUrl(eventJsonObject.get("image_event").getAsString());
        }

        return event;
    }
}
