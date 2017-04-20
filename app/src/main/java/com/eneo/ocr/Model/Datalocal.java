package com.eneo.ocr.Model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by stephineosoro on 28/06/16.
 */
public class Datalocal extends DataSupport {

    @Column(unique = true)
    private long id;


    private String meter_serial;

    private String meter_index;

    private String island;

    private String zone;

    private String agency;

    private String agencyID;

    private String datetime;

    private String latitude;

    private String longitude;

    private String locationName;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getMeter_serial() {
        return meter_serial;
    }

    public void setMeter_serial(String meter_serial) {
        this.meter_serial = meter_serial;
    }

    public String getMeter_index() {
        return meter_index;
    }

    public void setMeter_index(String meter_index) {
        this.meter_index = meter_index;
    }

    public String getIsland() {
        return island;
    }

    public void setIsland(String island) {
        this.island = island;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(String agencyID) {
        this.agencyID = agencyID;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



}

