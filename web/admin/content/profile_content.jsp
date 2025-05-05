<%@ page import="Model.Users" %>
<jsp:useBean id="user" class="Model.Users" scope="session" />

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

<h2 class="mb-4 border-bottom pb-2 text-body">
    <i class="fas fa-user-circle"></i> My Profile
</h2>

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-body">
                <form id="profileForm" onsubmit="return validateForm()" action="${pageContext.request.contextPath}/admin/profile" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="update_profile">

                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" value="${user.getUsername()}" required>
                        <div class="invalid-feedback" id="usernameError"></div>
                    </div>

                    <div class="mb-3">
                        <label for="name" class="form-label">Name</label>
                        <input type="text" class="form-control" id="name" name="name" value="${user.getName()}" required>
                        <div class="invalid-feedback" id="nameError"></div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" value="${user.getEmail()}" required>
                        <div class="invalid-feedback" id="emailError"></div>
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label">Phone</label>
                        <input type="phone" class="form-control" id="phone" name="phone" value="${user.getContact()}" required>
                        <div class="invalid-feedback" id="phoneError"></div>
                    </div>

                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Save Changes
                    </button>
                </form>
            </div>
        </div>

        <div class="card mt-4">
            <div class="card-body">
                <h5 class="card-title mb-3">Change Password</h5>
                <form id="passwordForm" action="${pageContext.request.contextPath}/admin/profile" method="POST" onsubmit="return validatePasswordForm()">
                    <input type="hidden" name="action" value="change_password">

                    <div class="mb-3">
                        <label for="currentPassword" class="form-label">Current Password</label>
                        <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                        <div class="invalid-feedback" id="currentPasswordError"></div>
                    </div>

                    <div class="mb-3">
                        <label for="newPassword" class="form-label">New Password</label>
                        <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                        <div class="invalid-feedback" id="newPasswordError"></div>
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirm New Password</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                        <div class="invalid-feedback" id="confirmPasswordError"></div>
                    </div>

                    <button type="submit" class="btn btn-warning">
                        <i class="fas fa-key"></i> Change Password
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function validateForm() {
    let isValid = true;
    const username = document.getElementById('username');
    const name = document.getElementById('name');
    const email = document.getElementById('email');

    // Reset previous errors
    username.classList.remove('is-invalid');
    name.classList.remove('is-invalid');
    email.classList.remove('is-invalid');

    // Validate username
    if (!username.value.trim()) {
        username.classList.add('is-invalid');
        document.getElementById('usernameError').textContent = 'Username is required';
        isValid = false;
    } else if (username.value.trim().length < 3) {
        username.classList.add('is-invalid');
        document.getElementById('usernameError').textContent = 'Username must be at least 3 characters long';
        isValid = false;
    }

    // Validate name
    if (!name.value.trim()) {
        name.classList.add('is-invalid');
        document.getElementById('nameError').textContent = 'Name is required';
        isValid = false;
    }

    // Validate email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email.value.trim()) {
        email.classList.add('is-invalid');
        document.getElementById('emailError').textContent = 'Email is required';
        isValid = false;
    } else if (!emailRegex.test(email.value.trim())) {
        email.classList.add('is-invalid');
        document.getElementById('emailError').textContent = 'Invalid email format';
        isValid = false;
    }

    return isValid;
}

function validatePasswordForm() {
    let isValid = true;
    const currentPassword = document.getElementById('currentPassword');
    const newPassword = document.getElementById('newPassword');
    const confirmPassword = document.getElementById('confirmPassword');

    // Reset previous errors
    currentPassword.classList.remove('is-invalid');
    newPassword.classList.remove('is-invalid');
    confirmPassword.classList.remove('is-invalid');

    // Validate current password
    if (!currentPassword.value.trim()) {
        currentPassword.classList.add('is-invalid');
        document.getElementById('currentPasswordError').textContent = 'Current password is required';
        isValid = false;
    }

    // Validate new password
    if (!newPassword.value.trim()) {
        newPassword.classList.add('is-invalid');
        document.getElementById('newPasswordError').textContent = 'New password is required';
        isValid = false;
    } else if (newPassword.value.length < 8) {
        newPassword.classList.add('is-invalid');
        document.getElementById('newPasswordError').textContent = 'Password must be at least 8 characters long';
        isValid = false;
    }

    // Validate confirm password
    if (!confirmPassword.value.trim()) {
        confirmPassword.classList.add('is-invalid');
        document.getElementById('confirmPasswordError').textContent = 'Please confirm your password';
        isValid = false;
    } else if (confirmPassword.value !== newPassword.value) {
        confirmPassword.classList.add('is-invalid');
        document.getElementById('confirmPasswordError').textContent = 'Passwords do not match';
        isValid = false;
    }

    return isValid;
}
</script>