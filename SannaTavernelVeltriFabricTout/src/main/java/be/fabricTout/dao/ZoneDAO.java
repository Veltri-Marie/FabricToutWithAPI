package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import be.fabricTout.javabeans.Zone;

public class ZoneDAO extends DAO<Zone> {

    public ZoneDAO(ServletContext context) {
        super(context);
    }

    @Override
    public boolean createDAO(Zone zone) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(zone);

            String response = getResource()
                    .path("zone")
                    .type(MediaType.APPLICATION_JSON)
                    .post(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Zone zone) {
        try {
            String response = getResource()
                    .path("zone/" + zone.getZoneId())
                    .accept(MediaType.APPLICATION_JSON)
                    .delete(String.class);

            return response != null && !response.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Zone zone) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(zone);

            String response = getResource()
                    .path("zone/" + zone.getZoneId())
                    .type(MediaType.APPLICATION_JSON)
                    .put(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Zone findDAO(int id) {
        Zone zone = null;
        try {
            String response = getResource()
                    .path("zone/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

                       
            JSONObject json = new JSONObject(response);
            System.out.println("Manager findDAO Raw JSON Response: " + json);
            
	        zone = new Zone(json);

	        System.out.println("Deserialization Successful: " + zone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zone;
    }

    @Override
    public List<Zone> findAllDAO() {
        List<Zone> zones = new ArrayList<>();

        try {
            String response = getResource()
                    .path("zone")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            System.out.println("Zone findAllDAO Raw JSON Response: " + response);

	        if (response == null || response.isEmpty()) {
	            return zones;
	        }

	        JSONArray jsonArray = new JSONArray(response);
	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject json = jsonArray.getJSONObject(i);
	            Zone zone = new Zone(json);
	            zones.add(zone); 
	        }

	        System.out.println("Managers successfully deserialized: " + zones);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zones;
    }
}
