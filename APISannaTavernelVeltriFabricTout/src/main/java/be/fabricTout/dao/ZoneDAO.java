package be.fabricTout.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.fabricTout.javabeans.Color;
import be.fabricTout.javabeans.Letter;
import be.fabricTout.javabeans.Machine;
import be.fabricTout.javabeans.State;
import be.fabricTout.javabeans.Type;
import be.fabricTout.javabeans.Zone;
import oracle.jdbc.OracleTypes;
import oracle.sql.STRUCT;

public class ZoneDAO extends DAO<Zone> {

    public ZoneDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean createDAO(Zone zone) {
        String procedureCall = "{call add_zone(?, ?, ?, ?)}"; 
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {

            stmt.registerOutParameter(1, Types.INTEGER);

            stmt.setString(2, zone.getLetter().name());
            stmt.setString(3, zone.getColor().name());
            stmt.setInt(4, zone.getSite().getIdSite());

            stmt.execute();

            int generatedId = stmt.getInt(1);
            zone.setZoneId(generatedId);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteDAO(Zone zone) {
        String procedureCall = "{call delete_zone(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, zone.getZoneId());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Zone zone) {
        String procedureCall = "{call update_zone(?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, zone.getZoneId());
            stmt.setString(2, zone.getLetter().name());
            stmt.setString(3, zone.getColor().name());
            stmt.setInt(4, zone.getSite().getIdSite());
            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(5) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Zone findDAO(int id) {
        String procedureCall = "{call find_zone(?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, OracleTypes.ARRAY, "ZONE_TABLE_TYPE"); 

            stmt.execute();

            Array array = stmt.getArray(2); 
            if (array != null) {
                Object[] zoneObjects = (Object[]) array.getArray(); 
                if (zoneObjects.length > 0) {
                    Object obj = zoneObjects[0];
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 7) { 
                            return setZone(attributes); 
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Zone> findAllDAO() {
        String procedureCall = "{call find_all_zones(?)}";
        List<Zone> zones = new ArrayList<>();
        
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.ARRAY, "ZONE_TABLE_TYPE");

            stmt.execute();

            Array array = stmt.getArray(1);  
            if (array != null) {
                Object[] zoneObjects = (Object[]) array.getArray();
                for (Object obj : zoneObjects) {
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 7) {
                            zones.add(setZone(attributes)); 
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return zones;
    }

    
    public Zone setZone(Object[] attributes) throws SQLException {
        Zone zone = new Zone(
            ((BigDecimal) attributes[0]).intValue(),  // zone_id
            Letter.valueOf(((String) attributes[1]).toUpperCase()),  // letter
            Color.valueOf(((String) attributes[2]).toUpperCase()),  // color
            ((BigDecimal) attributes[3]).intValue(),  // site_id
            (String) attributes[4],  // site_name
            (String) attributes[5]  // site_city
        );

        String machineList = (String) attributes[6];
        if (machineList != null && !machineList.isEmpty()) {
            String[] machineEntries = machineList.split(",");
            for (String entry : machineEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 4) {
                    Machine machine = new Machine(
                        Integer.parseInt(parts[0].trim()),  // id_machine
                        Type.valueOf(parts[1].trim().toUpperCase()),  // machine_type
                        Double.parseDouble(parts[2].trim()),  // machine_size
                        State.valueOf(parts[3].trim().toUpperCase()),  // machine_state
                        new ArrayList<>()
                    );
                    zone.addMachine(machine);
                }
            }
        }

        return zone;
    }

}
