package be.fabricTout.dao;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;


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
	        
            JSONObject json = new JSONObject(response);
            System.out.println("Manager findDAO Raw JSON Response: " + json);
            
	        manager = new Manager(json);

	        System.out.println("Deserialization Successful: " + manager);
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

	        if (response == null || response.isEmpty()) {
	            return managers;
	        }

	        JSONArray jsonArray = new JSONArray(response);
	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject json = jsonArray.getJSONObject(i);
	            Manager manager = new Manager(json);
	            managers.add(manager); 
	        }

	        System.out.println("Managers successfully deserialized: " + managers);
	    } catch (Exception e) {
	        System.err.println("Unexpected error in findAllDAO: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return managers;
	}

   
}