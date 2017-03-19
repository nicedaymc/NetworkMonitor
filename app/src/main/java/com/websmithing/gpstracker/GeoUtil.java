package com.websmithing.gpstracker;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by yapeng on 17/03/2017.
 */

public class GeoUtil {
    /** Semi-major axis of earth in meter */
    public static final double WGS84_A = 6378137.0;

    /** Semi-minor axis of earth in meter */
    public static final double WGS84_B = 6356752.314245;

    /** Eccentricity of earth */
    public static final double WGS84_E =
            Math.sqrt( (WGS84_A * WGS84_A) / (WGS84_B * WGS84_B) - 1);

    public static final double DEGREES_TO_RADIANS = Math.PI / 180;

    public static ArrayList<LatLng> polyLoc =new ArrayList();
    static{

        polyLoc.add(new LatLng(22.212836, 113.558870));
        polyLoc.add(new LatLng(22.215706, 113.550847));
        polyLoc.add(new LatLng(22.217097, 113.550826));
        polyLoc.add(new LatLng(22.216976, 113.543734));
        polyLoc.add(new LatLng(22.212945, 113.541265));
        polyLoc.add(new LatLng(22.213639, 113.535804));
        polyLoc.add(new LatLng(22.212209, 113.533170));
        polyLoc.add(new LatLng(22.201616, 113.536021));
        polyLoc.add(new LatLng(22.194690, 113.534645));
        polyLoc.add(new LatLng(22.184810, 113.526953));
        polyLoc.add(new LatLng(22.156902, 113.539889));
        polyLoc.add(new LatLng(22.144340, 113.549497));
        polyLoc.add(new LatLng(22.136859, 113.549401));
        polyLoc.add(new LatLng(22.136099, 113.539561));
        polyLoc.add(new LatLng(22.123641, 113.540610));
        polyLoc.add(new LatLng(22.118631, 113.548075));
        polyLoc.add(new LatLng(22.106592, 113.550038));
        polyLoc.add(new LatLng(22.109859, 113.574191));
        polyLoc.add(new LatLng(22.134473, 113.603584));
        polyLoc.add(new LatLng(22.173183, 113.587079));
        polyLoc.add(new LatLng(22.190888, 113.567161));
        polyLoc.add(new LatLng(22.211297, 113.562463));
    }

    /**
     * Calculates a three-dimensional point in the
     * World Geodetic System (WGS84) from latitude and longitude.
     */
    public static Point3D latLonToPoint3D(double lat, double lon) {
        double clat = Math.cos(lat * DEGREES_TO_RADIANS);
        double slat = Math.sin(lat * DEGREES_TO_RADIANS);
        double clon = Math.cos(lon * DEGREES_TO_RADIANS);
        double slon = Math.sin(lon * DEGREES_TO_RADIANS);

        double N = WGS84_A / Math.sqrt(1.0 - WGS84_E * WGS84_E * slat * slat);

        double x = N  * clat * clon;
        double y = N  * clat * slon;
        double z = N * (1.0 - WGS84_E * WGS84_E) * slat;
        return new Point3D(x, y, z);
    }

    /**
     * Calculates distance of projection p of vector a on vector b.
     *
     * Use formula for projection, with p being the projection point:
     * <p>
     * p = a X b / |b|^2 * b
     * </p>
     * X being the dot product, * being multiplication of vector and constant
     */
    public static Point3D calculateProjection(Point3D a, Point3D b) {
        return b.multiply(a.dotProduct(b) / (b.dotProduct(b)));
    }

    /**
     * Calculates shortest distance of vector x and the line defined by
     * the vectors a and b.
     */
    public static double calculateDistanceToLine(Point3D x, Point3D a, Point3D b) {
        Point3D projectionOntoLine =
                calculateProjection(x.subtract(a), b.subtract(a)).add(a);
        return projectionOntoLine.distance(x);
    }

    public static double calDist(Point3D x, Point3D edgePoint1,Point3D edgePoint2){
        return Math.max(calculateDistanceToLine(x, edgePoint1, edgePoint2),
                Math.min(x.distance(edgePoint1), x.distance(edgePoint2)));
    }

    public static double distance(ArrayList<LatLng> polyLoc, LatLng location){
        double mindist = 1000000000;
        Point3D x = latLonToPoint3D(location.latitude, location.longitude);
        for (int i=0;i<polyLoc.size();i++){
            LatLng p1=polyLoc.get(i);
            Point3D p1_3D = latLonToPoint3D(p1.latitude,p1.longitude);
            LatLng p2;
            if (i<polyLoc.size()-1){
                p2=polyLoc.get(i+1);
            }else{
                p2=polyLoc.get(0);
            }
            Point3D p2_3D = latLonToPoint3D(p2.latitude,p2.longitude);
            double dist=calDist(x,p1_3D,p2_3D);
            if (dist<mindist) mindist=dist;
        }
        return mindist;
    }



   // ArrayList<LatLng> polyLoc = new ArrayList<LatLng>();

    public static boolean contains(ArrayList<LatLng> polyLoc, LatLng location)
    {
        if (location==null)
            return false;

        LatLng lastPoint = polyLoc.get(polyLoc.size()-1);
        boolean isInside = false;
        double x = location.longitude;

        for(LatLng point: polyLoc)
        {
            double x1 = lastPoint.longitude;
            double x2 = point.longitude;
            double dx = x2 - x1;

            if (Math.abs(dx) > 180.0)
            {
                // we have, most likely, just jumped the dateline (could do further validation to this effect if needed).  normalise the numbers.
                if (x > 0)
                {
                    while (x1 < 0)
                        x1 += 360;
                    while (x2 < 0)
                        x2 += 360;
                }
                else
                {
                    while (x1 > 0)
                        x1 -= 360;
                    while (x2 > 0)
                        x2 -= 360;
                }
                dx = x2 - x1;
            }

            if ((x1 <= x && x2 > x) || (x1 >= x && x2 < x))
            {
                double grad = (point.latitude - lastPoint.latitude) / dx;
                double intersectAtLat = lastPoint.latitude + ((x - x1) * grad);

                if (intersectAtLat > location.latitude)
                    isInside = !isInside;
            }
            lastPoint = point;
        }

        return isInside;
    }

}
