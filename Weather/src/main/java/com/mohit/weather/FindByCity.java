package com.mohit.weather;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class FindByCity extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public FindByCity() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get User Input
		String city = request.getParameter("city");
		
		// From https://openweathermap.org
		String apiKey = "344174d8b2fc94c97a1e494c00f725fe";
		String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
		
		// Connection Established
		URL url = new URL(apiURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		
		// Read Data From API
		InputStream inputStream = conn.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		// Store InputStreamReader Data Into String (We Dont Use Here String Because Of String Is Immutable And Data Is Frequntly Changes)
		StringBuilder responseContent = new StringBuilder();
		
		// Scan Data From Input Stream Reader
		Scanner sc = new Scanner(reader);
		
		// Convert Data Into String
		while(sc.hasNext()) {
			responseContent.append(sc.nextLine());
		}
		sc.close();
		
		// Typecasting or Parsing The Data Into JSON
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
		
		//Date & Time
        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
        String date = new Date(dateTimestamp).toString();
        
        //Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").toString();
        
        // Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("date", date);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        
        conn.disconnect();
        
        // Forward Request To Weather.jsp Page
        RequestDispatcher rd = request.getRequestDispatcher("weather.jsp");
        rd.forward(request, response);
	}

}
