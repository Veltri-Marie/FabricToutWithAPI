package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.fabricTout.dao.MaintenanceDAO;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "KeyMaintenance",
        scope = Maintenance.class
    )
@JsonIgnoreProperties(ignoreUnknown = true)
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

	    if (json.has("idMaintenance")) {
	    	if (!json.optString("idMaintenance").isBlank())
	    		setIdMaintenance(json.optInt("idMaintenance", -1));
	    }

	    if (json.has("date")) {
	    	if (!json.optString("date").isBlank())
	    	{
		        Object dateObject = json.get("date");
		        if (dateObject instanceof JSONArray) {
		            JSONArray dateArray = (JSONArray) dateObject;
		            if (dateArray.length() == 3) { 
		                int year = dateArray.optInt(0, 0);
		                int month = dateArray.optInt(1, 1);
		                int day = dateArray.optInt(2, 1);
		                setDate(LocalDate.of(year, month, day));
		            }
		        } else if (dateObject instanceof String) {
		            try {
		                setDate(LocalDate.parse((String) dateObject));
		            } catch (Exception e) {
		                System.err.println("Erreur lors de l'analyse de la date : " + dateObject);
		                e.printStackTrace();
		            }
		        }
	    	}
	    }

	    if (json.has("duration")) {
	    	if (!json.optString("duration").isBlank())
	    		setDuration(json.optInt("duration", 0));
	    }

	    if (json.has("report")) {
	    	if (!json.optString("report").isBlank())
	    		setReport(json.optString("report", ""));
	    }

	    if (json.has("status")) {
	    	if (!json.optString("status").isBlank())
	    		setStatus(Status.valueOf(json.getString("status")));
	    }

	    if (json.has("machine")) {
	        JSONObject machineJson = json.optJSONObject("machine");
	        if (machineJson != null) {
	        	Object machineObject = json.get("machine");
	        	if (machineObject instanceof JSONObject) {
                    setMachine(new Machine((JSONObject) machineObject));
	        	}
	        } 
	    }

	    if (json.has("manager")) {
	        JSONObject managerJson = json.optJSONObject("manager");
	        if (managerJson != null) {
	        	Object managerObject = json.get("manager");
	        	if (managerObject instanceof JSONObject) {
                    setManager(new Manager((JSONObject) managerObject));
	        	}
	        } 
	    }

	    if (json.has("workers")) {
	        JSONArray workersArray = json.optJSONArray("workers");
	        if (workersArray != null) {
	            List<Worker> workers = new ArrayList<>();
	            for (int i = 0; i < workersArray.length(); i++) {
	            	Object workerObject = workersArray.get(i);
	            	if (workerObject instanceof JSONObject) {
	            		workers.add(new Worker((JSONObject) workerObject));
	            	}
	            }
	            setWorkers(workers);
	        } else {
	            setWorkers(new ArrayList<>());
	        }
	    } else {
	        setWorkers(new ArrayList<>());
	    }

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
