package mg.nexthope.point_de_vente_app.models;

import mg.nexthope.point_de_vente_app.constants.Constant;

public class Event {
    private int id;
    private String title;
    private String place;
    private String date;
    private String ticketPriceRange;
    private String pictureUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTicketPriceRange() {
        return ticketPriceRange;
    }

    public void setTicketPriceRange(String ticketPriceRange) {
        this.ticketPriceRange = ticketPriceRange;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = Constant.URL + "img/events/" + pictureUrl;
    }
}
