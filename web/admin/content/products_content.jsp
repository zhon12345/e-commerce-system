<%-- /admin/content/products_content.jsp --%>
<%-- This file contains only the content specific to the Product Management page --%>
<%@ page import="java.util.List, Model.Products, Model.Categories" %>
<h2 class="mb-3 border-bottom pb-2 text-body"><i class="fas fa-box-open"></i> Product Management</h2>

<button type="button" class="btn btn-primary mb-3 btn-add" data-bs-toggle="modal" data-bs-target="#addProductModal">
    <i class="fas fa-plus"></i> Add Product
</button>

<div class="table-responsive">
    <table class="table table-striped table-hover table-bordered align-middle">
        <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Description</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Category</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
                <%
                        List<Products> products = (List<Products>) request.getAttribute("products");
                        if (products != null) {
                                for (Products product : products) {
                %>
                <tr>
                        <td><%= product.getId() %></td>
                        <td><%= product.getName() %></td>
                        <td><%= product.getDescription() %></td>
                        <td><%= product.getPrice() %></td>
                        <td><%= product.getStock() %></td>
                        <td><%= product.getCategoryId().getName() %></td>
                        <td>
                                <a href="#" class="btn btn-sm btn-info action-btn" title="Edit" 
                                   onclick="editProduct(<%= product.getId() %>, '<%= product.getName() %>', '<%= product.getDescription() %>', 
                                   <%= product.getCategoryId().getId() %>, <%= product.getPrice() %>, <%= product.getStock() %>)">
                                   <i class="fas fa-edit"></i>
                                </a>
                                <a href="#" class="btn btn-sm btn-danger action-btn" title="Delete" 
                                   onclick="deleteProduct(<%= product.getId() %>)">
                                   <i class="fas fa-trash"></i>
                                </a>
                        </td>
                </tr>
                <%
                                }
                        } else {
                %>
                <tr>
                        <td colspan="7" class="text-center">No products found.</td>
                </tr>
                <% } %>
        </tbody>
    </table>
</div>

<div class="modal fade" id="addProductModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add New Product</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="addProductForm" action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data" >
                    <div class="mb-3">
                        <label for="productName" class="form-label">Product Name</label>
                        <input type="text" class="form-control" id="productName" name="addProductName" required>
                    </div>
                     <div class="mb-3">
                        <label for="productDescription" class="form-label">Product Description</label>
                        <input type="text" class="form-control" id="productDescription" name="addProductDescription" required>
                    </div>
                    <div class="mb-3">
                        <label for="productCategory" class="form-label">Category</label>
                        <select class="form-select" id="productCategory" name="addProductCategory" required>
                            <%
                                List<Categories> categories = (List<Categories>) request.getAttribute("categories");
                                if (categories != null) {
                                    for (Categories category : categories) {
                            %>
                                <option value="<%= category.getId() %>"><%= category.getName() %></option>
                            <% 
                                    }
                                } 
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="productPrice" class="form-label">Price</label>
                        <input type="number" class="form-control" id="productPrice" name="addProductPrice" required min="0" step="0.01">
                    </div>
                    <div class="mb-3">
                        <label for="productStock" class="form-label">Stock</label>
                        <input type="number" class="form-control" id="productStock" name="addProductStock" required min="0">
                    </div>
                    <div class="mb-3">
                        <label for="productPicture" class="form-label">Product Picture</label>
                        <input type="file" class="form-control" id="productPicture" name="addProductPicture" accept="image/*" multiple required>
                    </div>
               
                        <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                   <button type="submit" class="btn btn-primary" id="saveProductButton">Save Product</button>
                        </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editProductModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Edit Product</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="editProductForm" action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data">
                    <input type="hidden" id="editProductId" name="productId">
                    <input type="hidden" name="action" value="edit">
                    
                    <div class="mb-3">
                        <label for="editProductName" class="form-label">Product Name</label>
                        <input type="text" class="form-control" id="editProductName" name="productName" required>
                    </div>
                    <div class="mb-3">
                        <label for="editProductDescription" class="form-label">Product Description</label>
                        <input type="text" class="form-control" id="editProductDescription" name="productDescription" required>
                    </div>
                    <div class="mb-3">
                        <label for="editProductCategory" class="form-label">Category</label>
                        <select class="form-select" id="editProductCategory" name="productCategory" required>
                            <%
                                if (categories != null) {
                                    for (Categories category : categories) {
                            %>
                                <option value="<%= category.getId() %>"><%= category.getName() %></option>
                            <% 
                                    }
                                } 
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="editProductPrice" class="form-label">Price</label>
                        <input type="number" class="form-control" id="editProductPrice" name="productPrice" required min="0" step="0.01">
                    </div>
                    <div class="mb-3">
                        <label for="editProductStock" class="form-label">Stock</label>
                        <input type="number" class="form-control" id="editProductStock" name="productStock" required min="0">
                    </div>
                    <div class="mb-3">
                        <label for="editProductPicture" class="form-label">Product Pictures</label>
                        <div class="current-images mb-2">
                            <!-- Show current product images -->
                            <img src="${pageContext.request.contextPath}/assets/products/${product.name}/1.png" 
                                 class="img-thumbnail me-2" style="width: 100px; height: 100px; object-fit: cover;">
                        </div>
                        <input type="file" class="form-control" id="editProductPicture" 
                               name="productPicture" accept="image/*" multiple>
                        <small class="text-muted">Leave empty to keep current images</small>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Function to show toast messages
        function showToast(message, type) {
            const toastContainer = document.getElementById('toastContainer');
            if (!toastContainer) {
                const container = document.createElement('div');
                container.id = 'toastContainer';
                container.className = "toast-container position-fixed bottom-0 end-0 p-3";
                document.body.appendChild(container);
            }

            const toastDiv = document.createElement('div');
            toastDiv.className = 'toast';
            toastDiv.setAttribute('role', 'alert');
            toastDiv.setAttribute('aria-live', 'assertive');
            toastDiv.setAttribute('aria-atomic', 'true');

            let toastHeaderClass = 'bg-primary text-white';
            let icon = '<i class="fas fa-info-circle me-2"></i>';

            if (type === 'success') {
                toastHeaderClass = 'bg-success text-white';
                icon = '<i class="fas fa-check-circle me-2"></i>';
            } else if (type === 'error') {
                toastHeaderClass = 'bg-danger text-white';
                icon = '<i class="fas fa-exclamation-circle me-2"></i>';
            } else if (type === 'warning') {
                toastHeaderClass = 'bg-warning text-dark';
                icon = '<i class="fas fa-exclamation-triangle me-2"></i>';
            }

            toastDiv.innerHTML = `
                <div class="toast-header ${toastHeaderClass}">
                    ${icon}
                    <strong class="me-auto">${type.charAt(0).toUpperCase() + type.slice(1)}</strong>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">${message}</div>
            `;

            document.getElementById('toastContainer').appendChild(toastDiv);
            const toast = new bootstrap.Toast(toastDiv);
            toast.show();
        }

        // Function to edit a product
        window.editProduct = function(id, name, description, categoryId, price, stock) {
            // Set values in the edit form
            document.getElementById('editProductId').value = id;
            document.getElementById('editProductName').value = name;
            document.getElementById('editProductDescription').value = description;
            document.getElementById('editProductCategory').value = categoryId;
            document.getElementById('editProductPrice').value = price;
            document.getElementById('editProductStock').value = stock;

            // Show the modal
            new bootstrap.Modal(document.getElementById('editProductModal')).show();
        };

        // Function to delete a product
        window.deleteProduct = function(productId) {
            if (confirm('Are you sure you want to delete this product?')) {
                const formData = new FormData();
                formData.append('action', 'delete');
                formData.append('productId', productId);

                fetch('${pageContext.request.contextPath}/admin/products', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.text())
                .then(data => {
                    if (data === 'success') {
                        showToast('Product deleted successfully!', 'success');
                        window.location.reload();
                    } else {
                        showToast('Failed to delete product: ' + data, 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showToast('Error deleting product: ' + error.message, 'error');
                });
            }
        };

        // Add product form submission handler
        document.getElementById('saveProductButton').addEventListener('click', function() {
            var form = document.getElementById('addProductForm');
            if (form.checkValidity()) {
                // Get form values
                var productName = document.getElementById('productName').value;
                var productDescription = document.getElementById('productDescription').value;
                var productCategory = document.getElementById('productCategory').value;
                var productPrice = document.getElementById('productPrice').value;
                var productStock = document.getElementById('productStock').value;
                var productPicture = document.getElementById('productPicture').files[0]; // Get the file object

                // Create a FormData object to send the data, including the file
                var formData = new FormData();
                formData.append('addProductName', productName);
                formData.append('addProductDescription', productDescription);
                formData.append('addProductCategory', productCategory);
                formData.append('addProductPrice', productPrice);
                formData.append('addProductStock', productStock);
                formData.append('addProductPicture', productPicture); // Append the file

                //  Send data to your existing 'AddProducts' controller using AJAX.
                fetch('${pageContext.request.contextPath}/admin/products', {  //  Use the correct URL for your controller.
                    method: 'POST',
                    body: formData // Use formData instead of URLSearchParams
                })
                .then(response => response.text())
                .then(data => {
                    if (data.trim() === 'success') {
                        showToast('Product added successfully!', 'success');
                        var addProductModal = document.getElementById('addProductModal');
                        var modal = bootstrap.Modal.getInstance(addProductModal);
                        modal.hide();
                        form.reset();
                        window.location.reload();

                    } else {
                        showToast('Failed to add product: ' + data, 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showToast('Error adding product: ' + error.message, 'error');
                });
            } else {
                form.classList.add('was-validated');
            }
        });

        // Edit product form submission handler
        document.getElementById('editProductForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);

            fetch('${pageContext.request.contextPath}/admin/products', {
                method: 'POST',
                body: formData // Remove Content-Type header to allow multipart/form-data
            })
            .then(response => response.text())
            .then(data => {
                if (data.trim() === 'success') {
                    showToast('Product updated successfully!', 'success');
                    bootstrap.Modal.getInstance(document.getElementById('editProductModal')).hide();
                    window.location.reload();
                } else {
                    showToast('Failed to update product: ' + data, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Error updating product: ' + error.message, 'error');
            });
        });
    });
</script>
