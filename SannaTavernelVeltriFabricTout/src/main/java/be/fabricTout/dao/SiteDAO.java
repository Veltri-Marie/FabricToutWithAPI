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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import be.fabricTout.javabeans.Manager;
import be.fabricTout.javabeans.Site;

public class SiteDAO extends DAO<Site> {

    public SiteDAO(ServletContext context) {
        super(context);
    }

    @Override
    public boolean createDAO(Site site) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(site);

            String response = getResource()
                    .path("site")
                    .type(MediaType.APPLICATION_JSON)
                    .post(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Site site) {
        try {
            String response = getResource()
                    .path("site/" + site.getIdSite())
                    .accept(MediaType.APPLICATION_JSON)
                    .delete(String.class);

            return response != null && !response.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Site site) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(site);

            String response = getResource()
                    .path("site/" + site.getIdSite())
                    .type(MediaType.APPLICATION_JSON)
                    .put(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Site findDAO(int id) {
        Site site = null;
        try {
            String response = getResource()
                    .path("site/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            JSONObject json = new JSONObject(response);
            System.out.println("Site findDAO Raw JSON Response: " + json);
            
	        site = new Site(json);

	        System.out.println("Deserialization Successful: " + site);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return site;
    }

    @Override
    public List<Site> findAllDAO() {
        List<Site> sites = new ArrayList<>();

        try {
            String response = getResource()
                    .path("site")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            System.out.println("Site findAllDAO Raw JSON Response: " + response);

	        if (response == null || response.isEmpty()) {
	            return sites;
	        }

	        JSONArray jsonArray = new JSONArray(response);
	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject json = jsonArray.getJSONObject(i);
	            Site site = new Site(json);
	            sites.add(site); 
	        }

	        System.out.println("Managers successfully deserialized: " + sites);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sites;
    }
}
