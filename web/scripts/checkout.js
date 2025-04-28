import { showError, clearError } from './components/validation.js';

const creditCardSection = document.querySelector('.credit-card');
const paymentMethods = document.querySelectorAll('input[name="payment-method"]');
const addressSelector = document.getElementById('addressSelector');
const cardSelector = document.getElementById('cardSelector');
const orderForm = document.getElementById('orderForm');
const cvvInput = document.getElementById("cvv");

if (addressSelector) addressSelector.addEventListener("change", () => validateAddress());
if (cardSelector) cardSelector.addEventListener("change", () => validateCard());
if (cvvInput) cvvInput.addEventListener("input", () => validateCvv());

document.addEventListener('DOMContentLoaded', () => {
	initializeFormState();

	paymentMethods.forEach(method => {
		method.addEventListener('change', () => {
			toggleCreditCardSection(method.value)

			if (method.id !== 'credit-card') {
				cardSelector.value = '';
				clearCardFields();
				clearError('cardSelector');
				clearError('cvv');
			}
		});
	});

	addressSelector?.addEventListener('change', () => {
		updateAddressDetails();
	});

	cardSelector?.addEventListener('change', () => {
		updateCardDetails();
	});

	orderForm?.addEventListener('submit', () => {
		document.getElementById('selectedAddressId').value = addressSelector?.value || '';
		document.getElementById('selectedPaymentMethod').value = document.querySelector('input[name="payment-method"]:checked')?.value || 'cash';
		document.getElementById('selectedCardId').value = cardSelector?.value || '';
		document.getElementById('enteredCvv').value = cvvInput?.value || '';
	});
});

const initializeFormState = () => {
	const paymentRadio = document.querySelector(`input[value="${window.initialState.selectedPaymentMethod}"]`);
	if (paymentRadio) paymentRadio.checked = true;
	toggleCreditCardSection(window.initialState.selectedPaymentMethod);

	if (window.initialState.selectedAddressId && addressSelector) {
		addressSelector.value = window.initialState.selectedAddressId;
		updateAddressDetails();
	}

	if (window.initialState.selectedCardId && cardSelector) {
		cardSelector.value = window.initialState.selectedCardId;
		updateCardDetails();
	}

	if (cvvInput) cvvInput.value = window.initialState.enteredCvv;
};

function toggleCreditCardSection(method) {
	creditCardSection.style.display = method === 'card' ? 'block' : 'none';
}

function updateAddressDetails() {
	const selectedAddress = addressSelector.options[addressSelector.selectedIndex];
	if (!selectedAddress?.value) return;

	document.querySelector('[name="receiverName"]').value = selectedAddress.dataset.receiverName;
	document.querySelector('[name="contactNumber"]').value = selectedAddress.dataset.contactNumber;
	document.querySelector('[name="address1"]').value = selectedAddress.dataset.address1;
	document.querySelector('[name="address2"]').value = selectedAddress.dataset.address2;
	document.querySelector('[name="postalCode"]').value = selectedAddress.dataset.postalCode;
	document.querySelector('[name="city"]').value = selectedAddress.dataset.city;
	document.querySelector('[name="state"]').value = selectedAddress.dataset.state;
}

function updateCardDetails() {
	const selectedCard = cardSelector.options[cardSelector.selectedIndex];
	if (!selectedCard?.value) return;

	document.querySelector('[name="cardHolder"]').value = selectedCard.dataset.cardName;
	document.querySelector('[name="cardNumber"]').value = formatCardNumber(selectedCard.dataset.cardNumber);
	document.querySelector('[name="expDate"]').value = `${formatCardMonth(selectedCard.dataset.expMonth)}/${selectedCard.dataset.expYear.slice(-2)}`;
	cvvInput.value = '';
	cvvInput.focus();
}

function validateAddress() {
	if (!addressSelector?.value) {
		showError('addressSelector', 'Please select a delivery address');
		return false;
	}

	clearError('addressSelector');
	return true;
}

function validateCard() {
	if (document.querySelector('#credit-card:checked') && !cardSelector?.value) {
		showError('cardSelector', 'Please select a card');
		return false;
	}

	clearError('cardSelector');
	return true;
}

function validateCvv() {
	if (!validateCard()) return true;

	if (!cvvInput.value.trim()) {
		showError('cvv', 'CVV is required.');
		return false;
	}

	if (cvvInput.value.length < 3) {
		showError('cvv', 'CVV must be at least 3 digits long.');
		return false;
	}

	if (!/^\d{3}$/.test(cvvInput.value)) {
		showError('cvv', 'CVV must be a 3-digit number.');
		return false;
	}

	clearError('cvv');
	return true;
}

function validateForm() {
	let isValid = validateAddress();
	const paymentMethod = document.querySelector('input[name="payment-method"]:checked')?.value;

	if (paymentMethod === 'card') {
		const isCardValid = validateCard();
		const isCvvValid = validateCvv();

		isValid = isValid && isCardValid && isCvvValid;
	}

	return isValid;
}

function clearCardFields() {
	const fields = [
		'[name="cardHolder"]', '[name="cardNumber"]',
		'[name="expDate"]', '[name="cvv"]'
	];

	fields.forEach(selector => {
		const element = document.querySelector(selector);
		if (element) element.value = '';
	});
}

function formatCardNumber(number) {
	return number.replace(/\D/g, '')
		.replace(/(\d{4})(?=\d)/g, '$1 ')
		.substring(0, 19);
}

function formatCardMonth(month) {
	return month.toString().padStart(2, '0');
}

window.validateForm = validateForm;
window.showError = showError;