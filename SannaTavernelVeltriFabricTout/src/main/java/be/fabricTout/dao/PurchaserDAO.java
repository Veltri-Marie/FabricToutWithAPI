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

import be.fabricTout.javabeans.Manager;
import be.fabricTout.javabeans.Purchaser;

public class PurchaserDAO extends DAO<Purchaser>{

	public PurchaserDAO(ServletContext context) {
		super(context);
	}

	@Override
	public boolean createDAO(Purchaser obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDAO(Purchaser obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDAO(Purchaser obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public Purchaser findDAO(int id) {
        Purchaser purchaser = null;
        try {
            String response = getResource()
                    .path("purchaser/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            JSONObject json = new JSONObject(response);
            System.out.println("Purchaser findDAO Raw JSON Response: " + json);
            
	        purchaser = new Purchaser(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return purchaser;
    }

    @Override
    public List<Purchaser> findAllDAO() {
        List<Purchaser> purchasers = new ArrayList<>();

        try {
            String response = getResource()
                    .path("purchaser")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            JSONArray jsonArray = new JSONArray(response);
	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject json = jsonArray.getJSONObject(i);
	            Purchaser purchaser = new Purchaser(json);
	            purchasers.add(purchaser); 
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return purchasers;
    }
	

}
