package be.fabricTout.dao;

import be.fabricTout.javabeans.Color;
import be.fabricTout.javabeans.Letter;
import be.fabricTout.javabeans.Machine;
import be.fabricTout.javabeans.Maintenance;
import be.fabricTout.javabeans.Manager;
import be.fabricTout.javabeans.Site;
import be.fabricTout.javabeans.State;
import be.fabricTout.javabeans.Status;
import be.fabricTout.javabeans.Type;
import be.fabricTout.javabeans.Zone;
import oracle.jdbc.OracleTypes;
import oracle.sql.STRUCT;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MachineDAO extends DAO<Machine> {
    private Connection connection;

    public MachineDAO(Connection connection) {
		super(connection);
		this.connection = connection;
    }
	
    @Override
	public boolean createDAO(Machine machine) {
	    String sql = "{CALL create_machine(?, ?, ?, ?, ?)}"; 
	    try (CallableStatement stmt = connection.prepareCall(sql)) {
	        stmt.registerOutParameter(1, Types.INTEGER); 
	        stmt.setString(2, machine.getType().toString());
	        stmt.setDouble(3, machine.getSize());
	        stmt.setString(4, machine.getState().toString());


	        String zoneIds = String.join(",",
	                machine.getZones().stream()
	                        .map(zone -> String.valueOf(zone.getZoneId()))
	                        .toArray(String[]::new));
	        stmt.setString(5, zoneIds);

	        stmt.execute();

	        int generatedId = stmt.getInt(1);
	        machine.setIdMachine(generatedId);

	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}



    @Override
    public boolean updateDAO(Machine machine) {
        String sql = "{CALL update_machine(?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.setString(2, machine.getType().toString());
            stmt.setDouble(3, machine.getSize());
            stmt.setString(4, machine.getState().toString());

            String zoneIds = String.join(",",
                    machine.getZones().stream()
                            .map(zone -> String.valueOf(zone.getZoneId()))
                            .toArray(String[]::new));
            stmt.setString(5, zoneIds);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteDAO(Machine machine) {
        String sql = "{CALL delete_machine(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, machine.getIdMachine());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Machine findDAO(int id) {
        String sql = "{CALL find_machine(?, ?)}";  
        Machine machine = null;

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.ARRAY, "MACHINE_TABLE_TYPE"); 

            stmt.execute();

            Array array = stmt.getArray(2);
            if (array != null) {
                Object[] machineObjects = (Object[]) array.getArray();
                if (machineObjects.length > 0) {
                    Object obj = machineObjects[0];
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 16) {
                            machine = setMachineDAO(attributes);  
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machine;
    }

    @Override
    public List<Machine> findAllDAO() {
        String sql = "{CALL find_all_machines(?)}";  
        List<Machine> machines = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.ARRAY, "MACHINE_TABLE_TYPE");  
            stmt.execute();

            Array array = stmt.getArray(1);
            if (array != null) {
                Object[] machineObjects = (Object[]) array.getArray();
                for (Object obj : machineObjects) {
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 16) {
                            machines.add(setMachineDAO(attributes));  
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machines;  
    }

    private Machine setMachineDAO(Object[] attributes) {
        Machine machine = null;
        Site site = null;
        
        int machineId = ((BigDecimal) attributes[0]).intValue(); 
        Type machineType = Type.valueOf((String) attributes[1]);
        double machineSize = ((BigDecimal) attributes[2]).doubleValue(); 
        State machineState = State.valueOf((String) attributes[3]); 

        Timestamp timestamp = (Timestamp) attributes[12]; 
	    String birthDateStr = timestamp.toLocalDateTime().toLocalDate().toString(); 
	    LocalDate birthDate = LocalDate.parse(birthDateStr);
	    
        Manager manager = new Manager(
        		((BigDecimal) attributes[9]).intValue(), // manager_id
                (String) attributes[10], // manager_firstName
                (String) attributes[11], // manager_lastName
                birthDate, // manager_birthDate
                (String) attributes[13], // manager_phoneNumber
                (String) attributes[14], // manager_registrationCode
                (String) attributes[15], // manager_password
                new Site() // Site
        );

        List<Zone> zones = new ArrayList<>();
        String zoneList = (String) attributes[4];
        if (zoneList != null && !zoneList.isEmpty()) {
            String[] zoneEntries = zoneList.split(",");
            for (String entry : zoneEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    Zone zone = new Zone(
                            Integer.parseInt(parts[0].trim()), // zone_id
                            Letter.valueOf(parts[1].trim().toUpperCase()), // letter
                            Color.valueOf(parts[2].trim().toUpperCase()), // color
                            ((BigDecimal) attributes[6]).intValue(), // site_id
                            (String) attributes[7], // site_name
                            (String) attributes[8] // site_city
                    );
                    zones.add(zone);
                    site = zone.getSite();
                    site.setManager(manager);
                }
            }
        }

        machine = new Machine(machineId, machineType, machineSize, machineState, zones);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

       
        String maintenanceList = (String) attributes[5];
        if (maintenanceList != null && !maintenanceList.isEmpty()) {
            String[] maintenanceEntries = maintenanceList.split(",");
            for (String entry : maintenanceEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 5) {
                	
                	String dateMaintenanceStr = parts[1].trim();
                	LocalDate dateMaintenance = LocalDate.parse(dateMaintenanceStr, formatter);
                	
                    
                    if (dateMaintenance.getYear() > LocalDate.now().getYear()) {
                        int correctedYear = dateMaintenance.getYear() - 100; 
                        dateMaintenance = dateMaintenance.withYear(correctedYear);
                    }
                    
                    Maintenance maintenance = new Maintenance(
                            Integer.parseInt(parts[0].trim()), // id_maintenance
                            dateMaintenance, // date_maintenance
                            Integer.parseInt(parts[2].trim()), // duration
                            parts[3].trim(), // report
                            Status.valueOf(parts[4].trim()), // status
                            machine, // machine
                            new Manager(), // manager
                            new ArrayList<>() // workers
                    );
                    
                    machine.addMaintenance(maintenance);
                }
            }
        }
        
        return machine;
    }
}
