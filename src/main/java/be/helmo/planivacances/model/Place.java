package be.helmo.planivacances.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class Place {
    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Le numéro de boite est invalide")
    private String number;
    @NotBlank
    @Size(min = 4, message = "Le code postal doit comprendre au moins 4 chiffres")
    @Pattern(regexp = "^\\d{4,}$", message = "Le code postal est invalide")
    private String postalCode;

    @NotBlank
    private double lat;
    @NotBlank
    private double lon;


    //getters


    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getAddress() {
        return String.format("%s, %s, %s %s, %s", getStreet(), getNumber(), getCity(), getPostalCode(), getCountry());
    }


    //setters


    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

}
