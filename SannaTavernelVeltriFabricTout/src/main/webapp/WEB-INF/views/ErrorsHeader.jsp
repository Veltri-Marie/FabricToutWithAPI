<%@ page import="java.util.List" %>
<%
    List<String> errors = (List<String>) request.getAttribute("errors");
    List<String> successes = (List<String>) request.getAttribute("successes");
%>

<% if (errors != null && !errors.isEmpty()) { %>
    <div class="alert alert-danger">
        <ul>
            <% for (String error : errors) { %>
                <li><%= error %></li>
            <% } %>
        </ul>
    </div>
<% } %>

<% if (successes != null && !successes.isEmpty()) { %>
    <div class="alert alert-success">
        <ul>
            <% for (String success : successes) { %>
                <li><%= success %></li>
            <% } %>
        </ul>
    </div>
<% } %>

