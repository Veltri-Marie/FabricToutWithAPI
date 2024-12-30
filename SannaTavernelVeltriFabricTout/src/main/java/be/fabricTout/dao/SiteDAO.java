package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.fabricTout.javabeans.Site;

public class SiteDAO extends DAO<Site> {

    public SiteDAO(ServletContext context) {
        super(context);
    }

    @Override
    public boolean createDAO(Site site) {
        try {
            ObjectMapper mapper = new ObjectMapper();
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

            ObjectMapper mapper = new ObjectMapper();
            site = mapper.readValue(response, Site.class);
        } catch (IOException e) {
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

            ObjectMapper mapper = new ObjectMapper();
            sites = mapper.readValue(response, new TypeReference<List<Site>>() {});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sites;
    }
}
