package mg.nexthope.point_de_vente_app.services;

import com.google.gson.JsonObject;

import mg.nexthope.point_de_vente_app.models.Shop;
import mg.nexthope.point_de_vente_app.models.Ticket;

public class TicketService {
    public static Ticket getTicket(JsonObject ticketJsonObject) {
        Ticket ticket = new Ticket();

        try {
            ticket.setId(Integer.parseInt(ticketJsonObject.get("id").getAsString()));
            ticket.setLabel(ticketJsonObject.get("libelle").getAsString());
            ticket.setDescription(ticketJsonObject.get("description").getAsString());
            ticket.setPrice(Double.parseDouble(ticketJsonObject.get("prix").getAsString()));
            ticket.setCurrency(ticketJsonObject.get("code").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return ticket;
    }
}
