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

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "zoneId")
@JsonIgnoreProperties(ignoreUnknown = true)

public class Zone implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int zoneId;
    private Letter letter;
    private Color color;
    @JsonBackReference
    private Site site;
    @JsonManagedReference
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
		setZoneId(json.optInt("zoneId", -1));
		setLetter(Letter.valueOf(json.getString("letter")));
		setColor(Color.valueOf(json.getString("color")));
      
		if (json.has("site")) {
			setSite(new Site(json.getJSONObject("site")));
			site.addZone(this);
		}
		else if (json.has("idSite") && json.has("name") && json.has("city")) {
			setSite(new Site(json.getInt("idSite"), json.getString("name"), json.getString("city")));
			site.addZone(this);			
		}
		System.out.println("Zone(JSONObject json) " + this);

		
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
