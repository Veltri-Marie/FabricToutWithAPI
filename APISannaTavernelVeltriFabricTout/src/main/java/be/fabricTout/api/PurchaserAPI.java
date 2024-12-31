package be.fabricTout.api;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import be.fabricTout.connection.FabricToutConnection;
import be.fabricTout.dao.PurchaserDAO;
import be.fabricTout.javabeans.Purchaser;

@Path("/purchaser")
public class PurchaserAPI {

    private Connection connection;
    private PurchaserDAO purchaserDAO;

    public PurchaserAPI(@Context ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext is null. Unable to initialize database connection.");
        }
        this.connection = FabricToutConnection.getInstance(context);
        purchaserDAO = new PurchaserDAO(connection);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("id") int id) {
        try {
            Purchaser purchaser = Purchaser.find(purchaserDAO, id);

            if (purchaser != null) {
            	ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(purchaser);
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Purchaser not found")
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
            List<Purchaser> purchasers = Purchaser.findAll(purchaserDAO);

            if (purchasers != null && !purchasers.isEmpty()) {
            	ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                String json = mapper.writeValueAsString(purchasers);
                return Response
                        .status(Status.OK)
                        .entity(json)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("No purchasers found")
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
