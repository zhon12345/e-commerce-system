<%@ page import="java.util.List, Model.Users, java.text.SimpleDateFormat" %>

<% if (session.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <%= session.getAttribute("error") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("error"); %>
<% } %>

<% if (session.getAttribute("success") != null) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <%= session.getAttribute("success") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("success"); %>
<% } %>

<h2 class="mb-3 border-bottom pb-2 text-body"><i class="fas fa-users"></i> User Management</h2>

<%-- Get the isManager attribute passed from admin_users.jsp --%>
<%
    boolean isManager = false;
    if (request.getAttribute("isManager") != null) {
        isManager = (Boolean) request.getAttribute("isManager");
    }

    // Create date formatter for the created date
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
%>

<% if (isManager) { %>
<button type="button" class="btn btn-primary mb-3 btn-add" data-bs-toggle="modal" data-bs-target="#addUserModal">
   <i class="fas fa-plus"></i> Add User
</button>
<% } %>

<div class="table-responsive">
    <table class="table table-striped table-hover table-bordered align-middle">
        <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Created Date</th>
                <% if (isManager) { %><th>Actions</th><% } %>
            </tr>
        </thead>
        <tbody>
            <%
                List<Users> userList = (List<Users>) request.getAttribute("userList");
                if (userList != null && !userList.isEmpty()) {
                    for (Users user : userList) {
            %>
            <tr>
                <td><%= user.getId() %></td>
                <td><%= user.getUsername() %></td>
                <td><%= user.getEmail() %></td>
                <td><span class="badge bg-info"><%= user.getRole() %></span></td>
                <td><%= user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : "N/A" %></td>
                <% if (isManager) { %>
                <td>
                    <button class="btn btn-sm btn-info action-btn"
                            onclick="editUser('<%= user.getId() %>', '<%= user.getUsername() %>', '<%= user.getEmail() %>', '<%= user.getRole() %>')"
                            data-bs-toggle="modal" data-bs-target="#editUserModal">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger action-btn"
                            onclick="archiveUser(<%= user.getId() %>)">
                        <i class="fas fa-archive"></i>
                    </button>
                </td>
                <% } %>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="<%= isManager ? 6 : 5 %>" class="text-center">No users found</td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>

<% if (isManager) { %>
<!-- Add User Modal -->
<div class="modal fade" id="addUserModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-user-plus me-2"></i>Add New User</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="addUserForm" action="${pageContext.request.contextPath}/admin/users" method="POST">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="role" value="customer">

                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" required>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>

                    <div class="modal-footer px-0 pb-0">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Add User</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Edit User Modal -->
<div class="modal fade" id="editUserModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-user-edit me-2"></i>Edit User</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="editUserForm" action="${pageContext.request.contextPath}/admin/users" method="POST">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" id="editUserId" name="id">
                    <input type="hidden" name="role" value="customer">

                    <div class="mb-3">
                        <label for="editUsername" class="form-label">Username</label>
                        <input type="text" class="form-control" id="editUsername" name="username" required>
                    </div>

                    <div class="mb-3">
                        <label for="editEmail" class="form-label">Email</label>
                        <input type="email" class="form-control" id="editEmail" name="email" required>
                    </div>

                    <div class="modal-footer px-0 pb-0">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Update User</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    function editUser(id, username, email, role) {
        document.getElementById('editUserId').value = id;
        document.getElementById('editUsername').value = username;
        document.getElementById('editEmail').value = email;
    }

    function archiveUser(id) {
        if (confirm('Are you sure you want to archive this user? This action can be reversed by an administrator.')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/admin/users';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'archive';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = id;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    }
</script>
<% } %>