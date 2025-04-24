import { validateRequired, validateEmail, validatePasswords, showError } from './components/validation.js';

document.getElementById("username").addEventListener("input", () => validateUsername());
document.getElementById("email").addEventListener("input", () => validateEmail());
document.getElementById("password").addEventListener("input", () => validatePasswords());
document.getElementById("confirmPassword").addEventListener("input", () => validatePasswords());

function validateUsername() {
	return validateRequired('username', 'Username');
}

function validateForm() {
	const isUsernameValid = validateUsername();
	const isEmailValid = validateEmail();
	const isPasswordValid = validatePasswords();

	return isUsernameValid && isEmailValid && isPasswordValid;
}

window.validateForm = validateForm;
window.showError = showError;