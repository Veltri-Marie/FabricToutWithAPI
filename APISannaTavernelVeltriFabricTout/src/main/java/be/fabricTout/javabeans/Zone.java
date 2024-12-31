package be.fabricTout.javabeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.ZoneDAO;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "KeyZone",
        scope = Zone.class
    )
@JsonIgnoreProperties(ignoreUnknown = true)

public class Zone implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int zoneId;
    private Letter letter;
    private Color color;
    private Site site;
    private List<Machine> machines;

    // CONSTRUCTORS
    public Zone() {
        if (machines == null) {
            machines = new ArrayList<>();
        }
    }
	
	public Zone(int zoneId, Letter letter, Color color) {
		this();
		setZoneId(zoneId);
		setLetter(letter);
		setColor(color);
        site.addZone(this);
    }
	
	public Zone(int zoneId, Letter letter, Color color, Site site) {
		this();
		setZoneId(zoneId);
		setLetter(letter);
		setColor(color);
		setSite(site);
        site.addZone(this);
    }
	
	public Zone(int zoneId, Letter letter, Color color, int idSite, String name, String city) {
		this();
		setZoneId(zoneId);
		setLetter(letter);
		setColor(color);
		setSite(new Site(idSite, name, city));
		site.addZone(this);
	}
	
	public Zone(JSONObject json) {
		this();
		if (json.has("zoneId")) {
			if (!json.optString("zoneId").isBlank())
				setZoneId(json.getInt("zoneId"));
		}
		if (json.has("letter")) {
			if (!json.optString("letter").isBlank())
				setLetter(Letter.valueOf(json.getString("letter")));
		}
		if (json.has("color")) {
			if (!json.optString("color").isBlank())
				setColor(Color.valueOf(json.getString("color")));
		}
		if (json.has("site")) {
		    Object siteElement = json.get("site");
		    
		    if (siteElement instanceof JSONObject) {
		        JSONObject siteJson = (JSONObject) siteElement;
		        Site site = new Site(siteJson); 
		        setSite(site); 
		        site.addZone(this); 
		    } 
		}	
		if (json.has("machines")) {
			List<Machine> machines = new ArrayList<>();
			if (json.getJSONArray("machines") != null) {
				for (int i = 0; i < json.getJSONArray("machines").length(); i++) {
					Object machineElement = json.getJSONArray("machines").get(i);
					if (machineElement instanceof JSONObject)
						machines.add(new Machine((JSONObject) machineElement));	
				}
				setMachines(machines);
			}
		}
	}

    // PROPERTIES
    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public Letter getLetter() {
        return letter;
    }

    public void setLetter(Letter letter) {
        this.letter = letter;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }
    
	public Site getSite() {
		return site;
	}
	
	public void setSite(Site site) {
		this.site = site;
	}

    // METHODS
    public boolean create(ZoneDAO zoneDAO) {
        return zoneDAO.createDAO(this);
    }

    public boolean delete(ZoneDAO zoneDAO) {
        return zoneDAO.deleteDAO(this);
    }

    public boolean update(ZoneDAO zoneDAO) {
        return zoneDAO.updateDAO(this);
    }

    public static Zone find(ZoneDAO zoneDAO, int id) {
        return zoneDAO.findDAO(id);
    }

    public static List<Zone> findAll(ZoneDAO zoneDAO) {
        return zoneDAO.findAllDAO();
    }

    public void addMachine(Machine machine) {
        if (machines == null) {
            machines = new ArrayList<>();
        }
        machines.add(machine);
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zoneId=" + zoneId +
                ", letter=" + letter +
                ", color=" + color +
                ", site=" + site +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return zoneId == zone.zoneId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(zoneId);
    }
}
