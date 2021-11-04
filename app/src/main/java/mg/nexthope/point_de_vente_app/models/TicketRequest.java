package mg.nexthope.point_de_vente_app.models;

public class TicketRequest {
    int reservationId;
    int shopId;
    int eventId;

    public TicketRequest(int reservationId, int shopId, int eventId) {
        this.reservationId = reservationId;
        this.shopId = shopId;
        this.eventId = eventId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
