package be.fabricTout.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
        try {
            String response = getResource()
                    .path("machine/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);
            
            System.out.println(response);
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.readValue(response, Machine.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Machine> findAllDAO() {
        try {
            String response = getResource()
                    .path("machine")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);
            
            System.out.println("MachineDAO -> FindAll client :" + response);
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
          
            

            return mapper.readValue(response, new TypeReference<List<Machine>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
