package be.fabricTout.dao;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import oracle.sql.STRUCT;

import be.fabricTout.javabeans.Color;
import be.fabricTout.javabeans.Letter;
import be.fabricTout.javabeans.Site;
import be.fabricTout.javabeans.Worker;
import be.fabricTout.javabeans.Zone;
import oracle.jdbc.OracleTypes;

public class WorkerDAO extends DAO<Worker> {
	
	private Connection connection;

	public WorkerDAO(Connection connection) {
		super(connection);
		this.connection = connection;
	}
	
    @Override
    public boolean createDAO(Worker worker) {
        String sql = "{CALL create_worker(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, worker.getFirstName());
            stmt.setString(2, worker.getLastName());
            stmt.setDate(3, Date.valueOf(worker.getBirthDate()));
            stmt.setString(4, worker.getPhoneNumber());
            stmt.setString(5, worker.getRegistrationCode());
            stmt.setString(6, worker.getPassword());
            stmt.setInt(7, worker.getSite().getIdSite());
            stmt.registerOutParameter(8, Types.INTEGER);
            stmt.execute();
            worker.setIdPerson(stmt.getInt(8));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Worker worker) {
        String sql = "{CALL update_worker(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, worker.getIdPerson());
            stmt.setString(2, worker.getFirstName());
            stmt.setString(3, worker.getLastName());
            stmt.setDate(4, Date.valueOf(worker.getBirthDate()));
            stmt.setString(5, worker.getPhoneNumber());
            stmt.setString(6, worker.getRegistrationCode());
            stmt.setString(7, worker.getPassword());
            stmt.setInt(8, worker.getSite().getIdSite());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Worker worker) {
        String sql = "{CALL delete_worker(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, worker.getIdPerson());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Worker findDAO(int id) {
        String sql = "{CALL find_worker(?, ?)}"; 
        Worker worker = null;

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.ARRAY, "WORKER_TABLE_TYPE");

            stmt.execute();

            Array array = stmt.getArray(2);
            if (array != null) {
                Object[] workerObjects = (Object[]) array.getArray();

                if (workerObjects.length > 0) {
                    Object obj = workerObjects[0];
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;

                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 11) {
                            worker = setWorker(attributes);  
                        }
                    }
                }
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return worker;  
    }

    @Override
    public List<Worker> findAllDAO() {
        String sql = "{CALL find_all_workers(?)}";  
        List<Worker> workers = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.ARRAY, "WORKER_TABLE_TYPE"); 
            stmt.execute();

            Array array = stmt.getArray(1);
            if (array != null) {
                Object[] workerObjects = (Object[]) array.getArray();

                for (Object obj : workerObjects) {
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;

                        Object[] attributes = struct.getAttributes();  

                        if (attributes != null && attributes.length == 11) {

                            Worker worker = setWorker(attributes);  
                            workers.add(worker);  
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workers;
    }

    
	public Worker setWorker(Object[] attributes) {

	    Site site = null;
	    
	    String zoneList = (String) attributes[10]; 
	    if (zoneList != null && !zoneList.isEmpty()) {
	        String[] zoneEntries = zoneList.split(",");
	        for (String entry : zoneEntries) {
	            String[] parts = entry.split(":");
	            if (parts.length == 3) {

	                Zone zone = new Zone(
	                	    Integer.parseInt(parts[0].trim()), // idZone
	                	    Letter.valueOf(parts[1].trim().toUpperCase()), // letter
	                	    Color.valueOf(parts[2].trim().toUpperCase()), // color
	                	    ((BigDecimal) attributes[7]).intValue(), // site_id 
	                	    (String) attributes[8], // site_name
	                	    (String) attributes[9]  // site_city
	                	);
	                site = zone.getSite();
	            }
	        }
	    }

	    Timestamp timestamp = (Timestamp) attributes[3];
	    String birthDateStr = timestamp.toLocalDateTime().toLocalDate().toString();
	    LocalDate birthDate = LocalDate.parse(birthDateStr);
	    
	    Worker worker = new Worker(
	    		((BigDecimal) attributes[0]).intValue(),            // id_person
	            (String) attributes[1],        // firstName
	            (String) attributes[2],        // lastName
	            birthDate,  // birthDate
	            (String) attributes[4],        // phoneNumber
	            (String) attributes[5],        // registrationCode
	            (String) attributes[6],        // password
	            site      
	    );

	    return worker;
	}
}
