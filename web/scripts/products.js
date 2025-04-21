/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


document.addEventListener('DOMContentLoaded', function () {
    const stars = document.querySelectorAll('.star');
    const selectedRatingInput = document.getElementById('selectedRating');
    let currentRating = 0;
    let selectedRating = 0;

    // Handle star click
    stars.forEach(star => {
        star.addEventListener('click', function () {
            const value = parseInt(this.getAttribute('data-value'));
            selectedRating = value;
            currentRating = value;
            selectedRatingInput.value = value;
            updateStars();
        });
    });

    // Handle star hover
    stars.forEach(star => {
        star.addEventListener('mouseover', function () {
            const value = parseInt(this.getAttribute('data-value'));
            currentRating = value;
            updateStars();
        });

        star.addEventListener('mouseout', function () {
            currentRating = selectedRating;
            updateStars();
        });
    });

    // Update star display
    function updateStars() {
        stars.forEach(star => {
            const value = parseInt(star.getAttribute('data-value'));
            if (value <= currentRating) {
                star.classList.add('hovered');
                star.classList.remove('fa-regular');
                star.classList.add('fa-solid');
            } else {
                star.classList.remove('hovered');
                star.classList.remove('fa-solid');
                star.classList.add('fa-regular');
            }
        });

        // For selected stars (persistent)
        stars.forEach(star => {
            const value = parseInt(star.getAttribute('data-value'));
            if (value <= selectedRating) {
                star.classList.add('selected');
            } else {
                star.classList.remove('selected');
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const categoryFilters = document.querySelectorAll('.filter input[type="checkbox"]');
    const minPriceInput = document.getElementById('minPrice');
    const maxPriceInput = document.getElementById('maxPrice');
    const applyPriceBtn = document.querySelector('.apply');
    const productCards = document.querySelectorAll('.products-grid .card');

    // filter products
    function filterProducts() {
        let selectedCategories = [];
        categoryFilters.forEach(filter => {
            if (filter.checked) {
                selectedCategories.push(filter.id);
            }
        });

        // price range
        const minPrice = parseFloat(minPriceInput.value) || 0;
        const maxPrice = parseFloat(maxPriceInput.value) || Infinity;

        productCards.forEach(card => {
            const productCategory = card.querySelector('.name').textContent.toLowerCase(); // Adjust this according to your product data
            const productPrice = parseFloat(card.querySelector('.price').textContent.replace('RM ', '').replace(',', ''));

            const matchesCategory = selectedCategories.some(category => productCategory.includes(category.toLowerCase()));
            const matchesPrice = productPrice >= minPrice && productPrice <= maxPrice;

            if ((selectedCategories.length === 0 || matchesCategory) && (productPrice >= minPrice && productPrice <= maxPrice)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }

    categoryFilters.forEach(filter => {
        filter.addEventListener('change', filterProducts);
    });

    applyPriceBtn.addEventListener('click', filterProducts);
    filterProducts();
});

