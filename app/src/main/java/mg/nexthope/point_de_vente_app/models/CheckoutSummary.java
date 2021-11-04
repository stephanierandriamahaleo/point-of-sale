package mg.nexthope.point_de_vente_app.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CheckoutSummary implements Serializable {
    int reservationId;
    String checkoutNumber;
    String reservationDate;
    String purchaser;
    double amount;
    ArrayList<Ticket> tickets;

    public String getCheckoutNumber() {
        return checkoutNumber;
    }

    public void setCheckoutNumber(String checkoutNumber) {
        this.checkoutNumber = checkoutNumber;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
}
