package be.fabricTout.dao;

import be.fabricTout.javabeans.Color;
import be.fabricTout.javabeans.Letter;

import be.fabricTout.javabeans.Manager;
import be.fabricTout.javabeans.Site;

import be.fabricTout.javabeans.Zone;
import oracle.jdbc.OracleTypes;
import oracle.sql.STRUCT;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManagerDAO extends DAO<Manager> {
    private Connection connection;

    public ManagerDAO(Connection connection) {
        super(connection);
        this.connection = connection;
        }
        
    @Override
    public boolean createDAO(Manager manager) {
        String sql = "{CALL create_manager(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, manager.getIdPerson()); 
            stmt.setString(2, manager.getFirstName());
            stmt.setString(3, manager.getLastName());
            stmt.setDate(4, Date.valueOf(manager.getBirthDate()));
            stmt.setString(5, manager.getPhoneNumber());
            stmt.setString(6, manager.getRegistrationCode());
            stmt.setString(7, manager.getPassword());
            stmt.setInt(8, manager.getSite().getIdSite());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean updateDAO(Manager manager) {
        String sql = "{CALL update_manager(?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
        	stmt.setString(1, manager.getFirstName());
            stmt.setString(2, manager.getLastName());
            stmt.setDate(3, Date.valueOf(manager.getBirthDate()));
            stmt.setString(4, manager.getPhoneNumber());
            stmt.setString(5, manager.getRegistrationCode());
            stmt.setString(6, manager.getPassword());
            stmt.setInt(7, manager.getSite().getIdSite());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDAO(Manager manager) {
        String sql = "{CALL delete_manager(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, manager.getIdPerson());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Manager findDAO(int id) {
        String procedureCall = "{call find_manager(?, ?)}"; 
        Manager manager = null;

        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id); 
            stmt.registerOutParameter(2, OracleTypes.ARRAY, "MANAGER_TABLE_TYPE"); 

            stmt.execute();

            Array array = stmt.getArray(2); 
            if (array != null) {
                Object[] managerObjects = (Object[]) array.getArray();
                if (managerObjects.length > 0) {
                    Object obj = managerObjects[0];
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 11) {
                            manager = setManager(attributes);  
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manager;
    }

    @Override
    public List<Manager> findAllDAO() {
        String procedureCall = "{call find_all_managers(?)}";
        List<Manager> managers = new ArrayList<>();

        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.ARRAY, "MANAGER_TABLE_TYPE");  

            stmt.execute();

            Array array = stmt.getArray(1); 
            if (array != null) {
                Object[] managerObjects = (Object[]) array.getArray(); 
                for (Object obj : managerObjects) {
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 11) {
                            managers.add(setManager(attributes)); 
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managers;
    }

    
    public Manager setManager(Object[] attributes) {
        Site site = null;
        List<Zone> zones = new ArrayList<>();

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
                    zones.add(zone);
                    site = zone.getSite(); 
                }
            }
        }
        
        Timestamp timestamp1 = (Timestamp) attributes[3]; 
	    String birthDateStr = timestamp1.toLocalDateTime().toLocalDate().toString(); 
	    LocalDate birthDate = LocalDate.parse(birthDateStr);

        Manager manager = new Manager(
        		((BigDecimal) attributes[0]).intValue(), // id_person
                (String) attributes[1], // firstName
                (String) attributes[2], // lastName
                birthDate, // birthDate
                (String) attributes[4], // phoneNumber
                (String) attributes[5], // registrationCode
                (String) attributes[6], // password
                site
        );

        return manager;
    }
}