document.getElementById("username").addEventListener("input", () => validateUsername());
document.getElementById("password").addEventListener("input", () => validatePassword());

function validateUsername() {
	const username = document.getElementById("username").value.trim();

	if (username === "") {
		showError('username', "Username / Email is required.");
		return false;
	}

	clearError('username');
	return true;
}

function validatePassword() {
	const password = document.getElementById("password").value.trim();

	if (password === "") {
		showError('password', "Password is required.");
		return false;
	}

	clearError('password');
	return true;
}

function validateForm() {
	const isUsernameValid = validateUsername();
	const isPasswordValid = validatePassword();

	return isUsernameValid && isPasswordValid;
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