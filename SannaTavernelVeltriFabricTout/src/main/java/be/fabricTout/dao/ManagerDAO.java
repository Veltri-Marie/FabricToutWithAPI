package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import be.fabricTout.javabeans.Manager;

public class ManagerDAO extends DAO<Manager>{

	public ManagerDAO(ServletContext context) {
        super(context);
    }

	@Override
	public boolean createDAO(Manager obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDAO(Manager obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDAO(Manager obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Manager findDAO(int id) {
		System.out.println("Client ManagerDAO : findDAO");
	    Manager manager = null;
	    try {

	        String response = getResource()
	                .path("manager/" + id)
	                .accept(MediaType.APPLICATION_JSON)
	                .get(String.class);
	        
	        System.out.println("Response from API: " + response);


	        ObjectMapper mapper = new ObjectMapper();
	        mapper.registerModule(new JavaTimeModule());
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	        manager = mapper.readValue(response, Manager.class);

	        System.out.println("Deserialization Successful: " + manager);
	    } catch (IOException e) {
	        System.err.println("Error deserializing Manager object: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	        System.err.println("Unexpected error in findDAO: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return manager;
	}



    @Override
    public List<Manager> findAllDAO() {
        List<Manager> managers = new ArrayList<>();

        try {
            String response = getResource()
                    .path("manager")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);
            System.out.println("Manager findAllDAO Raw JSON Response: " + response);


            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            managers = mapper.readValue(response, new TypeReference<List<Manager>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return managers;
    }
   
}