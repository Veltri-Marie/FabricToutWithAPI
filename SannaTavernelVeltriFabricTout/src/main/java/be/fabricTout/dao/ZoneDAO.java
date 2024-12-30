package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.fabricTout.javabeans.Zone;

public class ZoneDAO extends DAO<Zone> {

    public ZoneDAO(ServletContext context) {
        super(context);
    }

    @Override
    public boolean createDAO(Zone zone) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(zone);

            String response = getResource()
                    .path("zone")
                    .type(MediaType.APPLICATION_JSON)
                    .post(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Zone zone) {
        try {
            String response = getResource()
                    .path("zone/" + zone.getZoneId())
                    .accept(MediaType.APPLICATION_JSON)
                    .delete(String.class);

            return response != null && !response.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Zone zone) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(zone);

            String response = getResource()
                    .path("zone/" + zone.getZoneId())
                    .type(MediaType.APPLICATION_JSON)
                    .put(String.class, json);

            return response != null && !response.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Zone findDAO(int id) {
        Zone zone = null;
        try {
            String response = getResource()
                    .path("zone/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            zone = mapper.readValue(response, Zone.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zone;
    }

    @Override
    public List<Zone> findAllDAO() {
        List<Zone> zones = new ArrayList<>();

        try {
            String response = getResource()
                    .path("zone")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            zones = mapper.readValue(response, new TypeReference<List<Zone>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zones;
    }
}
