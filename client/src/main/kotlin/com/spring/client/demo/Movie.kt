package com.spring.client.demo

import java.util.*

data class Movie(val id: String, val title: String, val genre: String)
data class MovieEvent(val movie: Movie, val `when`: Date, val user: String)
