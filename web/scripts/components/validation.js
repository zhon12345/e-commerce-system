function validateRequired(field, message) {
	const input = document.getElementById(field).value.trim();

	if (input === "") {
		showError(field, `${message} is required.`);
		return false;
	}

	clearError(field);
	return true;
}

function validateEmail() {
	const email = document.getElementById("email").value.trim();
	const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

	if (!validateRequired('email', 'Email')) {
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

	if (!validateRequired('password', 'Password')) {
		isValid = false;
	} else if (password.length < 8) {
		showError('password', "Password must be at least 8 characters long.");
		isValid = false;
	}

	if (!validateRequired('confirmPassword', 'Confirm Password')) {
		isValid = false;
	} else if (password !== confirmPassword) {
		showError('password', "Passwords do not match.");
		showError('confirmPassword', "Passwords do not match.");
		isValid = false;
	}

	if (password === confirmPassword && password.length >= 8) {
		clearError('password');
		clearError('confirmPassword');
	}

	return isValid;
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

export { validateRequired, validateEmail, validatePasswords, showError, clearError }