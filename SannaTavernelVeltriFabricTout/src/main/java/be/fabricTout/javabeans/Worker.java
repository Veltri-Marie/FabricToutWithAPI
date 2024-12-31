package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.WorkerDAO;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "KeyWorker",
        scope = Worker.class
    )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Worker extends Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private List<Maintenance> maintenances;
    private Site site;

    // CONSTRUCTORS
    public Worker() {
        super(); 
		if (maintenances == null) {
			maintenances = new ArrayList<>();
		}
    }

    public Worker(int idPerson, String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            String registrationCode, String password, Site site) {
        super(idPerson, firstName, lastName, birthDate, phoneNumber, registrationCode, password);
		if (maintenances == null) {
			maintenances = new ArrayList<>();
		}
		setSite(site);
    }
    
	public Worker(String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            String registrationCode, String password, Site site) {
		this(-1, firstName, lastName, birthDate, phoneNumber, registrationCode, password, site);
	}
	
	public Worker(JSONObject json) {
		super(json);
		if (json.has("site")) {
	        setSite(new Site(json.getJSONObject("site")));
	    }
	    if (json.has("maintenances")) {
	        JSONArray maintenancesArray = json.optJSONArray("maintenances");
	        if (maintenancesArray != null) {
	            List<Maintenance> maintenances = new ArrayList<>();
	            for (int i = 0; i < maintenancesArray.length(); i++) {
	            	Object object = maintenancesArray.get(i);
	            	if (object instanceof JSONObject)
	            		maintenances.add(new Maintenance((JSONObject) object));
	   	            }
	            setMaintenances(maintenances);
	        } else {
	            setMaintenances(new ArrayList<>());
	        }
	    }
	}
	
    // METHODS	
    public static Worker find(WorkerDAO workerDAO, int id) {
        return workerDAO.findDAO(id);
    }
    public static List<Worker> findAll(WorkerDAO workerDAO) {
        return workerDAO.findAllDAO();
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
            throw new IllegalArgumentException("site cannot be null");
    	}
        this.site = site;
    }

    // METHODS
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
        return "Worker{" +
                "maintenances=" + maintenances +
                ", site=" + site +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return maintenances.equals(worker.maintenances) && site.equals(worker.site) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return maintenances.hashCode() + site.hashCode() + super.hashCode();
    }
}
