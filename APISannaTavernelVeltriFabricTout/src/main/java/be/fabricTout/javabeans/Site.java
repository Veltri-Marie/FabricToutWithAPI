package be.fabricTout.javabeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.SiteDAO;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "KeySite",
        scope = Site.class
    )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Site implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int idSite;
    private String name;
    private String city;
    private List<Zone> zones;
    private List<Worker> workers;
    private Manager manager;

    // CONSTRUCTORS
    public Site() {
        if (zones == null) {
            zones = new ArrayList<>();
        }

		if (workers == null) {
			workers = new ArrayList<>();
		}
    }

    public Site(int idSite, String name, String city) {
    	this();
    	setIdSite(idSite);
        setName(name);
        setCity(city);		
	}
    
	public Site(String name, String city) {
		this(-1, name, city);
	}
    
    public Site(int idSite, String name, String city, List<Zone> zones) {
    	this(idSite, name, city);
    	setZones(zones);
    }
    
    public Site(String name, String city, List<Zone> zones) {
		this(-1, name, city);
		
	}
    
	public Site(JSONObject json) {
		this();
		if (json.has("idSite")) {
			if (!json.optString("idSite").isBlank())
				setIdSite(json.getInt("idSite"));
		}
		if (json.has("name")) {
			if (!json.optString("name").isBlank())
				setName(json.getString("name"));
		}
		if(json.has("city")) {
			if (!json.optString("city").isBlank())
				setCity(json.getString("city"));
		}
		if (json.has("zones")) {
		    JSONArray zonesArray = json.getJSONArray("zones");
		    if (zonesArray != null) {
		        List<Zone> zones = new ArrayList<>();
		        for (int i = 0; i < zonesArray.length(); i++) {
		            Object zoneElement = zonesArray.get(i);
		            if (zoneElement instanceof JSONObject) {
		                zones.add(new Zone((JSONObject) zoneElement));
		            }
		        }
		        setZones(zones);
		    }
		}

		if(json.has("workers")) {
            JSONArray workersArray = json.getJSONArray("workers");
            List<Worker> workers = new ArrayList<>();
            if (workersArray != null) {
	            for (int i = 0; i < workersArray.length(); i++) {
	            	Object workerElement = workersArray.get(i);
	            	if (workerElement instanceof JSONObject) {
                    		workers.add(new Worker((JSONObject) workerElement));
	            	}
	            }
	            setWorkers(workers);
            }
		}
		if (json.has("manager")) {
			Object managerObject = json.get("manager");
			if (managerObject instanceof JSONObject) {
				setManager(new Manager((JSONObject) managerObject));
			}
		}
	}
	

    // PROPERTIES
    public int getIdSite() {
        return idSite;
    }

    public void setIdSite(int idSite) {
    	String idString = String.valueOf(idSite); 
	    if (!idString.matches("-?\\d+")) { 
	        throw new IllegalArgumentException("idSite must be a valid integer.");
	    }
        this.idSite = idSite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {		
        this.city = city;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        if (zones == null)
        	throw new IllegalArgumentException("zones cannot be null");
        this.zones = zones;
    }


    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
    
    public List<Worker> getWorkers() {
		return workers;
    }
    
	public void setWorkers(List<Worker> workers) {
        this.workers = workers;
	}
    	        		
    // METHODS
    public boolean create(SiteDAO siteDAO) {
        return siteDAO.createDAO(this);
    }

    public boolean delete(SiteDAO siteDAO) {
        return siteDAO.deleteDAO(this);
    }

    public boolean update(SiteDAO siteDAO) {
        return siteDAO.updateDAO(this);
    }

    public static Site find(SiteDAO siteDAO, int id) {
        return siteDAO.findDAO(id);
    }

    public static List<Site> findAll(SiteDAO siteDAO) {
        return siteDAO.findAllDAO();
    }

    public void addZone(Zone zone) {
        if (this.zones == null) {
            this.zones = new ArrayList<>();
        }
        if (zone != null && !this.zones.contains(zone)) {
            this.zones.add(zone);
        }
    }

    
    public void addWorker(Worker worker) {
		if (this.workers == null) {
			this.workers = new ArrayList<>();
		}
		if (worker != null && !this.workers.contains(worker)) {
			this.workers.add(worker);
		}
    }
       

    @Override
    public String toString() {
        return "Site{" +
                "idSite=" + idSite +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", zones.size=" + (zones != null ? zones.size() : 0) + 
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return idSite == site.idSite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSite);
    }
}
