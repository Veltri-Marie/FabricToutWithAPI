package be.fabricTout.api;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import be.fabricTout.connection.FabricToutConnection;
import be.fabricTout.dao.EmployeeDAO;
import be.fabricTout.dao.MachineDAO;
import be.fabricTout.javabeans.Machine;

@Path("/machine")
public class MachineAPI {

    private Connection connection;
    private MachineDAO machineDAO;
    
    public MachineAPI(@Context ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext is null. Unable to initialize database connection.");
        }
        this.connection = FabricToutConnection.getInstance(context);
        this.machineDAO = new MachineDAO(connection);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(String machineJson) {
        try {
            JSONObject json = new JSONObject(machineJson);
            Machine machine = new Machine(json);

            if (machine.create(machineDAO)) {
                return Response
                        .status(Status.CREATED)
                        .entity(new JSONObject().put("idMachine", machine.getIdMachine()).toString())
                        .build();
            } else {
                return Response
                        .status(Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to create machine")
                        .build();
            }
        } catch (JSONException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Invalid JSON format: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred")
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        try {
            Machine machine = Machine.find(machineDAO, id);

            if (machine != null && machine.delete(machineDAO)) {
                return Response
                        .status(Status.NO_CONTENT)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Machine not found")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred")
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String machineJson) {
        try {
            JSONObject json = new JSONObject(machineJson);
            Machine machine = new Machine(json);

            if (machine.update(machineDAO)) {
                return Response
                        .status(Status.OK)
                        .entity(new JSONObject().put("message", "Machine updated successfully").toString())
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Machine not found")
                        .build();
            }
        } catch (JSONException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Invalid JSON format: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred")
                    .build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("id") int id) {
        try {
            Machine machine = Machine.find(machineDAO, id);

            if (machine != null) {
            	ObjectMapper mapper = new ObjectMapper();
            	mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.registerModule(new JavaTimeModule());

                String json = mapper.writeValueAsString(machine);
                System.out.println("MachineAPI.find()Serialization: \n" + json);
                
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Machine not found")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
        	System.out.println("API: MachineAPI.findAll()");
            List<Machine> machines = Machine.findAll(machineDAO);

            if (machines != null && !machines.isEmpty()) {
            	ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(machines);
                System.out.println("JSON de machineAPI :" + json);
                System.out.println("Status.OK");
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
            	System.out.println("Status NOT FOUND");
            	return Response
                        .status(Status.NOT_FOUND)
                        .entity("No machines found")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Status Exception");
            
            return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred")
                    .build();
        }
    }
}
