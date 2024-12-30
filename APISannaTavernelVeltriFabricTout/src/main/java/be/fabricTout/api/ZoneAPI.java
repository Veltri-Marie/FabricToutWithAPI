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
import be.fabricTout.dao.WorkerDAO;
import be.fabricTout.dao.ZoneDAO;
import be.fabricTout.javabeans.Zone;

@Path("/zone")
public class ZoneAPI {

    private Connection connection;
    private ZoneDAO zoneDAO;

    public ZoneAPI(@Context ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext is null. Unable to initialize database connection.");
        }
        this.connection = FabricToutConnection.getInstance(context);
        zoneDAO = new ZoneDAO(connection);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String zoneJson) {
        try {
            JSONObject json = new JSONObject(zoneJson);
            Zone zone = new Zone(json);

            if (zone.create(zoneDAO)) {
                return Response
                        .status(Status.CREATED)
                        .header("Location", "/zone/" + zone.getZoneId())
                        .entity(new JSONObject().put("idZone", zone.getZoneId()).toString())
                        .build();
            } else {
                return Response
                        .status(Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to create zone")
                        .build();
            }
        } catch (JSONException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Invalid JSON format")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
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
    public Response delete(@PathParam("id") int id) {
        try {
            Zone zone = Zone.find(zoneDAO, id);

            if (zone != null && zone.delete(zoneDAO)) {
                return Response
                        .status(Status.NO_CONTENT)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Zone not found")
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
    public Response update(String zoneJson) {
        try {
            JSONObject json = new JSONObject(zoneJson);
            Zone zone = new Zone(json);

            if (zone.update(zoneDAO)) {
                return Response
                        .status(Status.OK)
                        .entity("Zone updated successfully")
                        .build();
            } else {
                return Response
                        .status(Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to update zone")
                        .build();
            }
        } catch (JSONException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Invalid JSON format")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
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
            Zone zone = Zone.find(zoneDAO, id);

            if (zone != null) {
            	
            	ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(zone); 
                System.out.println("Zone in JSON: " + json);
                
                return Response
                        .status(Status.OK)
                        .entity(zone)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Zone not found")
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
            List<Zone> zones = Zone.findAll(zoneDAO);

            if (zones != null && !zones.isEmpty()) {
            	
            	ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(zones);
                
                return Response
                        .status(Status.OK)
                        .entity(zones)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("No zones found")
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
