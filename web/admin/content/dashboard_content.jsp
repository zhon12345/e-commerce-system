<%@ page import="java.util.List, Model.Users" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%-- Get values from request with null checks --%>
<%
    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer totalProducts = (Integer) request.getAttribute("totalProducts");
    Integer totalOrders = (Integer) request.getAttribute("totalOrders");
    Double revenue = (Double) request.getAttribute("totalRevenue");
    DecimalFormat df = new DecimalFormat("#,##0.00");
%>

<%-- Display error message if exists --%>
<% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <%= request.getAttribute("error") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>

<h2 class="mb-3 border-bottom pb-2">
    <i class="fas fa-tachometer-alt me-2"></i>Admin Dashboard
</h2>
<p class="text-muted mb-4">Overview of the system statistics</p>

<div class="row g-4">
    <%-- Users Card --%>
    <div class="col-xl-3 col-md-6">
        <div class="card h-100 border-start border-primary border-4">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-muted mb-2">Total Customers</h6>
                        <h2 class="mb-0 fw-bold"><%= totalUsers != null ? totalUsers : 0 %></h2>
                    </div>
                    <div class="bg-primary bg-opacity-10 rounded-circle p-3">
                        <i class="fas fa-users fa-2x text-primary"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Products Card --%>
    <div class="col-xl-3 col-md-6">
        <div class="card h-100 border-start border-success border-4">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-muted mb-2">Total Products</h6>
                        <h2 class="mb-0 fw-bold"><%= totalProducts != null ? totalProducts : 0 %></h2>
                    </div>
                    <div class="bg-success bg-opacity-10 rounded-circle p-3">
                        <i class="fas fa-box-open fa-2x text-success"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Orders Card --%>
    <div class="col-xl-3 col-md-6">
        <div class="card h-100 border-start border-info border-4">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-muted mb-2">Active Orders</h6>
                        <h2 class="mb-0 fw-bold"><%= totalOrders != null ? totalOrders : 0 %></h2>
                    </div>
                    <div class="bg-info bg-opacity-10 rounded-circle p-3">
                        <i class="fas fa-shopping-cart fa-2x text-info"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Revenue Card --%>
    <div class="col-xl-3 col-md-6">
        <div class="card h-100 border-start border-warning border-4">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-muted mb-2">Total Revenue</h6>
                        <h2 class="mb-0 fw-bold">RM <%= revenue != null ? df.format(revenue) : "0.00" %></h2>
                    </div>
                    <div class="bg-warning bg-opacity-10 rounded-circle p-3">
                        <i class="fas fa-dollar-sign fa-2x text-warning"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

