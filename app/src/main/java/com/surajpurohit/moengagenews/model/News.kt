package com.surajpurohit.moengagenews.model

import android.net.Uri

data class News(val newsSource : String, val newsDate : String, val newsHeadline : String, val newsImage : String, val newsAuthor : String, val newsDescription : String, val newsUrl : String){
}