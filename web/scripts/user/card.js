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
    const addCardBtn = document.getElementById('addCardBtn');
    const addPopup = document.getElementById('addPopup');
    const closePopupBtn = document.getElementById('closePopupBtn');

    addCardBtn.addEventListener('click', function () {
        addPopup.style.display = 'flex';
        disableScroll();

        document.getElementById('number').value = "";
        document.getElementById('name').value = "";
        document.getElementById('expiryDate').value = "";
    });

    closePopupBtn.addEventListener('click', function () {
        addPopup.style.display = 'none';
        enableScroll();
    });

    document.getElementById("number").addEventListener("input", () => validateNumber());
    document.getElementById("name").addEventListener("input", () => validateName());
    document.getElementById("expiryDate").addEventListener("input", () => validateExpiryDate());
});

function validateNumber() {
    return validateRequired('number', 'Card Number');
}

function validateName() {
    return validateRequired('name', 'Card Holder Name');
}

function validateExpiryDate() {
    return validateRequired('expiryDate', 'Expiry Date');
}

function validateForm() {
    const isNumberValid = validateNumber();
    const isNameValid = validateName();
    const isExpiryDateValid = validateExpiryDate();

    return isNumberValid && isNameValid && isExpiryDateValid;
}

window.validateForm = validateForm;
window.showError = showError;
