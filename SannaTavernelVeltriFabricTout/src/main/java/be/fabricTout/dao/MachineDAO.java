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

import be.fabricTout.javabeans.Machine;

public class MachineDAO extends DAO<Machine> {

    public MachineDAO(ServletContext context) {
        super(context);
    }

    @Override
    public boolean createDAO(Machine machine) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(machine);

            String response = getResource()
                    .path("machine")
                    .type(MediaType.APPLICATION_JSON)
                    .post(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Machine machine) {
        try {
            String response = getResource()
                    .path("machine/" + machine.getIdMachine())
                    .accept(MediaType.APPLICATION_JSON)
                    .delete(String.class);

            return response != null && !response.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Machine machine) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(machine);

            String response = getResource()
                    .path("machine/" + machine.getIdMachine())
                    .type(MediaType.APPLICATION_JSON)
                    .put(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Machine findDAO(int id) {
    	Machine machine = null;
        try {
            String response = getResource()
                    .path("machine/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);
           
            JSONObject json = new JSONObject (response);
                        
            machine = new Machine(json);
            
        } catch (Exception e) {
        	System.err.println("Unexpected error in findDAO: " + e.getMessage());
	        e.printStackTrace();
        }
        return machine;
    }

    @Override
    public List<Machine> findAllDAO() {
    	List<Machine> machines = new ArrayList<>();
    	try {
	        String response = getResource()
	                .path("machine")
	                .accept(MediaType.APPLICATION_JSON)
	                .get(String.class);

	        if (response == null || response.isEmpty()) {
	            return machines;
	        }

	        JSONArray jsonArray = new JSONArray(response);
	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject json = jsonArray.getJSONObject(i);
	            Machine machine = new Machine(json);
	            machines.add(machine); 
	        }

	    } catch (Exception e) {
	        System.err.println("Unexpected error in findAllDAO: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return machines;
    }
}
