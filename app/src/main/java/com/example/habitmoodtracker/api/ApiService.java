package com.example.habitmoodtracker.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("random")
    Call<QuoteResponse> getRandomQuote();
}