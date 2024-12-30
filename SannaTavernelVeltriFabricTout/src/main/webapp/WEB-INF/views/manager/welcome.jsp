<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="be.fabricTout.javabeans.Machine" %>
<%@ page import="be.fabricTout.javabeans.Site" %>
<%@ page import="be.fabricTout.javabeans.Zone" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Machine List</title>

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



    <div class="container">
        <h1>Machine List</h1>
		<!-- Display success or error messages -->
        <%@ include file="../ErrorsHeader.jsp" %> 

        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Size</th>
                    <th>Site</th>
                    <th>Zone(s)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Machine> machines = (List<Machine>) request.getAttribute("machines");
                    if (machines != null && !machines.isEmpty()) {
                        for (Machine machine : machines) {
                %>
                <tr>
                    <td><%= machine.getIdMachine() %></td>
                    <td><%= machine.getType() %></td>
                    <td><%= machine.getState() %></td>
                    <td><%= machine.getSize() %></td>

                    <td>
                        <%
                            List<Zone> zones = machine.getZones();
                            if (zones != null && !zones.isEmpty()) {
                                Site site = zones.get(0).getSite();
                                if (site != null) {
                                    out.print(site.getName());
                                } else {
                                    out.print("No site available");
                                }
                            } else {
                                out.print("No site available");
                            }
                        %>
                    </td>

                    <td>
                        <%
                            if (zones != null && !zones.isEmpty()) {
                                for (Zone zone : zones) {
                        %>
                            <span><%= zone.getLetter() %> (<%= zone.getColor() %>)</span><br>
                        <%
                                }
                            } else {
                        %>
                            <span>No zones available</span>
                        <%
                            }
                        %>
                    </td>

                   <td>
                        <% if ("OPERATIONAL".equals(machine.getState().toString())) { %>
                            <form action="Manager" method="get" style="display:inline;">
                                <input type="hidden" name="action" value="reportMachineMaintenance" />
                                <input type="hidden" name="idMachine" value="<%= machine.getIdMachine() %>" />
                                <button type="submit" class="btn btn-info btn-sm">Report Maintenance</button>
                            </form>
                        <% } %>

                        <form action="Manager" method="get" style="display:inline; margin-left:5px;">
                            <input type="hidden" name="action" value="seeMaintenances" />
                            <input type="hidden" name="idMachine" value="<%= machine.getIdMachine() %>" />
                            <button type="submit" class="btn btn-success btn-sm">See Maintenances</button>
                        </form>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="text-center">No machines found.</td>
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
