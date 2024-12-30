package be.fabricTout.api;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import be.fabricTout.connection.FabricToutConnection;
import be.fabricTout.dao.SiteDAO;
import be.fabricTout.dao.WorkerDAO;
import be.fabricTout.javabeans.Worker;

@Path("/worker")
public class WorkerAPI {

    private Connection connection;
    private WorkerDAO workerDAO;

    public WorkerAPI(@Context ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext is null. Unable to initialize database connection.");
        }
        this.connection = FabricToutConnection.getInstance(context);
        workerDAO = new WorkerDAO(connection);
    }
    

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("id") int id) {
        try {
            Worker worker = Worker.find(workerDAO, id);

            if (worker != null) {
                return Response
                        .status(Status.OK)
                        .entity(worker)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Worker not found")
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
            List<Worker> workers = Worker.findAll(workerDAO);

            if (workers != null && !workers.isEmpty()) {
                return Response
                        .status(Status.OK)
                        .entity(workers)
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("No workers found")
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