package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

            ObjectMapper mapper = new ObjectMapper();
            purchaser = mapper.readValue(response, Purchaser.class);
        } catch (IOException e) {
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

            ObjectMapper mapper = new ObjectMapper();
            purchasers = mapper.readValue(response, new TypeReference<List<Purchaser>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return purchasers;
    }
	

}
