package adam.de.pickersdk.model;

import java.io.Serializable;

/*
    PlacePicker sdk model is used to store the values of lat, lng and address being returned
    on result of the application side
 */

public class PlaceModel implements Serializable {

    private Double lat;
    private Double lng;
    private String address;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
