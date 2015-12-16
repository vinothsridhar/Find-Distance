package com.sri.finddistance.gson;

import java.util.ArrayList;

/**
 * Created by sridhar on 16/12/15.
 */
public class Directions {

    public String status;
    public ArrayList<Route> routes = new ArrayList<Route>();

    public static class Route {
        ArrayList<Leg> legs = new ArrayList<Leg>();
    }

    public static class Leg {
        public Distance distance;
    }

    public static class Distance {
        public String text;
        public String value;
    }

    public String getDistance() {
        return routes.get(0).legs.get(0).distance.text;
    }

}
