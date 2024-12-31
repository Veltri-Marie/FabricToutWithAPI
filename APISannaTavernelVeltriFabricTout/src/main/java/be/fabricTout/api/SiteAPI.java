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
import be.fabricTout.dao.SiteDAO;
import be.fabricTout.javabeans.Site;

@Path("/site")
public class SiteAPI {
    private Connection connection;
    private SiteDAO siteDAO;

    public SiteAPI(@Context ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext is null. Unable to initialize database connection.");
        }
        this.connection = FabricToutConnection.getInstance(context);
        this.siteDAO = new SiteDAO(connection);
    }
    

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String siteJson) {
        try {
            JSONObject json = new JSONObject(siteJson);
            Site site = new Site(json);

            if (site.create(siteDAO)) {
                return Response
                        .status(Status.CREATED)
                        .header("Location", "/site/" + site.getIdSite())
                        .entity(new JSONObject().put("idSite", site.getIdSite()).toString())
                        .build();
            } else {
                return Response
                        .status(Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to create site")
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
            Site site = Site.find(siteDAO, id);

            if (site != null && site.delete(siteDAO)) {
                return Response
                        .status(Status.NO_CONTENT)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Site not found")
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
    public Response update(@PathParam("id") int id, String siteJson) {
        try {
            JSONObject json = new JSONObject(siteJson);
            Site site = new Site(json);

            if (site.update(siteDAO)) {
                return Response
                        .status(Status.OK)
                        .entity("Site updated successfully")
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Site not found")
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
            Site site = Site.find(siteDAO, id);

            if (site != null) {
            	
            	ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(site); 
                
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Site not found")
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
            List<Site> sites = Site.findAll(siteDAO);

            if (sites != null && !sites.isEmpty()) {
            	
            	ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(sites);
            	
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("No sites found")
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
