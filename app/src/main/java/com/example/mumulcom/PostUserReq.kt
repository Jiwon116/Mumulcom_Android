package com.example.mumulcom

import com.google.gson.annotations.SerializedName

data class PostUserReq(

    @SerializedName("reporterUserIdx") var reporterUserIdx:Long,
    @SerializedName("reportedUserIdx") var reportedUserIdx:Long,
    @SerializedName("reportTypeIdx") var reportTypeIdx:Long,
    @SerializedName("reportContent") var reportContent:String,


)
