package be.fabricTout.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.fabricTout.dao.EmployeeDAO;
import be.fabricTout.dao.MachineDAO;
import be.fabricTout.dao.PurchaserDAO;
import be.fabricTout.javabeans.Employee;
import be.fabricTout.javabeans.Machine;
import be.fabricTout.javabeans.Purchaser;
import be.fabricTout.javabeans.State;
import be.fabricTout.javabeans.Zone;

public class PurchaserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MachineDAO machineDAO;
    private EmployeeDAO employeeDAO;
    private PurchaserDAO purchaserDAO;
    private Purchaser currentPurchaser = null;
    ArrayList<String> errors = new ArrayList<String>();
    List<String> successes = new ArrayList<String>();

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        machineDAO = new MachineDAO(context);
        employeeDAO = new EmployeeDAO(context);
        purchaserDAO = new PurchaserDAO(context);
    }

    public PurchaserServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        HttpSession session = request.getSession();
        Object idPurchaserObj = session.getAttribute("idEmployee"); 

        errors.add("You must be logged in to access this page.");
        if (idPurchaserObj == null) {
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
            return;
        }
        
        else if (!"Purchaser".equals(Employee.findTypeById(employeeDAO, (int) idPurchaserObj))) {
			errors.add("You must be a purchaser to access this page.");
			request.setAttribute("errors", errors);
			forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
			return;
		}
        
        int idPurchaser = (int) idPurchaserObj; 
        
        currentPurchaser = Purchaser.find(purchaserDAO, idPurchaser);
        
        if (currentPurchaser != null) {
            session.setAttribute("firstName", currentPurchaser.getFirstName());
        }

        if ("viewMachineHistory".equals(action)) {
            viewMachineHistory(request, response);
        } else if ("submitOrder".equals(action)) {
        	processMachineOrder(request, response);
        } else {
            loadAllMachines(request, response);
        }
        
    	errors.clear();
		successes.clear();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }


    private void loadAllMachines(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Machine> machines = Machine.findAll(machineDAO); 
            
            if (machines != null && !machines.isEmpty()) {
                Set<Machine> machineSet = new HashSet<>(machines); 
                machines.clear();
                machines.addAll(machineSet); 

                machines.sort((m1, m2) -> Integer.compare(m2.getMaintenances().size(), m1.getMaintenances().size()));
            }

            request.setAttribute("machines", machines);

            forwardToPage(request, response, "/WEB-INF/views/purchaser/machineList.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            errors.add("An error occurred while loading the machine list.");
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/purchaser/machineList.jsp");
        }
    }

    private void viewMachineHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String machineIdParam = request.getParameter("machineId");

        if (machineIdParam != null) {
            try {
                Machine machine = Machine.find(machineDAO, Integer.parseInt(machineIdParam));

                request.setAttribute("machine", machine);
                request.setAttribute("maintenanceHistory", machine.getMaintenances());

                if (machine.getMaintenances() != null && machine.getMaintenances().size() > 6) {
                    request.setAttribute("showReorderButton", true);
                } else {
                    request.setAttribute("showReorderButton", false);
                }

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/views/purchaser/machineHistory.jsp");
                dispatcher.forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                errors.add("An error occurred while fetching machine maintenance history.");
                request.setAttribute("errors", errors);
                forwardToPage(request, response, "/WEB-INF/views/purchaser/machineHistory.jsp");
            }
        }
    }    

    private void processMachineOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String machineIdParam = request.getParameter("machineId");

        if (machineIdParam != null) {
            try {
                Machine machine = Machine.find(machineDAO, Integer.parseInt(machineIdParam));
                if (machine == null) {
                	errors.add("Machine not found.");
                    request.setAttribute("errors", errors);
                    forwardToPage(request, response, "/WEB-INF/views/purchaser/machineHistory.jsp");
                    return;
                }

                if (machine.getMaintenances() != null && machine.getMaintenances().size() > 6) {
                    Machine newMachine = new Machine(machine.getType(), machine.getSize(), State.OPERATIONAL, machine.getZones());

                    for (Zone zone : machine.getZones()) {
                        newMachine.addZone(zone);
                    }

                    boolean success = newMachine.create(machineDAO);

                    if (success) {
                    	machine.delete(machineDAO);

                    	successes.add("Machine successfully re-ordered.");
                        request.setAttribute("successes", successes);
                        loadAllMachines(request, response);
                    } else {
                    	errors.add("Error re-ordering the machine.");
                        request.setAttribute("errors", errors);
                    }
                } else {
                	errors.add("The machine does not have enough maintenance records to qualify for re-order.");
                	request.setAttribute("errors", errors);
                    forwardToPage(request, response, "/WEB-INF/views/purchaser/machineHistory.jsp");
                }

            } catch (Exception e) {
                e.printStackTrace();
                errors.add("An error occurred while processing the order.");
                request.setAttribute("errors", errors);
                forwardToPage(request, response, "/WEB-INF/views/purchaser/machineHistory.jsp");
            }
        } else {
        	errors.add("Machine ID is missing.");
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/purchaser/machineHistory.jsp");
        }
    }
    
    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}