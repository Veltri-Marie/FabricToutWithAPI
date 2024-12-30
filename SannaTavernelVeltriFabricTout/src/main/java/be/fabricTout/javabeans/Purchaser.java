package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.PurchaserDAO;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idPerson")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Purchaser extends Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    // CONSTRUCTORS
    public Purchaser() {
        super();
    }

    public Purchaser(int idPerson, String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            String registrationCode, String password) {
        super(idPerson, firstName, lastName, birthDate, phoneNumber, registrationCode, password);
    }
    
	public Purchaser(String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            String registrationCode, String password) {
		this(-1, firstName, lastName, birthDate, phoneNumber, registrationCode, password);
	}
	
	public Purchaser(JSONObject json) {
		super(json);
		System.out.println("Purchaser (JSONObject json): " + json);

	}
    
    // METHODS
    public static Purchaser find(PurchaserDAO purchaserDAO, int id) {
        return purchaserDAO.findDAO(id);
    }
    public static List<Purchaser> findAll(PurchaserDAO purchaserDAO) {
        return purchaserDAO.findAllDAO();
    }
    
    @Override
    public String toString() {
        return "Purchaser{" +
                super.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o); 
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
