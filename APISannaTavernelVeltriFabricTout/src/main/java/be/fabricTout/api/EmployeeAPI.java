package be.fabricTout.api;

import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import be.fabricTout.connection.FabricToutConnection;
import be.fabricTout.dao.EmployeeDAO;
import be.fabricTout.javabeans.Employee;

@Path("/employee")
public class EmployeeAPI {
    private Connection connection;
    private EmployeeDAO employeeDAO;

    public EmployeeAPI(@Context ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext is null. Unable to initialize database connection.");
        }
        this.connection = FabricToutConnection.getInstance(context);
        this.employeeDAO = new EmployeeDAO(connection);
    }

    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(String employeeJson) {
        try {
            JSONObject json = new JSONObject(employeeJson);

            if (!json.has("registrationCode") || !json.has("password")) {
                return Response
                        .status(Status.BAD_REQUEST)
                        .entity("Missing required fields: registrationCode or password")
                        .build();
            }

            String registrationCode = json.getString("registrationCode");
            String password = json.getString("password");

            int id = Employee.authenticate(employeeDAO, registrationCode, password);

            if (id > 0) {
                return Response
                        .status(Status.OK)
                        .entity(new JSONObject().put("idEmployee", id).toString())
                        .build();
            } else {
                return Response
                        .status(Status.UNAUTHORIZED)
                        .entity("Invalid registration code or password")
                        .build();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Invalid JSON format")
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
    @Path("/type/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByTypeId(@PathParam("id") int id) {
        try {
            String employeeType = Employee.findTypeById(employeeDAO, id);

            if (employeeType != null) {
                return Response
                        .status(Status.OK)
                        .entity(new JSONObject().put("employeeType", employeeType).toString())
                        .build();
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Employee type not found")
                        .build();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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
}
