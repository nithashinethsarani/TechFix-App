package com.example.techfix_app.utils;

import android.location.Location;

import com.example.techfix_app.models.Branch;

import java.util.List;

public class LocationUtils {

    // Distance in meters between two coordinates
    public static float calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        return results[0];
    }

    // Returns the nearest branch to the given user location
    public static Branch findNearestBranch(double userLat, double userLng, List<Branch> branches) {
        Branch nearest = null;
        float minDistance = Float.MAX_VALUE;

        for (Branch branch : branches) {
            float distance = calculateDistance(userLat, userLng, branch.getLatitude(), branch.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = branch;
            }
        }
        return nearest;
    }
}