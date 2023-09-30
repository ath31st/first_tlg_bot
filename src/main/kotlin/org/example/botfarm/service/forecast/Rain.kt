package org.example.botfarm.service.forecast

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h") var h3: Double? = null,
)
