package com.laioffer.TravelPlanner.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.laioffer.TravelPlanner.entity.GoogleResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController {

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView getSearchPages() {
        ModelAndView model = new ModelAndView("search");
        return model;
    }

    @RequestMapping(value = "/search/{placeId}", method = RequestMethod.POST)
    public @ResponseBody
    List<String> getAttractionList(@RequestParam String placeId) throws IOException {
        String API_KEY = "AIzaSyB12CQWfe0bkz0f5T-D2QpeRdgiHIyMYHg"; /*Personal Google API Key*/
        String google_map = "https://maps.googleapis.com/maps/api/place";
        String charset = "UTF-8";
        String details_field = String.format("/details/json?place_id=%s&fields=name,rating,formatted_phone_number&key=%s", placeId, API_KEY);

        URL city_detail = new URL(google_map + details_field);
        Reader reader = new InputStreamReader(city_detail.openStream(), charset);
        JsonObject city_detail_json = new Gson().fromJson(reader, JsonObject.class);

        JsonObject location_obj = city_detail_json.getAsJsonObject("geometry").getAsJsonObject("location");
        String location = location_obj.get("lan") + "," + location_obj.get("lon");
        int radius = 50000; /*meters*/
        String type = "tourist_attraction";
        String nearby_search = String.format("/nearbysearch/json?location=%s&radius=%d&type=%s&key=%s", location, radius, type, API_KEY);

        URL nearby = new URL(google_map + nearby_search);
        reader = new InputStreamReader(nearby.openStream(), charset);
        JsonArray nearby_attractions_jsonarray = (new Gson().fromJson(reader, JsonObject.class)).getAsJsonArray("results");

        List<String> res = new ArrayList<>();
        for(JsonElement attraction : nearby_attractions_jsonarray) {
            res.add(attraction.getAsJsonObject().get("place_id").getAsString());
        }

        return res;
    }

}
