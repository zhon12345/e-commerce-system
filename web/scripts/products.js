document.addEventListener('DOMContentLoaded', function () {
	const form = document.querySelector('form.box');
	const stars = document.querySelectorAll('.star-rating .star');
	const selectedRatingInput = document.getElementById('selectedRating');
	let currentRating = parseInt(selectedRatingInput.value) || 0;

	updateStars(currentRating);

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
			form.requestSubmit();
		});
	});

	form.addEventListener('submit', function (e) {
		const minInput = document.getElementById('min');
		const maxInput = document.getElementById('max');

		if (!minInput.value) minInput.disabled = true;
		if (!maxInput.value) maxInput.disabled = true;
		if (!selectedRatingInput.value) selectedRatingInput.disabled = true;
	});

	function updateStars(rating) {
		stars.forEach(star => {
			const starValue = parseInt(star.getAttribute('data-value'));
			if (starValue <= rating) {
				star.classList.replace('fa-regular', 'fa-solid');
			} else {
				star.classList.replace('fa-solid', 'fa-regular');
			}
		});
	}
});