package mg.nexthope.point_de_vente_app.models;

public class CartItem {
    int ticketId;
    String ticketLabel;
    int quantity;

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketLabel() {
        return ticketLabel;
    }

    public void setTicketLabel(String ticketLabel) {
        this.ticketLabel = ticketLabel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
