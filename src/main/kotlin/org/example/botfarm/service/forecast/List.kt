package org.example.botfarm.service.forecast

import com.google.gson.annotations.SerializedName

data class List(
    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("main") var main: Main? = Main(),
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf(),
    @SerializedName("clouds") var clouds: Clouds? = Clouds(),
    @SerializedName("wind") var wind: Wind? = Wind(),
    @SerializedName("visibility") var visibility: Int? = null,
    @SerializedName("pop") var pop: Double? = null,
    @SerializedName("rain") var rain: Rain? = Rain(),
    @SerializedName("sys") var sys: Sys? = Sys(),
    @SerializedName("dt_txt") var dtTxt: String? = null
)