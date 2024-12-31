package be.fabricTout.api;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import be.fabricTout.connection.FabricToutConnection;
import be.fabricTout.dao.MachineDAO;
import be.fabricTout.dao.MaintenanceDAO;
import be.fabricTout.javabeans.Maintenance;

@Path("/maintenance")
public class MaintenanceAPI {
    private Connection connection;
    private MaintenanceDAO maintenanceDAO;
    
    public MaintenanceAPI(@Context ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext is null. Unable to initialize database connection.");
        }
        this.connection = FabricToutConnection.getInstance(context);
        this.maintenanceDAO = new MaintenanceDAO(connection);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(String maintenanceJson) {
        try {
            JSONObject json = new JSONObject(maintenanceJson);
            Maintenance maintenance = new Maintenance(json);

            if (maintenance.create(maintenanceDAO)) {
                return Response
                        .status(Status.CREATED)
                        .header("Location", "/maintenance/" + maintenance.getIdMaintenance())
                        .entity(new JSONObject().put("idMaintenance", maintenance.getIdMaintenance()).toString())
                        .build();
            } else {
                return Response
                        .status(Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to create maintenance")
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
            Maintenance maintenance = Maintenance.find(maintenanceDAO, id);

            if (maintenance != null && maintenance.delete(maintenanceDAO)) {
                return Response
                        .status(Status.NO_CONTENT)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Maintenance not found")
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
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, String maintenanceJson) {
        try {
            JSONObject json = new JSONObject(maintenanceJson);
            System.out.println("json: " + json);
            Maintenance maintenance = new Maintenance(json);

            if (maintenance.update(maintenanceDAO)) {
                return Response
                        .status(Status.OK)
                        .entity("Maintenance updated successfully")
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Maintenance not found")
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
            Maintenance maintenance = Maintenance.find(maintenanceDAO, id);
           
            if (maintenance != null) {
            	ObjectMapper mapper = new ObjectMapper();
            	mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.registerModule(new JavaTimeModule());

                String json = mapper.writeValueAsString(maintenance);
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Maintenance not found")
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
            List<Maintenance> maintenances = Maintenance.findAll(maintenanceDAO);

            if (maintenances != null && !maintenances.isEmpty()) {
            	ObjectMapper mapper = new ObjectMapper();
            	mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.registerModule(new JavaTimeModule());

                String json = mapper.writeValueAsString(maintenances);
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("No maintenances found")
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
}
