package com.sri.finddistance.gson;

import java.util.ArrayList;

/**
 * Created by sridhar on 15/12/15.
 */
public class GeoCode {

    public ArrayList<AddressComponents> results = new ArrayList<AddressComponents>();
    public String status;

    public static class AddressComponents {
        public ArrayList<Address> address_components = new ArrayList<Address>();
        public String formatted_address;
        public String place_id;
    }

    public static class Address {
        public String long_name;
        public String short_name;
        public String[] types;
    }

}
