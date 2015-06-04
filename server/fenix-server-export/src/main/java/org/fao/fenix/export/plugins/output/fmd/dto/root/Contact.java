package org.fao.fenix.export.plugins.output.fmd.dto.root;


public class Contact {

    private String country;
    private String date;
    private String name;
    private String contact;

    public Contact(String country, String date, String name, String contact) {
        this.country = country;
        this.date = date;
        this.name = name;
        this.contact = contact;
    }

    public Contact() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
