package be.fabricTout.dao;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import be.fabricTout.javabeans.Employee;

public class EmployeeDAO extends DAO<Employee> {

    public EmployeeDAO(ServletContext context) {
        super(context);
    }

    public int authenticateDAO(String registrationCode, String password) {
    	
        try {
            if (registrationCode == null || registrationCode.isEmpty() || password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Registration code and password cannot be null or empty.");
            }

            String payload = String.format("{\"registrationCode\":\"%s\", \"password\":\"%s\"}", registrationCode, password);

            String response = getResource()
                    .path("employee/authenticate")
                    .type(MediaType.APPLICATION_JSON)
                    .post(String.class, payload);

            JSONObject json = new JSONObject(response);
            System.out.println("json : " + json);
            if (json.has("idEmployee")) {
                return json.getInt("idEmployee");
            }
        } catch (JSONException e) {
            System.err.println("Invalid JSON format in response: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; 
    }

    public String findTypeByIdDAO(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("ID must be a positive integer.");
            }

            String response = getResource()
                    .path("employee/type/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);

            JSONObject json = new JSONObject(response);
            if (json.has("employeeType")) {
                return json.getString("employeeType");
            }
        } catch (JSONException e) {
            System.err.println("Invalid JSON format in response: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }


	@Override
	public boolean createDAO(Employee obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDAO(Employee obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDAO(Employee obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Employee findDAO(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Employee> findAllDAO() {
		// TODO Auto-generated method stub
		return null;
	}
}
