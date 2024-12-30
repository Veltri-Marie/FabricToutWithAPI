package be.fabricTout.dao;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import oracle.sql.STRUCT;

import be.fabricTout.javabeans.Purchaser;
import oracle.jdbc.OracleTypes;

public class PurchaserDAO extends DAO<Purchaser> {
	private Connection connection;

	public PurchaserDAO(Connection connection) {
		super(connection);
		this.connection = connection;
	}
			
	@Override
	public boolean createDAO(Purchaser purchaser) {
		System.out.println("PurchaserDAO : createDAO");
	    String sql = "{CALL create_purchaser(?, ?, ?, ?, ?, ?, ?)}";
	    try (CallableStatement stmt = connection.prepareCall(sql)) {
	        stmt.setInt(1, purchaser.getIdPerson()); 
	        stmt.setString(2, purchaser.getFirstName());
	        stmt.setString(3, purchaser.getLastName());
	        stmt.setDate(4, Date.valueOf(purchaser.getBirthDate()));
	        stmt.setString(5, purchaser.getPhoneNumber());
	        stmt.setString(6, purchaser.getRegistrationCode());
	        stmt.setString(7, purchaser.getPassword());
	        stmt.execute();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	
	@Override
	public boolean updateDAO(Purchaser purchaser) {
		System.out.println("PurchaserDAO : updateDAO");
	    String sql = "{CALL update_purchaser(?, ?, ?, ?, ?, ?, ?)}";
	    try (CallableStatement stmt = connection.prepareCall(sql)) {
	        stmt.setInt(1, purchaser.getIdPerson());
	        stmt.setString(2, purchaser.getFirstName());
	        stmt.setString(3, purchaser.getLastName());
	        stmt.setDate(4, Date.valueOf(purchaser.getBirthDate()));
	        stmt.setString(5, purchaser.getPhoneNumber());
	        stmt.setString(6, purchaser.getRegistrationCode());
	        stmt.setString(7, purchaser.getPassword());
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


    @Override
    public boolean deleteDAO(Purchaser purchaser) {
    	System.out.println("PurchaserDAO : deleteDAO");
    	String sql = "{CALL delete_purchaser(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, purchaser.getIdPerson());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	
    @Override
    public Purchaser findDAO(int id) {
        System.out.println("PurchaserDAO : findDAO");
        String procedureCall = "{call find_purchaser(?, ?)}"; 
        Purchaser purchaser = null;

        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.setInt(1, id); 
            stmt.registerOutParameter(2, OracleTypes.ARRAY, "PURCHASER_TABLE_TYPE");

            stmt.execute();

            Array array = stmt.getArray(2);  
            if (array != null) {
                Object[] purchaserObjects = (Object[]) array.getArray(); 
                if (purchaserObjects.length > 0) {
                    Object obj = purchaserObjects[0];
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 7) {
                            purchaser = setPurchaser(attributes);  
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchaser;
    }


    @Override
    public List<Purchaser> findAllDAO() {
        System.out.println("PurchaserDAO : findAllDAO");
        String procedureCall = "{call find_all_purchasers(?)}"; 
        List<Purchaser> purchasers = new ArrayList<>();

        try (CallableStatement stmt = this.connect.prepareCall(procedureCall)) {
            stmt.registerOutParameter(1, OracleTypes.ARRAY, "PURCHASER_TABLE_TYPE");

            stmt.execute();

            Array array = stmt.getArray(1);
            if (array != null) {
                Object[] purchaserObjects = (Object[]) array.getArray(); 
                for (Object obj : purchaserObjects) {
                    if (obj instanceof STRUCT) {
                        STRUCT struct = (STRUCT) obj;
                        Object[] attributes = struct.getAttributes();
                        if (attributes != null && attributes.length == 7) {
                            purchasers.add(setPurchaser(attributes));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchasers;
    }

	
    private Purchaser setPurchaser(Object[] attributes) throws SQLException {
        System.out.println("PurchaserDAO : setPurchaser");
        
        Timestamp timestamp1 = (Timestamp) attributes[3]; 
	    String birthDateStr = timestamp1.toLocalDateTime().toLocalDate().toString(); 
	    LocalDate birthDate = LocalDate.parse(birthDateStr);

        int idPerson = ((BigDecimal) attributes[0]).intValue(); 
        String firstName = (String) attributes[1];
        String lastName = (String) attributes[2];
        String phoneNumber = (String) attributes[4];
        String registrationCode = (String) attributes[5];
        String password = (String) attributes[6];

        Purchaser purchaser = new Purchaser(idPerson, firstName, lastName, birthDate, phoneNumber, registrationCode, password);

        return purchaser;
    }

}
