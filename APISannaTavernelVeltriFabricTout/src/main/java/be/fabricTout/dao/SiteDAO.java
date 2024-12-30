package be.fabricTout.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.fabricTout.javabeans.Color;
import be.fabricTout.javabeans.Letter;
import be.fabricTout.javabeans.Manager;
import be.fabricTout.javabeans.Site;
import be.fabricTout.javabeans.Worker;
import be.fabricTout.javabeans.Zone;
import oracle.jdbc.OracleTypes;
import oracle.sql.STRUCT;

public class SiteDAO extends DAO<Site> {

    public SiteDAO(Connection conn) {
        super(conn);
    }

    @Override
    public boolean createDAO(Site site) {
        System.out.println("SiteDAO : createDAO");
        String procedureCall = "{call add_site(?, ?, ?)}"; 
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {

            stmt.registerOutParameter(1, Types.INTEGER);

            stmt.setString(2, site.getName());
            stmt.setString(3, site.getCity());

            stmt.execute();

            int generatedId = stmt.getInt(1);
            site.setIdSite(generatedId);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteDAO(Site site) {
    	System.out.println("SiteDAO : deleteDAO");
    	String procedureCall = "{call delete_site(?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, site.getIdSite()); 
            stmt.execute();
            return true; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }


    @Override
    public boolean updateDAO(Site site) {
    	System.out.println("SiteDAO : updateDAO");
    	String procedureCall = "{call update_site(?, ?, ?, ?)}";
        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, site.getIdSite());
            stmt.setString(2, site.getName());
            stmt.setString(3, site.getCity());
            stmt.registerOutParameter(4, Types.INTEGER);

            stmt.execute();
            int rowsUpdated = stmt.getInt(4);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Site findDAO(int id) {
        System.out.println("SiteDAO : findDAO");
        String procedureCall = "{call find_site(?, ?)}";  
        Site site = null;

        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id);  // Passage de l'ID du site
            stmt.registerOutParameter(2, OracleTypes.ARRAY, "SITE_TABLE_TYPE"); 
            stmt.execute();

            Array array = stmt.getArray(2);  
            if (array != null) {
                Object[] siteObjects = (Object[]) array.getArray();  
                if (siteObjects.length > 0) {
                    Object obj = siteObjects[0];  
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();  
                        if (attributes != null && attributes.length == 6) {
                            site = setSiteDAO(attributes);  
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return site;
    }


    @Override
    public List<Site> findAllDAO() {
        System.out.println("SiteDAO : findAllDAO");
        String procedureCall = "{call find_all_sites(?)}"; 
        List<Site> sites = new ArrayList<>();

        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.ARRAY, "SITE_TABLE_TYPE");

            stmt.execute();

            Array array = stmt.getArray(1);  
            if (array != null) {
                Object[] siteObjects = (Object[]) array.getArray();  
                for (Object obj : siteObjects) {
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 6) { 
                            sites.add(setSiteDAO(attributes));  
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sites;
    }

    private Site setSiteDAO(Object[] attributes) throws SQLException {
        System.out.println("SiteDAO : setSiteDAO");
        Site site = null; 
        Zone zone = null;
        List<Zone> zones = new ArrayList<>();
        
        System.out.println("Nombre d'attributs : " + attributes.length);
        for (int i = 0; i < attributes.length; i++) {
            System.out.println("Attribut " + i + ": " + attributes[i] + " (Type: " + attributes[i].getClass().getName() + ")");
        }
        
        String zoneList = (String) attributes[10];
        if (zoneList != null && !zoneList.isEmpty()) {
            int counter = 0;
            String[] zoneEntries = zoneList.split(",");
            for (String entry : zoneEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    int zoneId = Integer.parseInt(parts[0].trim());
                    Letter letter = Letter.valueOf(parts[1].trim().toUpperCase());
                    Color color = Color.valueOf(parts[2].trim().toUpperCase());

                    if (counter == 0) {
                        Zone firstZone = new Zone(
                            zoneId,
                            letter,
                            color,
                            ((BigDecimal) attributes[7]).intValue(),  // site_id
                            (String) attributes[8],  // site_name
                            (String) attributes[9]   // site_city 
                        );
                        site = firstZone.getSite();  
                        zones.add(firstZone);
                        counter++;
                    } else {
                        zone = new Zone(
                            zoneId,
                            letter,
                            color, 
                            site
                        );
                        zones.add(zone);

                        if (site != null) {
                            site.addZone(zone);  
                        }
                    }
                }
            }
        }

        Manager manager = new Manager(
            ((BigDecimal) attributes[0]).intValue(), // id_person 
            (String) attributes[1],  // firstName 
            (String) attributes[2],  // lastName 
            LocalDate.parse((String) attributes[3]),  // birthDate 
            (String) attributes[4],  // phoneNumber 
            (String) attributes[5],  // registrationCode 
            (String) attributes[6],  // password
            site  
        );
        site.setManager(manager);  

        String workerList = (String) attributes[11];
        if (workerList != null && !workerList.isEmpty()) {
            String[] workereEntries = workerList.split(",");
            for (String entry : workereEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 5) {
                    try {
                        Worker worker = new Worker(
                            Integer.parseInt(parts[0].trim()),  // idPerson
                            parts[1].trim(),  // firstName
                            parts[2].trim(),  // lastName
                            LocalDate.parse(parts[3].trim()),  // birthDate
                            parts[4].trim(),  // phoneNumber
                            parts[5].trim(),  // registrationCode
                            parts[6].trim(),  // password
                            site  
                        );
                        site.addWorker(worker); 
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return site;  
    }
}
