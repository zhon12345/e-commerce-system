<%@ page import="java.util.List, Model.Products, Model.Categories" %>

<% if (session.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <%= session.getAttribute("error") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("error"); %>
<% } %>

<% if (session.getAttribute("success") != null) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <%= session.getAttribute("success") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("success"); %>
<% } %>

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
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
        <%
            List<Products> productList = (List<Products>) request.getAttribute("productList");
            if (productList != null && !productList.isEmpty()) {
                for (Products product : productList) {
                    String productFolder = product.getName().replaceAll("\\s+", "_");
        %>
            <tr>
                <td><%= product.getId() %></td>
                <td><%= product.getName() %></td>
                <td><%= product.getDescription() != null ? product.getDescription() : "" %></td>
                <td><%= product.getCategoryId() != null ? product.getCategoryId().getName() : "Uncategorized" %></td>
                <td><%= product.getPrice() %></td>
                <td><%= product.getStock() %></td>
                <td>
                    <button class="btn btn-sm btn-info action-btn" title="Edit" onclick="editProduct(<%= product.getId() %>, '<%= product.getName().replace("'", "\\'") %>', '<%= product.getDescription() != null ? product.getDescription().replace("'", "\\'") : "" %>', '<%= product.getCategoryId() != null ? product.getCategoryId().getId() : "" %>', '<%= product.getPrice() %>', '<%= product.getStock() %>')">
                        <i class="fas fa-edit"></i>
                    </button>
                    <form style="display:inline;" action="${pageContext.request.contextPath}/admin/products" method="post" onsubmit="return confirm('Are you sure you want to delete this product?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="productId" value="<%= product.getId() %>">
                        <button type="submit" class="btn btn-sm btn-danger action-btn" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </form>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr>
                <td colspan="7" class="text-center">No products found</td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<!-- Add Product Modal -->
<div class="modal fade" id="addProductModal" tabindex="-1" aria-labelledby="addProductModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addProductModalLabel">Add New Product</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="create">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="productName" class="form-label">Product Name</label>
                        <input type="text" class="form-control" id="productName" name="name" required>
                    </div>
                    <div class="mb-3">
                        <label for="productDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="productDescription" name="description" rows="3"></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="productCategory" class="form-label">Category</label>
                        <select class="form-select" id="productCategory" name="categoryId" required>
                            <option value="">Select a category</option>
                            <%
                                List<Categories> categoryList = (List<Categories>) request.getAttribute("categoryList");
                                if (categoryList != null) {
                                    for (Categories category : categoryList) {
                            %>
                                <option value="<%= category.getId() %>"><%= category.getName() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="productPrice" class="form-label">Price (RM)</label>
                        <input type="text" class="form-control" id="productPrice" name="price" required>
                    </div>
                    <div class="mb-3">
                        <label for="productStock" class="form-label">Stock</label>
                        <input type="text" class="form-control" id="productStock" name="stock" required>
                    </div>
                    <div class="mb-3">
                        <label for="productImage" class="form-label">Product Image</label>
                        <input type="file" class="form-control" id="productImage" name="productImage" accept="image/jpeg,image/jpg,image/png">
                        <div class="form-text">Upload a JPG or PNG image (max 5MB)</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Add Product</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Product Modal -->
<div class="modal fade" id="editProductModal" tabindex="-1" aria-labelledby="editProductModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editProductModalLabel">Edit Product</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editProductId" name="productId">
                <input type="hidden" id="editOldProductName" name="oldProductName">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="editProductName" class="form-label">Product Name</label>
                        <input type="text" class="form-control" id="editProductName" name="name" required>
                    </div>
                    <div class="mb-3">
                        <label for="editProductDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="editProductDescription" name="description" rows="3"></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="editProductCategory" class="form-label">Category</label>
                        <select class="form-select" id="editProductCategory" name="categoryId" required>
                            <option value="">Select a category</option>
                            <%
                                if (categoryList != null) {
                                    for (Categories category : categoryList) {
                            %>
                                <option value="<%= category.getId() %>"><%= category.getName() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="editProductPrice" class="form-label">Price (RM)</label>
                        <input type="text" class="form-control" id="editProductPrice" name="price" required>
                    </div>
                    <div class="mb-3">
                        <label for="editProductStock" class="form-label">Stock</label>
                        <input type="text" class="form-control" id="editProductStock" name="stock" required>
                    </div>
                    <div class="mb-3">
                        <label for="editProductImage" class="form-label">Product Image</label>
                        <input type="file" class="form-control" id="editProductImage" name="productImage" accept="image/jpeg,image/jpg,image/png">
                        <div class="form-text">Upload a new JPG or PNG image or leave empty to keep the current one</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Product</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function editProduct(id, name, description, categoryId, price, stock) {
        document.getElementById('editProductId').value = id;
        document.getElementById('editProductName').value = name;
        document.getElementById('editOldProductName').value = name;
        document.getElementById('editProductDescription').value = description;
        document.getElementById('editProductCategory').value = categoryId;
        document.getElementById('editProductPrice').value = price;
        document.getElementById('editProductStock').value = stock;

        const editModal = new bootstrap.Modal(document.getElementById('editProductModal'));
        editModal.show();
    }

    document.addEventListener('DOMContentLoaded', function() {
        ['productPrice', 'editProductPrice'].forEach(id => {
            const priceInput = document.getElementById(id);

            priceInput.addEventListener('input', function() {
                let value = this.value.replace(/[^0-9.]/g, '');

                const decimalCount = (value.match(/\./g) || []).length;
                if (decimalCount > 1) {
                    const firstDecimalIndex = value.indexOf('.');
                    value = value.substring(0, firstDecimalIndex + 1) +
                           value.substring(firstDecimalIndex + 1).replace(/\./g, '');
                }

                if (value.includes('.')) {
                    const parts = value.split('.');
                    if (parts[1].length > 2) {
                        parts[1] = parts[1].substring(0, 2);
                        value = parts.join('.');
                    }
                }

                this.value = value;
            });
        });

        ['productStock', 'editProductStock'].forEach(id => {
            const stockInput = document.getElementById(id);

            stockInput.addEventListener('input', function() {
                this.value = this.value.replace(/[^0-9]/g, '');
            });
        });

        ['productImage', 'editProductImage'].forEach(id => {
            const imageInput = document.getElementById(id);

            imageInput.addEventListener('change', function() {
                if (this.files.length > 0) {
                    const file = this.files[0];
                    if (file.size > 5 * 1024 * 1024) {
                        alert('Image file is too large. Maximum size is 5MB.');
                        this.value = '';
                        return;
                    }

                    const validTypes = ['image/jpeg', 'image/jpg', 'image/png'];
                    if (!validTypes.includes(file.type)) {
                        alert('Invalid file type. Please upload a JPG or PNG image.');
                        this.value = '';
                        return;
                    }
                }
            });
        });
    });
</script>
