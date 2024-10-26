package com.citius.mcsanit

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class Helper {

    companion object{
        fun deg2rad(deg: Double): Double {
            return (deg * Math.PI / 180.0)
        }

        fun rad2deg(rad: Double): Double {
            return (rad * 180.0 / Math.PI)
        }
        fun cal_distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(
                deg2rad(lat1)
            ) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
            dist = acos(dist)
            dist = rad2deg(dist)
            dist = dist * 60 * 1.1515 * 1609.344
            return (dist)
        }
    }
}