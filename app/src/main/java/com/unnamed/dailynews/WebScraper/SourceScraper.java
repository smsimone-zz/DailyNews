package com.unnamed.dailynews.WebScraper;

import com.jaunt.ResponseException;
import com.jaunt.UserAgent;


public class SourceScraper {

    public String scrapPage(String url){
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.visit(url);

        }catch(ResponseException e){
            e.printStackTrace();
        }
        return "";
    }

}