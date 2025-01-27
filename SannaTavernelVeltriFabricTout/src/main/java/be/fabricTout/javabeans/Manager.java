package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.ManagerDAO;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "KeyManager",
        scope = Manager.class
    )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Manager extends Employee implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private List<Maintenance> maintenances;
    private Site site;

    // CONSTRUCTORS
    public Manager() {
        super();
		if (maintenances == null) {
			maintenances = new ArrayList<>();
		}
    }

    public Manager(int idPerson, String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            String registrationCode, String password, Site site) {
        super(idPerson, firstName, lastName, birthDate, phoneNumber, registrationCode, password);
		if (maintenances == null) {
			maintenances = new ArrayList<>();
		}
		setSite(site);
    }
    
	public Manager(String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            String registrationCode, String password, Site site) {
		this(-1, firstName, lastName, birthDate, phoneNumber, registrationCode, password, site);
	}
	
	public Manager(JSONObject json) {
	    super(json);

	    if (json.has("site")) {
	        Object siteObject = json.get("site");

	        if (siteObject instanceof JSONObject) {
	            setSite(new Site((JSONObject) siteObject));
	        }
	    }

	    if (json.has("maintenances")) {
	        JSONArray maintenancesArray = json.optJSONArray("maintenances");
	        if (maintenancesArray != null) {
	            List<Maintenance> maintenances = new ArrayList<>();
	            for (int i = 0; i < maintenancesArray.length(); i++) {
	            	Object maintenanceObject = maintenancesArray.get(i);
	            	if (maintenanceObject instanceof JSONObject) {
	            		maintenances.add(new Maintenance((JSONObject) maintenanceObject));
	            	}
	            }
	            setMaintenances(maintenances);
	        } else {
	            setMaintenances(new ArrayList<>());
	        }
	    }
	}

	
    
   
    // PROPERTIES
    public List<Maintenance> getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(List<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }
    
	public Site getSite() {
		return site;
	}
	
	public void setSite(Site site) {
		if (site == null) {
			throw new IllegalArgumentException("Site cannot be null");
		}
		this.site = site;
	}

	 // METHODS
    public static Manager find(ManagerDAO managerDAO, int id) {
        return managerDAO.findDAO(id);
    }
    public static List<Manager> findAll(ManagerDAO managerDAO) {
        return managerDAO.findAllDAO();
    }
    
    public void addMaintenance(Maintenance maintenance) {
        if (maintenances == null) {
            maintenances = new ArrayList<>();
        }
        if (maintenance != null && !maintenances.contains(maintenance)) {
            maintenances.add(maintenance);
        }
    }

    @Override
    public String toString() {
        return "Manager{" +
                "maintenances=" + maintenances +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return maintenances.equals(manager.maintenances) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return maintenances.hashCode() + super.hashCode();
    }
}
