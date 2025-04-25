import { validateRequired, showError } from '../components/validation.js';

function disableScroll() {
	const scrollY = window.scrollY || document.documentElement.scrollTop;

	document.body.style.position = 'fixed';
	document.body.style.top = `-${scrollY}px`;
	document.body.style.width = '100%';
	document.body.style.overflow = 'hidden';
}

function enableScroll() {
	const scrollY = parseInt(document.body.style.top || '0');

	document.body.style.position = '';
	document.body.style.top = '';
	document.body.style.width = '';
	document.body.style.overflow = '';

	window.scrollTo(0, Math.abs(scrollY));
}

document.addEventListener("DOMContentLoaded", function () {
	const addAddressBtn = document.getElementById('addAddressBtn');
	const addPopup = document.getElementById('addPopup');
	const closePopupBtn = document.getElementById('closePopupBtn');

	addAddressBtn.addEventListener('click', function () {
		addPopup.style.display = 'flex';
		disableScroll();

		document.getElementById('name').value = "";
		document.getElementById('phone').value = "";
		document.getElementById('line1').value = "";
		document.getElementById('line2').value = "";
		document.getElementById('postcode').value = "";
		document.getElementById('city').value = "";
		document.getElementById('state').value = "";
	});

	closePopupBtn.addEventListener('click', function () {
		addPopup.style.display = 'none';
		enableScroll();
	});

	// Set up form validation
	document.getElementById("name").addEventListener("input", () => validateName());
	document.getElementById("phone").addEventListener("input", () => validatePhone());
	document.getElementById("line1").addEventListener("input", () => validateLine());
	document.getElementById("city").addEventListener("input", () => validateCity());
	document.getElementById("state").addEventListener("input", () => validateState());
	document.getElementById("postcode").addEventListener("input", () => validatePostcode());
});

function validateName() {
	return validateRequired('name', 'Name');
}

function validatePhone() {
	return validateRequired('phone', 'Phone Number');
}

function validateLine() {
	return validateRequired('line1', 'Line 1');
}

function validatePostcode() {
	return validateRequired('postcode', 'Postcode');
}

function validateCity() {
	return validateRequired('city', 'City');
}

function validateState() {
	return validateRequired('state', 'State');
}

function validateForm() {
	const isNameValid = validateName();
	const isPhoneValid = validatePhone();
	const isLine1Valid = validateLine();
	const isCityValid = validateCity();
	const isStateValid = validateState();
	const isPostcodeValid = validatePostcode();

	return isNameValid && isPhoneValid && isLine1Valid && isCityValid && isStateValid && isPostcodeValid;
}

window.validateForm = validateForm;
window.showError = showError;




