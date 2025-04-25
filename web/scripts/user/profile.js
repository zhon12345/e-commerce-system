import { validateRequired, validateEmail, showError } from '../components/validation.js';

document.getElementById("name").addEventListener("input", () => validateName());
document.getElementById("email").addEventListener("input", () => validateEmail());
document.getElementById("phone").addEventListener("input", () => validatePhone());

document.addEventListener('DOMContentLoaded', () => {
	const avatarInput = document.getElementById('avatar');
	if (avatarInput) {
		document.querySelector('.upload-pic')?.addEventListener('click', function () {
			avatarInput.click();
		});

		avatarInput.addEventListener('change', function () {
			previewAvatar(this);
		});
	}
});

function previewAvatar(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();

		reader.onload = function (e) {
			document.getElementById('userAvatar').src = e.target.result;
		}

		reader.readAsDataURL(input.files[0]);
	}
}

function validateName() {
	return validateRequired('name', 'Name');
}

function validatePhone() {
	return validateRequired('phone', 'Phone Number');
}

function validateForm() {
	const isNameValid = validateName();
	const isEmailValid = validateEmail();
	const isPhoneValid = validatePhone();

	return isNameValid && isEmailValid && isPhoneValid;
}

window.previewAvatar = previewAvatar;
window.validateForm = validateForm;
window.showError = showError;