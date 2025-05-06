import { showError, clearError } from './components/validation.js';

const stars = document.querySelectorAll('.star-rating .star');
const selectedRatingInput = document.getElementById('selectedRating');
const reviewTextArea = document.getElementById('review');

reviewTextArea.addEventListener("input", () => validateReview());

document.addEventListener('DOMContentLoaded', function () {
	const urlParams = new URLSearchParams(window.location.search);
	const activeTab = urlParams.get('tab') || 'description';
	let currentRating = parseInt(selectedRatingInput.value) || 0;

	if (selectedRatingInput.value) {
		updateStars(currentRating);
	}

	stars.forEach(star => {
		star.addEventListener('click', function () {
			const clickedRating = parseInt(this.getAttribute('data-value'));

			if (clickedRating === currentRating) {
				currentRating = 0;
				selectedRatingInput.value = '';
			} else {
				currentRating = clickedRating;
				selectedRatingInput.value = currentRating;
			}

			updateStars(currentRating);
			validateRating();
		});
	});

	switchTab(activeTab, null);
});

function validateRating() {
	const rating = parseInt(selectedRatingInput.value);

	if (isNaN(rating)) {
		showError("rating", "Please select a rating.");
		return false;
	} else if (rating < 1 || rating > 5) {
		showError("rating", "Please select a rating between 1 and 5.");
		return false;
	}

	clearError("rating");
	return true;
}

function validateReview() {
	const review = reviewTextArea.value.trim();

	if (review === "") {
		showError("review", "Review cannot be empty.");
		return false;
	}

	clearError("review");
	return true;
}

function validateForm() {
	const isRatingValid = validateRating();
	const isReviewValid = validateReview();

	return isRatingValid && isReviewValid;
}

function updateQuantity(change) {
	const input = document.querySelector('.quantity-input');
	let value = parseInt(input.value) + change;
	const maxStock = parseInt('${product.stock}');

	if (value < 1) value = 1;
	if (value > maxStock) value = maxStock;

	input.value = value;
}

function updateStars(rating) {
	stars.forEach(star => {
		const starValue = parseInt(star.getAttribute('data-value'));
		star.classList.toggle('fa-solid', starValue <= rating);
		star.classList.toggle('fa-regular', starValue > rating);
	});
}

function switchTab(tabName, event) {
	const url = new URL(window.location.href);
	url.searchParams.set('tab', tabName);
	window.history.replaceState(null, null, url);

	document.querySelectorAll('.content-section').forEach(section => {
		section.style.display = 'none';
	});

	document.querySelectorAll('.tab').forEach(tab => {
		tab.classList.remove('active');
	});

	document.getElementById(tabName + '-content').style.display = 'block';

	if (event) {
		event.currentTarget.classList.add('active');
	} else {
		document.querySelector(`.tab[onclick*="${tabName}"]`).classList.add('active');
	}
}

window.updateQuantity = updateQuantity;
window.switchTab = switchTab;
window.validateForm = validateForm;
window.showError = showError;