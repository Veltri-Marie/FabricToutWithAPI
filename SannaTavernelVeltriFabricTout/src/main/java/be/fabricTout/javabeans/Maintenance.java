package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.MaintenanceDAO;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idMaintenance")
public class Maintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private int idMaintenance;
    private LocalDate date;
    private int duration;
    private String report;
    private Status status;
    private Machine machine;
    private List<Worker> workers;
    private Manager manager;

    // CONSTRUCTORS
    public Maintenance() {
        if (workers == null) {
            workers = new ArrayList<>();
        }
    }

    public Maintenance(int idMaintenance, LocalDate date, int duration, String report, Status status, 
                       Machine machine, Manager manager, List<Worker> workers) {
        this();
        setIdMaintenance(idMaintenance);
        setDate(date);
        setDuration(duration);
        setReport(report);
        setStatus(status);
        setMachine(machine);
        setManager(manager);
        setWorkers(workers);

        for (Worker worker : workers) {
            worker.addMaintenance(this);
        }
        machine.addMaintenance(this);
        manager.addMaintenance(this);
        
    }
    
	public Maintenance(LocalDate date, int duration, String report, Status status, Machine machine, Manager manager, List<Worker> workers) {
		this(-1, date, duration, report, status, machine, manager, workers);
	}
	
	public Maintenance(JSONObject json) {
		this();
		setIdMaintenance(json.optInt("idMaintenance", -1));
		setDate(LocalDate.parse(json.getString("date")));
		setDuration(json.getInt("duration"));
		setReport(json.getString("report"));
		setStatus(Status.valueOf(json.getString("status")));
		setMachine(new Machine(json.getJSONObject("machine")));
		setManager(new Manager(json.getJSONObject("manager")));
		JSONArray workersArray = json.getJSONArray("workers");
		List<Worker> workers = new ArrayList<>();
		for (int i = 0; i < workersArray.length(); i++) {
			workers.add(new Worker(workersArray.getJSONObject(i)));
		}
		setWorkers(workers);
		
		System.out.println("Maintenance (JSONObject json): " + json);

	}

    // PROPERTIES
    public int getIdMaintenance() {
        return idMaintenance;
    }

    public void setIdMaintenance(int idMaintenance) {
    	String idString = String.valueOf(idMaintenance); 
	    if (!idString.matches("-?\\d+")) { 
	        throw new IllegalArgumentException("idMaintenance must be a valid integer.");
	    }
        this.idMaintenance = idMaintenance;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}

        this.date = date;
    }
    
    public String getDateAsString() {
        return date != null ? date.format(DateTimeFormatter.ISO_DATE) : null;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
    	String durationString = String.valueOf(duration);
	    if (!durationString.matches("-?\\d+")) { 
    		throw new IllegalArgumentException("duration must be a valid integer.");
        }
	    if (duration < 0) {
	    	throw new IllegalArgumentException("duration must be a positive integer.");
	    }
	        
        this.duration = duration;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
		if (status == null) {
			throw new IllegalArgumentException("status cannot be null");
		}
        this.status = status;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
    	if (workers == null) {
    		throw new IllegalArgumentException("workers cannot be null");
    	}
    	
        this.workers = workers;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    // METHODS
    public boolean create(MaintenanceDAO maintenanceDAO) {
        return maintenanceDAO.createDAO(this);
    }


    public boolean delete(MaintenanceDAO maintenanceDAO) {
        return maintenanceDAO.deleteDAO(this);
    }

    public boolean update(MaintenanceDAO maintenanceDAO) {
        return maintenanceDAO.updateDAO(this);
    }

    public static Maintenance find(MaintenanceDAO maintenanceDAO, int id) {
        return maintenanceDAO.findDAO(id);
    }

    public static List<Maintenance> findAll(MaintenanceDAO maintenanceDAO) {
        return maintenanceDAO.findAllDAO();
    }

    public void addWorker(Worker worker) {
        if (workers == null) {
            workers = new ArrayList<>();
        }
        if (!workers.contains(worker)) {
            workers.add(worker);
        }
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "idMaintenance=" + idMaintenance +
                //", date=" + date +
                ", duration=" + duration +
                ", report='" + report + '\'' +
                ", status=" + status +
                ", machine=" + machine +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance that = (Maintenance) o;
        return idMaintenance == that.idMaintenance &&
               duration == that.duration &&
               Objects.equals(date, that.date) &&
               Objects.equals(report, that.report) &&
               status == that.status &&
               Objects.equals(machine, that.machine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMaintenance, date, duration, report, status, machine);
    }
}
