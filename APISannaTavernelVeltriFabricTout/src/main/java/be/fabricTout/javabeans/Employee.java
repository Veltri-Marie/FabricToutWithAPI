package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.EmployeeDAO;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Employee extends Person implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private String registrationCode;
    private String password;

    // CONSTRUCTORS
    public Employee() {
        super();
    }

    public Employee(int idPerson, String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
                   String registrationCode, String password) {
        super(idPerson, firstName, lastName, birthDate, phoneNumber);
        setRegistrationCode(registrationCode);
        setPassword(password);
    }
    
    public Employee(String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            String registrationCode, String password) {
    	this(-1, firstName, lastName, birthDate, phoneNumber, registrationCode, password);
    }
    
    public Employee(JSONObject json) {
        super(json);

        if (json.has("registrationCode")) {
            setRegistrationCode(json.optString("registrationCode"));
        }
        if (json.has("password")) {
            setPassword(json.optString("password"));
        }

        System.out.println("Employee(JSONObject json): " + json);
    }


    // PROPERTIES
    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
    	if (registrationCode == null) {
    		throw new IllegalArgumentException("Registration code cannot be null");
    	}
		if (registrationCode.isBlank()) {
			throw new IllegalArgumentException("Registration code cannot be empty");
		}
    	if(registrationCode.length() < 5) {
    		throw new IllegalArgumentException("Registration code must be at least 5 characters long");
    	}
        this.registrationCode = registrationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
    	if (password == null) {
    		throw new IllegalArgumentException("Password cannot be null");
    	}
    	if (password.isBlank()) {
    		throw new IllegalArgumentException("Password cannot be empty");
    	}
        this.password = password;
    }

    // METHODS
    public static int authenticate(EmployeeDAO employeeDAO, String registrationCode, String password) {
        return employeeDAO.authenticateDAO(registrationCode, password);
    }
    
    
	public static String findTypeById(EmployeeDAO employeeDAO, int id) {
		return employeeDAO.findTypeByIdDAO(id);
	}
    
    @Override
    public String toString() {
        return "Employee{" +
                "registrationCode='" + registrationCode + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(registrationCode, employee.registrationCode) &&
               Objects.equals(password, employee.password) &&
               super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationCode, password, super.hashCode());
    }

}
