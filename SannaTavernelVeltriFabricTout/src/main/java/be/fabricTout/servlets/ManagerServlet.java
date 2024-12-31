package be.fabricTout.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.fabricTout.dao.EmployeeDAO;
import be.fabricTout.dao.MachineDAO;
import be.fabricTout.dao.MaintenanceDAO;
import be.fabricTout.dao.ManagerDAO;
import be.fabricTout.dao.WorkerDAO;
import be.fabricTout.javabeans.Employee;
import be.fabricTout.javabeans.Machine;
import be.fabricTout.javabeans.Maintenance;
import be.fabricTout.javabeans.Manager;
import be.fabricTout.javabeans.State;
import be.fabricTout.javabeans.Status;
import be.fabricTout.javabeans.Worker;

public class ManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MachineDAO machineDAO;
    private MaintenanceDAO maintenanceDAO;
    private WorkerDAO workerDAO;
    private ManagerDAO managerDAO;
    private EmployeeDAO employeeDAO;
    Manager currentManager = null;
    ArrayList<String> errors = new ArrayList<String>();
    List<String> successes = new ArrayList<String>();


    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        machineDAO = new MachineDAO(context);
        maintenanceDAO = new MaintenanceDAO(context);
        workerDAO = new WorkerDAO(context);
        managerDAO = new ManagerDAO(context);
        employeeDAO = new EmployeeDAO(context);

    }

    public ManagerServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Object idManagerObj = session.getAttribute("idEmployee");

        if (idManagerObj == null) {
        	errors.add("You must be logged in to access this page.");
            request.setAttribute("errors", errors);
            return;
        }
        
        else if (!"Manager".equals(Employee.findTypeById(employeeDAO, (int) idManagerObj))) {
			errors.add("You must be a manager to access this page.");
			request.setAttribute("errors", errors);
			forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
			return;
		}

        int idManager = (int) idManagerObj;
        currentManager = Manager.find(managerDAO, idManager);

        if (currentManager != null) {
            session.setAttribute("firstName", currentManager.getFirstName());
        }
        

        String action = request.getParameter("action");
        
        successes.clear();
        errors.clear();

        try {
            if ("reportMachineMaintenance".equals(action)) {
                reportMachineMaintenance(request, response);
            } else if ("seeMaintenances".equals(action)) {
                seeMaintenances(request, response);
            } else if ("validate".equals(action)) {
                validCompletedMaintenance(request, response);
			} else if ("refused".equals(action)) {
				refusedCompletedMaintenance(request, response);
            } else {
                loadAllMachines(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("An error occurred while processing the request.");
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/manager/welcome.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void loadAllMachines(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Machine> machines = Machine.findAll(machineDAO);
            List<Machine> machinesManagerSite = new ArrayList<>();
            
            for (Machine machine : machines) {
                if (machine.getZones().get(0).getSite().getIdSite() == currentManager.getSite().getIdSite()) {
                    machinesManagerSite.add(machine);
                }
            }

            Set<Machine> uniqueMachines = new HashSet<>(machinesManagerSite);

            List<Machine> sortedMachines = uniqueMachines.stream()
                    .sorted(Comparator
                            .comparing((Machine m) -> m.getMaintenances().stream()
                                    .anyMatch(maintenance -> maintenance.getStatus() == Status.WAITING))
                            .reversed() 
                            .thenComparing((Machine m) -> "NEEDS_MAINTENANCE".equals(m.getState().toString())) 
                            .thenComparing(Machine::getIdMachine)) 
                    .collect(Collectors.toList());
             
            request.setAttribute("machines", sortedMachines);
            forwardToPage(request, response, "/WEB-INF/views/manager/welcome.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Error loading the machine list.");
            request.setAttribute("errors", errors);
            forwardToPage(request, response, "/WEB-INF/views/manager/welcome.jsp");
        }
    }

    private void reportMachineMaintenance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMachine = Integer.parseInt(request.getParameter("idMachine"));
            Machine machine = Machine.find(machineDAO, idMachine);
            List<Worker> workers = Worker.findAll(workerDAO);

            if (machine != null && machine.getZones() != null && !machine.getZones().isEmpty()) {
                int siteId = machine.getZones().get(0).getSite().getIdSite();
                List<Worker> workersSite = workers.stream()
                        .filter(worker -> worker.getSite() != null && worker.getSite().getIdSite() == siteId)
                        .collect(Collectors.toList());

                if ("OPERATIONAL".equals(machine.getState().toString())) {
                    machine.setState(State.valueOf("NEEDS_MAINTENANCE"));
                    machine.update(machineDAO);

                    Maintenance maintenance = new Maintenance(LocalDate.now(), 0, "", Status.IN_PROGRESS, machine, currentManager, workersSite);
                    maintenance.create(maintenanceDAO);
                    
                    successes.add("Maintenance successfully reported for Machine ID: " + machine.getIdMachine());
                    request.setAttribute("successes", successes);
                } else {
                	errors.add("Only machines in OPERATIONAL state can be reported for maintenance.");
                    request.setAttribute("errors", errors);
                }
            } else {
            	errors.add("Machine or associated site not found.");
	            request.setAttribute("errors", errors);
            }
            loadAllMachines(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Error reporting machine maintenance.");
            request.setAttribute("errors", errors);
            loadAllMachines(request, response);
        }
    }


    private void seeMaintenances(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMachine = Integer.parseInt(request.getParameter("idMachine"));
            Machine machine = Machine.find(machineDAO, idMachine);

            if (machine != null) {
                List<Maintenance> maintenances = machine.getMaintenances();
                List<Maintenance> sortedMaintenances = maintenances.stream()
                        .sorted(Comparator
                                .comparing((Maintenance m) -> m.getStatus() == Status.WAITING).reversed()
                                .thenComparing(Maintenance::getIdMaintenance))
                        .collect(Collectors.toList());

                request.setAttribute("machine", machine);
                request.setAttribute("maintenances", sortedMaintenances);
                forwardToPage(request, response, "/WEB-INF/views/manager/seeMaintenances.jsp");
            } else {
            	errors.add("Machine not found.");
                request.setAttribute("errors", errors);
                loadAllMachines(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Error retrieving maintenances.");
            request.setAttribute("errors", errors);
            loadAllMachines(request, response);
        }
    }

    private void validCompletedMaintenance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMachine = Integer.parseInt(request.getParameter("idMachine"));
            int idMaintenance = Integer.parseInt(request.getParameter("idMaintenance"));
            Machine machine = Machine.find(machineDAO, idMachine);
            Maintenance maintenance = Maintenance.find(maintenanceDAO, idMaintenance);

            if (machine != null && "NEEDS_MAINTENANCE".equals(machine.getState().toString())
                    && maintenance != null && "WAITING".equals(maintenance.getStatus().toString())) {

                maintenance.setStatus(Status.valueOf("COMPLETED"));
                maintenance.update(maintenanceDAO);

                machine.setState(State.valueOf("OPERATIONAL"));
                machine.update(machineDAO);
                
                successes.add("Maintenance validated successfully.");
                request.setAttribute("successes", successes);
            } else {
            	errors.add("Only 'WAITING' maintenances for machines in 'NEEDS_MAINTENANCE' state can be validated.");
                request.setAttribute("errors", errors);
            }
            seeMaintenances(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Error validating maintenance.");
            request.setAttribute("errors", errors);
            loadAllMachines(request, response);
        }
    }

    private void refusedCompletedMaintenance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMachine = Integer.parseInt(request.getParameter("idMachine"));
            int idMaintenance = Integer.parseInt(request.getParameter("idMaintenance"));
            Machine machine = Machine.find(machineDAO, idMachine);
            Maintenance maintenance = Maintenance.find(maintenanceDAO, idMaintenance);

            if (machine != null && "NEEDS_MAINTENANCE".equals(machine.getState().toString())
                    && maintenance != null && "WAITING".equals(maintenance.getStatus().toString())) {

                maintenance.setStatus(Status.REJECTED);
                maintenance.update(maintenanceDAO);

                successes.add("Maintenance refused successfully.");
                request.setAttribute("successes", successes);
            } else {
            	errors.add("Only 'WAITING' maintenances for machines in 'NEEDS_MAINTENANCE' state can be refused.");
                request.setAttribute("errors", errors);
            }
            seeMaintenances(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Error refusing maintenance.");
            request.setAttribute("errors", errors);
            loadAllMachines(request, response);
        }
    }


    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
            dispatcher.forward(request, response);
    }

}
