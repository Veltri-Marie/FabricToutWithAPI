<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabricTout.javabeans.Machine" %>
<%@ page import="be.fabricTout.javabeans.Maintenance" %>
<%@ page import="be.fabricTout.javabeans.Zone" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Machine Maintenance History</title>

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
    <h1>Machine Maintenance History</h1>

    <%
        Machine machine = (Machine) request.getAttribute("machine");
        if (machine != null) {
    %>
        <div class="mb-4">
            <p><strong>Machine ID:</strong> <%= machine.getIdMachine() %></p>
            <p><strong>Type:</strong> <%= machine.getType() %></p>
            <p><strong>Size:</strong> <%= machine.getSize() %></p>

            <%
                List<Zone> zones = machine.getZones();
                if (zones != null && !zones.isEmpty()) {
                    String siteName = zones.get(0).getSite().getName();
            %>
                <p><strong>Site:</strong> <%= siteName %></p>
                <p><strong>Zones:</strong></p>
                <ul>
                    <%
                        for (Zone zone : zones) {
                    %>
                        <li><%= zone.getLetter() + " (" + zone.getColor() + ")" %></li>
                    <%
                        }
                    %>
                </ul>
            <%
                } else {
                    out.println("<p>No zones available.</p>");
                }
            %>
        </div>

        <h2>Maintenance History</h2>
        <%
            List<Maintenance> maintenanceHistory = machine.getMaintenances();
            if (maintenanceHistory != null && !maintenanceHistory.isEmpty()) {
        %>
            <table class="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Duration (hours)</th>
                        <th>Report</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Maintenance maintenance : maintenanceHistory) {
                    %>
                    <tr>
                        <td><%= maintenance.getDate() %></td>
                        <td><%= maintenance.getDuration() %></td>
                        <td><%= maintenance.getReport() != null ? maintenance.getReport() : "No report available" %></td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        <% 
            } else {
                out.println("<p>No maintenance history available.</p>");
            }
        %>

        <%
            Boolean showReorderButton = (Boolean) request.getAttribute("showReorderButton");
            if (showReorderButton != null && showReorderButton) {
        %>
            <form action="Purchaser" method="POST" class="mt-3">
                <input type="hidden" name="action" value="submitOrder"/>
                <input type="hidden" name="machineId" value="<%= machine.getIdMachine() %>"/>

                <button type="submit" class="btn btn-warning">Re-order Machine</button>
            </form>

        <%
            }
        %>

    <% } else { %>
        <p>Machine not found.</p>
    <% } %>

    <br>
    <a href="Purchaser" class="btn btn-primary mt-3">Back to Machine List</a>
</div>

<div class="text-center mt-3">
    <small class="text-muted">&copy; 2024 FabricTout</small>
</div>

<script src="${pageContext.request.contextPath}/resources/js/jquery-3.4.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
</body>
</html>
