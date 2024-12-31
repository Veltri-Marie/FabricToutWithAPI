package be.fabricTout.dao;

import java.util.List;

import javax.servlet.ServletContext;

import be.fabricTout.javabeans.Machine;
import be.fabricTout.javabeans.Maintenance;

import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MaintenanceDAO extends DAO<Maintenance> {

    public MaintenanceDAO(ServletContext context) {
        super(context);
    }

    @Override
    public boolean createDAO(Maintenance maintenance) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(maintenance);

            String response = getResource()
                    .path("maintenance")
                    .type(MediaType.APPLICATION_JSON)
                    .post(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Maintenance maintenance) {
        try {
            String response = getResource()
                    .path("maintenance/" + maintenance.getIdMaintenance())
                    .accept(MediaType.APPLICATION_JSON)
                    .delete(String.class);

            return response != null && !response.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Maintenance maintenance) {
    	System.out.println("MaintenanceDAO.update in client");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(maintenance);

            String response = getResource()
                    .path("maintenance/" + maintenance.getIdMaintenance())
                    .type(MediaType.APPLICATION_JSON)
                    .put(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Maintenance findDAO(int id) {
        Maintenance maintenance = null;
        try {
            String response = getResource()
                    .path("maintenance/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            JSONObject json = new JSONObject (response);
            
            
            maintenance = new Maintenance(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maintenance;
    }

    @Override
    public List<Maintenance> findAllDAO() {
        List<Maintenance> maintenances = new ArrayList<>();

        try {
            String response = getResource()
                    .path("maintenance")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            JSONArray jsonArray = new JSONArray(response);
	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject json = jsonArray.getJSONObject(i);
	            Maintenance maintenance = new Maintenance(json);
	            maintenances.add(maintenance); 
	        }
	        
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maintenances;
    }
}
