package mg.nexthope.point_de_vente_app.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import mg.nexthope.point_de_vente_app.models.CheckoutSummary;
import mg.nexthope.point_de_vente_app.models.Event;
import mg.nexthope.point_de_vente_app.models.Ticket;

public class CheckoutSummaryService {
    public static CheckoutSummary getCheckoutSummary(JsonObject checkoutSummaryJsonObject) {
        CheckoutSummary checkoutSummary = new CheckoutSummary();

        JsonObject reservationJsonObject = checkoutSummaryJsonObject.get("reservation").getAsJsonObject();
        checkoutSummary.setCheckoutNumber(reservationJsonObject.get("nom_reservation").getAsString());
        checkoutSummary.setReservationDate(reservationJsonObject.get("created_at").getAsString());
        checkoutSummary.setPurchaser(
                reservationJsonObject.get("user_checkout").getAsJsonObject().get("nom").getAsString()
                + " " +
                reservationJsonObject.get("user_checkout").getAsJsonObject().get("prenom").getAsString()
        );
        checkoutSummary.setAmount(reservationJsonObject.get("montant_total").getAsDouble());
        checkoutSummary.setReservationId(reservationJsonObject.get("id").getAsInt());

        JsonArray ticketJsonArray = checkoutSummaryJsonObject.get("billet").getAsJsonArray();
        ArrayList<Ticket> tickets = new ArrayList<>();
        for(int i = 0; i < ticketJsonArray.size(); i++) {
            Ticket ticket = new Ticket();
            ticket.setNumber(ticketJsonArray.get(i).getAsJsonObject().get("identifiant").getAsString());
            ticket.setPrice(ticketJsonArray.get(i).getAsJsonObject().get("prix").getAsDouble());
            ticket.setPlace(ticketJsonArray.get(i).getAsJsonObject().get("place_id").getAsString());
            ticket.setSection(ticketJsonArray.get(i).getAsJsonObject().get("section_id").getAsString());
            ticket.setLabel(
                    ticketJsonArray.get(i).getAsJsonObject().get("type_billet")
                            .getAsJsonObject().get("libelle").getAsString()
            );
            tickets.add(ticket);
        }

        checkoutSummary.setTickets(tickets);

        return checkoutSummary;
    }
}
