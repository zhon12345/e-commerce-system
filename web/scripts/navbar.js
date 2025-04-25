function toggleSearch() {
	const searchInput = document.querySelector(".search-input");
	const closeIcon = document.getElementById("closeIcon");

	searchInput.classList.toggle("active");
	closeIcon.classList.toggle("active");

	if (searchInput.classList.contains("active")) {
		searchInput.focus();
	}
}

function closeSearch() {
	const searchInput = document.querySelector(".search-input");
	const closeIcon = document.getElementById("closeIcon");

	searchInput.classList.remove("active");
	closeIcon.classList.remove("active");
	searchInput.value = "";
}

document.addEventListener("click", function (e) {
	const searchContainer = document.querySelector(".search-container");
	const searchInput = document.querySelector(".search-input");
	const searchIcon = document.getElementById("searchIcon");
	const closeIcon = document.getElementById("closeIcon");

	if (!searchContainer.contains(e.target) && e.target !== searchIcon && e.target !== closeIcon) {
		searchInput.classList.remove("active");
		closeIcon.classList.remove("active");
	}
});