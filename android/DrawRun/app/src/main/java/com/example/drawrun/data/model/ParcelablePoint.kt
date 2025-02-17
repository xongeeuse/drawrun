package com.example.drawrun.data.model

import android.os.Parcel
import android.os.Parcelable
import com.mapbox.geojson.Point

data class ParcelablePoint(val point: Point) : Parcelable {
    constructor(parcel: Parcel) : this(
        Point.fromLngLat(parcel.readDouble(), parcel.readDouble())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(point.longitude())
        parcel.writeDouble(point.latitude())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelablePoint> {
        override fun createFromParcel(parcel: Parcel): ParcelablePoint {
            return ParcelablePoint(parcel)
        }

        override fun newArray(size: Int): Array<ParcelablePoint?> {
            return arrayOfNulls(size)
        }
    }
}
