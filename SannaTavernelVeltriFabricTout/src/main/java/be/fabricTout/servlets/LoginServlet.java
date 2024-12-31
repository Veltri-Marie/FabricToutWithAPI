package be.fabricTout.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.fabricTout.dao.EmployeeDAO;
import be.fabricTout.javabeans.Employee;


public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        employeeDAO = new EmployeeDAO(context);
    }

    public LoginServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            logout(request, response);
        } else {
            forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String registrationCode = request.getParameter("registrationCode");
        String password = request.getParameter("password");

        List<String> errors = validateInputs(registrationCode, password);

        if (!errors.isEmpty()) {
            redirectToErrorPage(request, response, errors);
            return;
        }

        try {
            int idEmployee = Employee.authenticate(employeeDAO, registrationCode, password);

            if (idEmployee > 0) {
                handleSuccessfulLogin(request, response, idEmployee);
            } else {
                errors.add("Incorrect username or password.");
                redirectToErrorPage(request, response, errors);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("An unexpected error occurred during authentication. Please try again later.");
            redirectToErrorPage(request, response, errors);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("idEmployee", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
    }

    private void handleSuccessfulLogin(HttpServletRequest request, HttpServletResponse response, int idEmployee)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.setAttribute("idEmployee", idEmployee);

        Cookie cookie = new Cookie("idEmployee", String.valueOf(idEmployee));
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        String employeeType = Employee.findTypeById(employeeDAO, idEmployee);
        String redirectUrl;

        switch (employeeType) {
            case "Worker":
                redirectUrl = request.getContextPath() + "/Worker";
                break;
            case "Purchaser":
                redirectUrl = request.getContextPath() + "/Purchaser";
                break;
            case "Manager":
                redirectUrl = request.getContextPath() + "/Manager";
                break;
            default:
                List<String> errors = new ArrayList<>();
                errors.add("Unknown employee type.");
                redirectToErrorPage(request, response, errors);
                return;
        }

        response.sendRedirect(response.encodeRedirectURL(redirectUrl));
    }

    private List<String> validateInputs(String registrationCode, String password) {
        List<String> errors = new ArrayList<>();

        if (registrationCode == null || registrationCode.isEmpty()) {
            errors.add("The [registrationCode] parameter is empty.");
        } else if (!registrationCode.matches("^[0-9A-Za-z]{5,}$")) {
            errors.add("The [registrationCode] must be at least 5 characters.");
        }

        if (password == null || password.isEmpty()) {
            errors.add("The [password] parameter is empty.");
        } else if (!password.matches("^[0-9A-Za-z]{4,}$")) {
            errors.add("The [password] must contain at least 4 characters.");
        }

        return errors;
    }

    private void redirectToErrorPage(HttpServletRequest request, HttpServletResponse response, List<String> errors)
            throws ServletException, IOException {
        request.setAttribute("errors", errors);
        forwardToPage(request, response, "/WEB-INF/views/user/index.jsp");
    }


    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}
