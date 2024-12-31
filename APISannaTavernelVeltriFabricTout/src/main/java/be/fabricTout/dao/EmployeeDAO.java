package be.fabricTout.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import be.fabricTout.javabeans.Employee;

public class EmployeeDAO extends DAO<Employee> {
	
	public EmployeeDAO(Connection conn) {
        super(conn);
    }
    
	public int authenticateDAO(String registrationCode, String password) {
        String procedureCall = "{call authenticate_employee(?, ?, ?)}";
        try (CallableStatement stmt = connect.prepareCall(procedureCall)) {
            stmt.setString(1, registrationCode);
            stmt.setString(2, password);

            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.execute();

            return stmt.getInt(3); 
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; 
        }
    }
    
    public String findTypeByIdDAO(int id) {
        String sql = "{call find_employee_type(?, ?)}"; 
        try (CallableStatement stmt = this.connect.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.execute();
            return stmt.getString(2); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

	@Override
	public boolean createDAO(Employee obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDAO(Employee obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDAO(Employee obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Employee findDAO(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Employee> findAllDAO() {
		// TODO Auto-generated method stub
		return null;
	}
    

}
