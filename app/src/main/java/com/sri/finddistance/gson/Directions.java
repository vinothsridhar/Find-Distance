package com.sri.finddistance.gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sridhar on 16/12/15.
 */
public class Directions implements Serializable {

    public String status;
    public ArrayList<Route> routes = new ArrayList<Route>();

    public static class Route implements Serializable {
        public ArrayList<Leg> legs = new ArrayList<Leg>();
    }

    public static class Leg implements Serializable {
        public TextAndValue distance;
        public TextAndValue duration;
        public String end_address;
        public LatLng end_location;
        public String start_address;
        public LatLng start_location;
        public ArrayList<Step> steps = new ArrayList<Step>();
    }

    public static class TextAndValue implements Serializable {
        public String text;
        public String value;
    }

    public static class LatLng implements Serializable {
        public double lat;
        public double lng;
    }

    public static class PolyLine implements Serializable {
        public String points;
    }

    public static class Step implements Serializable {
        public TextAndValue distance;
        public TextAndValue duration;
        public LatLng end_location;
        public String html_instructions;
        public LatLng start_location;
        public String travel_mode;
        public PolyLine polyline;
    }

    public String getDistance() {
        return routes.get(0).legs.get(0).distance.text;
    }

}
