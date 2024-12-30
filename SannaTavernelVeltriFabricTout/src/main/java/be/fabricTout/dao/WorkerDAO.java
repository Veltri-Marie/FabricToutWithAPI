package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	            ObjectMapper mapper = new ObjectMapper();
	            worker = mapper.readValue(response, Worker.class);
	        } catch (IOException e) {
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

	            ObjectMapper mapper = new ObjectMapper();
	            workers = mapper.readValue(response, new TypeReference<List<Worker>>() {});

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return workers;
	    }
	
}