package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.fabricTout.javabeans.Purchaser;
import be.fabricTout.javabeans.Worker;

public class WorkerDAO extends DAO<Worker>{

	public WorkerDAO(ServletContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean createDAO(Worker obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDAO(Worker obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDAO(Worker obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	    public Worker findDAO(int id) {
	        Worker worker = null;
	        try {
	            String response = getResource()
	                    .path("worker/" + id)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .get(String.class);

	            JSONObject json = new JSONObject(response);
	            System.out.println("Worker findDAO Raw JSON Response: " + json);
	            
	            worker = new Worker(json);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return worker;
	    }

	    @Override
	    public List<Worker> findAllDAO() {
	        List<Worker> workers = new ArrayList<>();

	        try {
	            String response = getResource()
	                    .path("worker")
	                    .accept(MediaType.APPLICATION_JSON)
	                    .get(String.class);

	            JSONArray jsonArray = new JSONArray(response);
		        for (int i = 0; i < jsonArray.length(); i++) {
		            JSONObject json = jsonArray.getJSONObject(i);
		            Worker worker = new Worker(json);
		            workers.add(worker); 
		        }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return workers;
	    }
	
}