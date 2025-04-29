<%-- /admin/content/products_content.jsp --%>
<%-- This file contains only the content specific to the Product Management page --%>
<%@ page import="java.util.List, Model.Products, java.sql.*" %>
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
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    // 1. Load the JDBC driver.  Use the Derby driver.
                    Class.forName("org.apache.derby.jdbc.ClientDriver"); // For Derby Client

                    // 2. Establish the database connection.  Replace with your Derby database URL, username, and password if needed.
                    String jdbcUrl = "jdbc:derby://localhost:1527/e-commerce-system"; // Replace
                    String username = "nbuser";  // Replace, often "user" for Derby
                    String password = "nbuser";  // Replace, often "password" for Derby
                    connection = DriverManager.getConnection(jdbcUrl, username, password);

                    // 3. Create a prepared statement to fetch the product data.
                    String sql = "SELECT p.id, p.name, p.description, p.price, p.stock, c.name as category_name " +
                                 "FROM Products p INNER JOIN Categories c ON p.category_id = c.id";
                    preparedStatement = connection.prepareStatement(sql);

                    // 4. Execute the query and get the result set.
                    resultSet = preparedStatement.executeQuery();

                    // 5. Process the result set and display the data in the table.
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String description = resultSet.getString("description");
                        double price = resultSet.getDouble("price");
                        int stock = resultSet.getInt("stock");
                        String categoryName = resultSet.getString("category_name");
            %>
            <tr>
                <td><%= id %></td>
                <td><%= name %></td>
                <td><%= description %></td>
                <td><%= price %></td>
                <td><%= stock %></td>
                <td><%= categoryName %></td>
                <td>
                    <a href="#" class="btn btn-sm btn-info action-btn" title="Edit"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn btn-sm btn-danger action-btn" title="Delete"
                       onclick="if(confirm('Are you sure?')){ showToast('Product deleted successfully.', 'success'); } return false;"><i
                            class="fas fa-trash"></i></a>
                </td>
            </tr>
            <%
                    }
                } catch (ClassNotFoundException e) {
                    // Handle the exception if the JDBC driver is not found.
                    out.println("<tr><td colspan='7' class='text-center text-danger'>Error: Could not load database driver. " + e.getMessage() + "</td></tr>");
                    e.printStackTrace();
                } catch (SQLException e) {
                    // Handle any SQL errors.
                    out.println("<tr><td colspan='7' class='text-center text-danger'>Error: Database error. " + e.getMessage() + "</td></tr>");
                    e.printStackTrace();
                } finally {
                    // 6. Close database resources (result set, statement, connection) in a finally block
                    //    to ensure they are always closed, even if exceptions occur.
                    try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
                    try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
                    try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
                }
            %>
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
                <form id="addProductForm">
                    <div class="mb-3">
                        <label for="productName" class="form-label">Product Name</label>
                        <input type="text" class="form-control" id="productName" name="productName" required>
                    </div>
                     <div class="mb-3">
                        <label for="productDescription" class="form-label">Product Description</label>
                        <input type="text" class="form-control" id="productDescription" name="productDescription" required>
                    </div>
                    <div class="mb-3">
                        <label for="productCategory" class="form-label">Category</label>
                        <select class="form-select" id="productCategory" name="productCategory" required>
                            <%
                                // Fetch categories from the database and populate the dropdown.
                                try {
                                    Class.forName("org.apache.derby.jdbc.ClientDriver");  //Or EmbeddedDriver
                                    String categoryUrl = "jdbc:derby://localhost:1527/e-commerce-system";
                                    String categoryUsername = "nbuser";
                                    String categoryPassword = "nbuser";
                                    connection = DriverManager.getConnection(categoryUrl, categoryUsername, categoryPassword);
                                    String categorySql = "SELECT id, name FROM Categories"; // Corrected table name
                                    preparedStatement = connection.prepareStatement(categorySql);
                                    resultSet = preparedStatement.executeQuery();
                                    while (resultSet.next()) {
                                        int categoryId = resultSet.getInt("id");
                                        String categoryName = resultSet.getString("name");
                            %>
                                        <option value="<%= categoryId %>"><%= categoryName %></option>
                            <%
                                    }
                                } catch (ClassNotFoundException | SQLException e) {
                                    out.println("<option value=''>Error loading categories</option>");
                                     e.printStackTrace();
                                } finally {
                                    try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
                                    try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
                                    try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
                                }
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="productPrice" class="form-label">Price</label>
                        <input type="number" class="form-control" id="productPrice" name="productPrice" required min="0" step="0.01">
                    </div>
                    <div class="mb-3">
                        <label for="productStock" class="form-label">Stock</label>
                        <input type="number" class="form-control" id="productStock" name="productStock" required min="0">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id="saveProductButton">Save Product</button>
            </div>
        </div>
    </div>
</div>
<script>
    document.getElementById('saveProductButton').addEventListener('click', function() {
        var form = document.getElementById('addProductForm');
        if (form.checkValidity()) {
            // Get form values
            var productName = document.getElementById('productName').value;
            var productDescription = document.getElementById('productDescription').value;
            var productCategory = document.getElementById('productCategory').value;
            var productPrice = document.getElementById('productPrice').value;
            var productStock = document.getElementById('productStock').value;

            // Perform further processing:
            //   1.  Send data to a servlet using AJAX to insert into the database.
            //   2.  Handle the response from the servlet (success/failure).
            //   3.  Update the product table in the JSP page dynamically (without a full page reload) if the insertion is successful.
            //   4.  Display a success/error message to the user.

            // Example using fetch API (Modern approach):
            fetch('/your_servlet_name', {  // Replace '/your_servlet_name'
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded', // Important for form data
                },
                body: new URLSearchParams({  // Convert form data to URL-encoded format
                    productName: productName,
                    productDescription: productDescription,
                    productCategory: productCategory,
                    productPrice: productPrice,
                    productStock: productStock
                })
            })
            .then(response => response.text()) // Or response.json() if your servlet sends JSON
            .then(data => {
                if (data.trim() === 'success') { //  Check the response from the servlet
                    showToast('Product added successfully!', 'success');
                    //  Close the modal
                    var addProductModal = document.getElementById('addProductModal');
                    var modal = bootstrap.Modal.getInstance(addProductModal);
                    modal.hide();

                    // Clear the form
                    form.reset();

                    //  Dynamically add a new row to the table (using DOM manipulation)
                    //  You'll need to fetch the new product ID from the database (usually done in the servlet)
                    //  and include it in the response.  For this example, I'll assume the servlet
                    //  returns the new product ID as part of the 'success' message, e.g., "success:123"
                    //  (where 123 is the new product ID).  A better approach is to send a JSON object back from the server.

                    //  For now, assume we just added a product with a known ID.
                    //  In real application, you would get this ID from the server's response.
                    let newProductId = -1;
                    // split data
                    if(data.startsWith("success:")){
                         newProductId = parseInt(data.split(":")[1]);
                    }

                    if (newProductId > 0) { //Check that we have a valid ID.
                        let newRow = document.createElement('tr');
                        newRow.innerHTML = `
                            <td>${newProductId}</td>
                            <td>${productName}</td>
                            <td>${productDescription}</td>
                            <td>${productPrice}</td>
                            <td>${productStock}</td>
                            <td>${document.getElementById('productCategory').options[document.getElementById('productCategory').selectedIndex].text}</td>
                            <td>
                                <a href="#" class="btn btn-sm btn-info action-btn" title="Edit"><i class="fas fa-edit"></i></a>
                                <a href="#" class="btn btn-sm btn-danger action-btn" title="Delete"
                                   onclick="if(confirm('Are you sure?')){ showToast('Product deleted successfully.', 'success'); } return false;"><i
                                        class="fas fa-trash"></i></a>
                            </td>
                        `;
                        document.querySelector('.table tbody').appendChild(newRow);
                     } else {
                         //If newProductId is not greater than 0, then there was an error.
                         //Display error message.
                         showToast('Product added, but ID was not retrieved.  Please refresh the page.', 'warning');
                     }


                } else {
                    showToast('Failed to add product: ' + data, 'error'); // Show the error message from the servlet
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Error adding product: ' + error.message, 'error');
            });
        } else {
            //  The form is not valid; display the browser's validation messages.
            form.classList.add('was-validated');
        }
    });



    function showToast(message, type) {
        const toastContainer = document.getElementById('toastContainer');  // Make sure this ID exists in your HTML
        if (!toastContainer) {
            // Create the container if it doesn't exist
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
        } else if (type === 'warning'){
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

        document.getElementById('toastContainer').appendChild(toastDiv);  // Append toast

        const toast = new bootstrap.Toast(toastDiv);
        toast.show();
    }
</script>
