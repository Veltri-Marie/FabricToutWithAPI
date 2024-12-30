package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.MachineDAO;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idMachine")
public class Machine implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int idMachine;
    private Type type;
    private double size;
    private State state;
    private List<Maintenance> maintenances;
    private List<Zone> zones;

    // CONSTRUCTORS
    public Machine() {
        if (maintenances == null) {
            maintenances = new ArrayList<>();
        }
        if (zones == null) {
            zones = new ArrayList<>();
        }
    }

    public Machine(int idMachine, Type type, double size, State state, List<Zone> zones) {
        this();  
        setIdMachine(idMachine);
        setType(type);
        setSize(size);
        setState(state);	
        setZones(zones);
		for (Zone zone : zones) {
			zone.addMachine(this);
		}
    }
    
    public Machine(JSONObject json) {
		this();
		setIdMachine(json.optInt("idMachine", -1));
		setType(Type.valueOf(json.getString("type")));
		setSize(json.getDouble("size"));
		setState(State.valueOf(json.getString("state")));
		JSONArray zonesArray = json.getJSONArray("zones");
		List<Zone> zones = new ArrayList<>();
		for (int i = 0; i < zonesArray.length(); i++) {
			zones.add(new Zone(zonesArray.getJSONObject(i)));
		}
		setZones(zones);
		
		JSONArray maintenancesArray = json.getJSONArray("maintenances");
		List<Maintenance> maintenances = new ArrayList<>();
		for (int i = 0; i < maintenancesArray.length(); i++) {
			maintenances.add(new Maintenance(maintenancesArray.getJSONObject(i)));
		}
		setMaintenances(maintenances);
		
		System.out.println("Machine (JSONObject json): " + json);
    }
    
	public Machine(Type type, double size, State state, List<Zone> zones) {
		this(-1, type, size, state, zones);
	}

    // PROPERTIES
    public int getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(int idMachine) {
    	String idString = String.valueOf(idMachine); 
	    if (!idString.matches("-?\\d+")) { 
	        throw new IllegalArgumentException("idMachine must be a valid integer.");
	    }
        this.idMachine = idMachine;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
    	if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
    	}  	
        this.type = type;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
    	if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");
    	}
    	if (size > 100) {
            throw new IllegalArgumentException("size must be less than 100");
    	}
    	
        this.size = size;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
    	if (state == null) {
            throw new IllegalArgumentException("state cannot be null");
    	}
        this.state = state;
    }

    public List<Maintenance> getMaintenances() {
        return maintenances != null ? maintenances : new ArrayList<>();
    }

    public void setMaintenances(List<Maintenance> maintenances) {
    	if (maintenances == null) {
            //throw new IllegalArgumentException("maintenances cannot be null");
    		maintenances = new ArrayList<>();
    	}
    	if (maintenances.isEmpty()) {
            //throw new IllegalArgumentException("maintenances cannot be empty");
    		maintenances = new ArrayList<>();
    	}
        this.maintenances = maintenances;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    // METHODS
    public boolean create(MachineDAO machineDAO) {
        return machineDAO.createDAO(this);
    }


    public boolean delete(MachineDAO machineDAO) {
        return machineDAO.deleteDAO(this);
    }

    public boolean update(MachineDAO machineDAO) {
        return machineDAO.updateDAO(this);
    }

    public static Machine find(MachineDAO machineDAO, int id) {
        return machineDAO.findDAO(id);
    }

    public static List<Machine> findAll(MachineDAO machineDAO) {
        return machineDAO.findAllDAO();
    }

    public void addMaintenance(Maintenance maintenance) {
        if (maintenances == null) {
            maintenances = new ArrayList<>();
        }
        if (!maintenances.contains(maintenance)) {
            maintenances.add(maintenance);
        }
    }

    public void addZone(Zone zone) {
        if (zones == null) {
            zones = new ArrayList<>();
        }
        if (!zones.contains(zone)) {
            zones.add(zone);
            zone.addMachine(this);
        }
    }

    @Override
    public String toString() {
        return "Machine{" +
                "idMachine=" + idMachine +
                ", type=" + type +
                ", size=" + size +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;
        return idMachine == machine.idMachine &&
               Double.compare(machine.size, size) == 0 &&
               type == machine.type &&
               state == machine.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMachine, type, size, state);
    }
}
