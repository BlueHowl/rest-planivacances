package be.helmo.planivacances.model;

import com.google.cloud.firestore.annotation.Exclude;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Place {
    @NotNull
    private String address; //Label ? permet de limiter les appels geocoding
    @NotNull
    private double lat;
    @NotNull
    private double lon;

    //getters

    public String getAddress() {
        return address;
    }

    /*@Exclude
    public String getLatLong() {
        return String.format("%f,%f", lat, lon);
    }*/

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }


    //setters

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

}
