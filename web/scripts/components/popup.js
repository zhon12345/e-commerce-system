function closePopup() {
	const popup = document.getElementById('popup');
	const overlay = document.getElementById('overlay');

	if (popup && overlay) {
		popup.classList.remove('show');
		overlay.classList.remove('show');
	}
}