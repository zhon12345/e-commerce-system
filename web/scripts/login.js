import { validateRequired, showError } from './components/validation.js';

document.getElementById("username").addEventListener("input", () => validateUsername());
document.getElementById("password").addEventListener("input", () => validatePassword());

function validateUsername() {
	return validateRequired('username', 'Username / Email');
}

function validatePassword() {
	return validateRequired('password', 'Password');
}

function validateForm() {
	const isUsernameValid = validateUsername();
	const isPasswordValid = validatePassword();

	return isUsernameValid && isPasswordValid;
}

window.validateForm = validateForm;
window.showError = showError;