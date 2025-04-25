// products.js
document.addEventListener('DOMContentLoaded', function () {
    // Star rating filter
    const stars = document.querySelectorAll('.star-rating .star');
    const selectedRatingInput = document.getElementById('selectedRating');

    stars.forEach(star => {
        star.addEventListener('click', function () {
            const value = parseInt(this.getAttribute('data-value'));
            selectedRatingInput.value = value;

            // Update star display
            stars.forEach((s, index) => {
                if (index < value) {
                    s.classList.remove('fa-regular');
                    s.classList.add('fa-solid');
                } else {
                    s.classList.remove('fa-solid');
                    s.classList.add('fa-regular');
                }
            });
        });
    });

    // Apply rating filter
    document.getElementById('applyRating').addEventListener('click', function () {
        const rating = parseInt(selectedRatingInput.value);
        // You would typically make an AJAX call here to filter products
        // For now, we'll just show an alert
        alert(`Filtering by rating: ${rating}+ stars`);
        // Implement actual filtering logic here
    });

    // Apply price filter
    document.getElementById('applyPrice').addEventListener('click', function () {
        const minPrice = document.getElementById('minPrice').value;
        const maxPrice = document.getElementById('maxPrice').value;

        // Validate inputs
        if (minPrice && isNaN(minPrice)) {  // Fixed parentheses
            alert('Please enter a valid minimum price');
            return;
        }

        if (maxPrice && isNaN(maxPrice)) {  // Fixed parentheses
            alert('Please enter a valid maximum price');
            return;
        }

        // You would typically make an AJAX call here to filter products
        alert(`Filtering by price range: RM ${minPrice || '0'} to RM ${maxPrice || 'Any'}`);
        // Implement actual filtering logic here
    });


    // Category filter
    const categoryCheckboxes = document.querySelectorAll('input[name="category"]');
    categoryCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            const selectedCategories = Array.from(document.querySelectorAll('input[name="category"]:checked'))
                    .map(cb => cb.value);
            // You would typically make an AJAX call here to filter products
            alert(`Filtering by categories: ${selectedCategories.join(', ')}`);
            // Implement actual filtering logic here
        });
    });

    // Add to cart functionality
    document.querySelectorAll('.add-to-cart').forEach(button => {
        button.addEventListener('click', function () {
            const productId = this.getAttribute('data-product-id');
            // You would typically make an AJAX call here to add to cart
            alert(`Added product ${productId} to cart`);
            // Implement actual add to cart logic here
        });
    });
});