<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabricTout.javabeans.Maintenance" %>
<%@ page import="be.fabricTout.javabeans.Machine" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Worker Dashboard</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/owl.carousel.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/fonts.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/responsive.css" />
</head>
<body class="sub_page">

<div class="hero_area">
    <!-- Header section starts -->
    <header class="header_section">
        <div class="container">
            <nav class="navbar navbar-expand-lg custom_nav-container align-items-center">
                <!-- Brand and logo -->
                <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/Login">
                    <img src="${pageContext.request.contextPath}/resources/images/logo.png" alt="FabricTout Logo" class="logo">
                    <span class="ml-2">FabricTout</span>
                </a>
                <!-- Navbar toggler for mobile view -->
                <button class="navbar-toggler" type="button" 
                        data-toggle="collapse" data-target="#navbarSupportedContent" 
                        aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <!-- Navbar content -->
                <div class="collapse navbar-collapse justify-content-end" id="navbarSupportedContent">
				    <ul class="navbar-nav d-flex align-items-center">
				        <!-- Greeting the user -->
				        <li class="nav-item">
				            <span class="nav-link btn btn-primary text-white rounded-pill px-3 py-2">
				                Hi, <strong><%= session.getAttribute("firstName") != null ? session.getAttribute("firstName") : "Guest" %></strong>
				            </span>
				        </li>
				        <!-- Logout functionality -->
				        <li class="nav-item">
				            <form action="Login" method="get" class="nav-link btn">
	                            <input type="hidden" name="action" value="logout" />
								<button type="Submit" class="nav-link btn">Logout</button>                        
							</form>
				        </li>
				    </ul>
				</div>
            </nav>
        </div>
    </header>
    <!-- Header section ends -->
</div>

<div class="container mt-4">
    <h1 class="mb-4">Welcome to Worker Dashboard</h1>

    <!-- Display success or error messages -->
        <%@ include file="../ErrorsHeader.jsp" %> 
        
    <h3>Assigned Maintenances</h3>
    <table class="table table-striped table-bordered">
        <thead>
            <tr>
                <th>ID</th>
                <th>Machine</th>
                <th>Date</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Maintenance> maintenances = (List<Maintenance>) request.getAttribute("maintenances");
                if (maintenances != null && !maintenances.isEmpty()) {
                    for (Maintenance maintenance : maintenances) {
            %>
            <tr>
                <td><%= maintenance.getIdMaintenance() %></td>
                <td><%= maintenance.getMachine().getType() %> (ID: <%= maintenance.getMachine().getIdMachine() %>)</td>
                <td><%= maintenance.getDate() %></td>
                <td><%= maintenance.getStatus() %></td>
                <td>
                    <% if ("IN_PROGRESS".equals(maintenance.getStatus().toString()) || "REJECTED".equals(maintenance.getStatus().toString())) { %>
                        <form action="Worker" method="get" style="display:inline;">
                            <input type="hidden" name="action" value="reportCompletedMaintenance" />
                            <input type="hidden" name="idMaintenance" value="<%= maintenance.getIdMaintenance() %>" />
                            <button type="submit" class="btn btn-success btn-sm">Report Completion</button>
                        </form>
                    <% } %>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="5" class="text-center">No maintenances assigned to you.</td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>

<div class="text-center mt-3">
    <small class="text-muted">&copy; 2024 FabricTout</small>
</div>

<script src="${pageContext.request.contextPath}/resources/js/jquery-3.4.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
</body>
</html>
