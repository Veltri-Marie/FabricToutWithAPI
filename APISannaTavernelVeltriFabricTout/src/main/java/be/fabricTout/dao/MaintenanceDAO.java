package be.fabricTout.dao;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import oracle.sql.STRUCT;


import be.fabricTout.javabeans.Machine;
import be.fabricTout.javabeans.Maintenance;
import be.fabricTout.javabeans.Manager;
import be.fabricTout.javabeans.Site;
import be.fabricTout.javabeans.State;
import be.fabricTout.javabeans.Status;
import be.fabricTout.javabeans.Type;
import be.fabricTout.javabeans.Worker;
import oracle.jdbc.OracleTypes;


public class MaintenanceDAO extends DAO<Maintenance> {

    public MaintenanceDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean createDAO(Maintenance maintenance) {
        System.out.println("MaintenanceDAO : createDAO");
        String procedureCall = "{call add_maintenance(?, ?, ?, ?, ?, ?, ?, ?)}"; 
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {

            stmt.registerOutParameter(1, Types.INTEGER);

            stmt.setDate(2, Date.valueOf(maintenance.getDate()));
            stmt.setInt(3, maintenance.getDuration());
            stmt.setString(4, maintenance.getReport());
            stmt.setString(5, maintenance.getStatus().toString());
            stmt.setInt(6, maintenance.getManager().getIdPerson());
            stmt.setInt(7, maintenance.getMachine().getIdMachine());

            String workerIds = String.join(",",
                    maintenance.getWorkers().stream()
                            .map(worker -> String.valueOf(worker.getIdPerson()))
                            .toArray(String[]::new));
            System.out.println("WorkerIds : " + workerIds);
            stmt.setString(8, workerIds); 

            stmt.execute();

            int generatedId = stmt.getInt(1);
            maintenance.setIdMaintenance(generatedId);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public boolean deleteDAO(Maintenance maintenance) {
    	System.out.println("MaintenanceDAO : deleteDAO");
        String procedureCall = "{call delete_maintenance(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, maintenance.getIdMaintenance());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Maintenance maintenance) {
        System.out.println("MaintenanceDAO : updateDAO");

        String procedureCall = "{call update_maintenance(?, ?, ?, ?, ?, ?, ?, ?)}"; 
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {

            stmt.setInt(1, maintenance.getIdMaintenance());
            stmt.setDate(2, Date.valueOf(maintenance.getDate()));
            stmt.setInt(3, maintenance.getDuration());
            stmt.setString(4, maintenance.getReport());
            stmt.setString(5, maintenance.getStatus().toString());
            stmt.setInt(6, maintenance.getMachine().getIdMachine());
            stmt.setInt(7, maintenance.getManager().getIdPerson());

            String workerIds = String.join(",",
                    maintenance.getWorkers().stream()
                            .map(worker -> String.valueOf(worker.getIdPerson()))
                            .toArray(String[]::new));
            stmt.setString(8, workerIds); 

            stmt.execute();
            System.out.println("Maintenance mise à jour avec succès.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Maintenance findDAO(int id) {
        System.out.println("MaintenanceDAO : findDAO");
        String procedureCall = "{call find_maintenance(?, ?)}";  
        Maintenance maintenance = null;

        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);  
            stmt.registerOutParameter(2, OracleTypes.ARRAY, "MAINTENANCE_TABLE_TYPE");

            stmt.execute();

            Array array = stmt.getArray(2); 
            if (array != null) {
                Object[] maintenanceObjects = (Object[]) array.getArray();
                if (maintenanceObjects.length > 0) {
                    Object obj = maintenanceObjects[0];
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 17) {
                            maintenance = setMaintenanceDAO(attributes);  
                        }
                    }
                }
            } else {
                System.out.println("Aucune donnée récupérée dans la collection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maintenance;
    }

    @Override
    public List<Maintenance> findAllDAO() {
        System.out.println("MaintenanceDAO : findAllDAO");
        String procedureCall = "{call find_all_maintenances(?)}";
        List<Maintenance> maintenances = new ArrayList<>();
        
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.ARRAY, "MAINTENANCE_TABLE_TYPE");

            stmt.execute();

            Array array = stmt.getArray(1); 
            if (array != null) {
                Object[] maintenanceObjects = (Object[]) array.getArray(); 
                for (Object obj : maintenanceObjects) {
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 17) {
                            maintenances.add(setMaintenanceDAO(attributes)); 
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maintenances;
    }


    private Maintenance setMaintenanceDAO(Object[] attributes) throws SQLException {
        System.out.println("SetMaintenanceDAO");
        Maintenance maintenance = null;

        Timestamp timestamp = (Timestamp) attributes[1]; 
	    String maintenanceDatestr = timestamp.toLocalDateTime().toLocalDate().toString(); 
	    LocalDate maintenanceDate = LocalDate.parse(maintenanceDatestr);
	    
        int maintenanceId = ((BigDecimal) attributes[0]).intValue();
        int duration = ((BigDecimal) attributes[2]).intValue();
        String report = (String) attributes[3];
        Status status = Status.valueOf((String) attributes[4]);

        Machine machine = new Machine(
            ((BigDecimal) attributes[5]).intValue(),
            Type.valueOf((String) attributes[6]),
            ((BigDecimal) attributes[7]).doubleValue(),
            State.valueOf((String) attributes[8]),
            new ArrayList<>()
        );

        Timestamp timestamp1 = (Timestamp) attributes[13]; 
	    String birthDateStr = timestamp1.toLocalDateTime().toLocalDate().toString(); 
	    LocalDate birthDate = LocalDate.parse(birthDateStr);
	    
        Manager manager = new Manager(
            ((BigDecimal) attributes[10]).intValue(),
            (String) attributes[11],
            (String) attributes[12],
            birthDate,
            (String) attributes[14],
            (String) attributes[15],
            (String) attributes[16],
            new Site()
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");


        List<Worker> workers = new ArrayList<>();
        String workerList = (String) attributes[9];
        if (workerList != null && !workerList.isEmpty()) {
            String[] workerDetails = workerList.split(",");
            for (String details : workerDetails) {
                String[] parts = details.split(":");

                LocalDate workerBirthDate = LocalDate.parse(parts[3], formatter);
                
                if (workerBirthDate.getYear() > LocalDate.now().getYear()) {
                    int correctedYear = workerBirthDate.getYear() - 100; 
                    workerBirthDate = workerBirthDate.withYear(correctedYear);
                }
            	
                Worker worker = new Worker(
                    Integer.parseInt(parts[0]),
                    parts[1],
                    parts[2],
                    workerBirthDate,
                    parts[4],
                    parts[5],
                    parts[6],
                    new Site()
                );
                workers.add(worker);
            }
        }

        maintenance = new Maintenance(
            maintenanceId,
            maintenanceDate,
            duration,
            report,
            status,
            machine,
            manager,
            workers
        );

        return maintenance;
    }
}
