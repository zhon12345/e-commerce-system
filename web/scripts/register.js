document.getElementById("username").addEventListener("input", () => validateUsername());
document.getElementById("email").addEventListener("input", () => validateEmail());
document.getElementById("password").addEventListener("input", () => validatePasswords());
document.getElementById("confirmPassword").addEventListener("input", () => validatePasswords());

function validateUsername() {
	const username = document.getElementById("username").value.trim();

	if (username === "") {
		showError('username', "Username is required.");
		return false;
	}

	clearError('username');
	return true;
}

function validateEmail() {
	const email = document.getElementById("email").value.trim();
	const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

	if (email === "") {
		showError('email', "Email is required.");
		return false;
	}

	if (!regex.test(email)) {
		showError('email', "Invalid email format.");
		return false;
	}

	clearError('email');
	return true;
}

function validatePasswords() {
	const password = document.getElementById("password").value.trim();
	const confirmPassword = document.getElementById("confirmPassword").value.trim();

	let isValid = true;

	if (password === "") {
		showError('password', "Password is required.");
		isValid = false;
	} else if (password.length < 8) {
		showError('password', "Password must be at least 8 characters long.");
		isValid = false;
	} else {
		clearError('password');
	}

	if (confirmPassword === "") {
		showError('confirmPassword', "Confirm Password is required.");
		isValid = false;
	} else if (password !== confirmPassword) {
		showError('password', "Passwords do not match.");
		showError('confirmPassword', "Passwords do not match.");
		isValid = false;
	} else {
		clearError('confirmPassword');
	}

	if (password === confirmPassword && password.length >= 8) {
		clearError('password');
		clearError('confirmPassword');
	}

	return isValid;
}

function validateForm() {
	const isUsernameValid = validateUsername();
	const isEmailValid = validateEmail();
	const isPasswordValid = validatePasswords();

	return isUsernameValid && isEmailValid && isPasswordValid;
}

function showError(field, message) {
	const inputElement = document.getElementById(field);
	const errorElement = document.querySelector(`.${field} .error-message`);

	if (inputElement && errorElement) {
		inputElement.classList.add("error");

		errorElement.textContent = message;
		errorElement.style.display = "block";
	}
}

function clearError(field) {
	const inputElement = document.getElementById(field);
	const errorElement = document.querySelector(`.${field} .error-message`);

	if (inputElement && errorElement) {
		inputElement.classList.remove("error");

		errorElement.textContent = "";
		errorElement.style.display = "none";
	}
}