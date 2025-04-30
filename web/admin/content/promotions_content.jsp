<%@ page import="java.util.List, Model.Promotions, java.text.SimpleDateFormat, java.util.Date" %>
<%
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today = dateFormat.format(new Date());
%>

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

<h2 class="mb-3 border-bottom pb-2 text-body"><i class="fas fa-tags"></i> Promotion Management</h2>

<button type="button" class="btn btn-primary mb-3" data-bs-toggle="modal" data-bs-target="#addPromotionModal">
    <i class="fas fa-plus"></i> Add Promotion
</button>

<div class="table-responsive">
    <table class="table table-striped table-hover table-bordered align-middle">
        <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Promo Code</th>
                <th>Discount (%)</th>
                <th>Valid From</th>
                <th>Valid To</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Promotions> promotionList = (List<Promotions>) request.getAttribute("promotionList");
                if (promotionList != null && !promotionList.isEmpty()) {
                    for (Promotions promo : promotionList) {
            %>
            <tr>
                <td><%= promo.getId() %></td>
                <td><%= promo.getPromoCode() %></td>
                <td><%= promo.getDiscount() %>%</td>
                <td><%= dateFormat.format(promo.getValidFrom()) %></td>
                <td><%= dateFormat.format(promo.getValidTo()) %></td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="editPromotion('<%= promo.getId() %>', '<%= promo.getPromoCode() %>', '<%= promo.getDiscount() %>', '<%= dateFormat.format(promo.getValidFrom()) %>', '<%= dateFormat.format(promo.getValidTo()) %>')" data-bs-toggle="modal" data-bs-target="#editPromotionModal">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deletePromotion(<%= promo.getId() %>)">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
            <% 
                    }
                } else {
            %>
            <tr>
                <td colspan="6" class="text-center">No promotions found</td>
            </tr>
            <% } %>
        </tbody>
    </table>
</div>

<!-- Add Promotion Modal -->
<div class="modal fade" id="addPromotionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add New Promotion</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form action="${pageContext.request.contextPath}/admin/managepromo" method="POST">
                    <input type="hidden" name="action" value="add">
                    
                    <div class="mb-3">
                        <label for="promoCode" class="form-label">Promo Code</label>
                        <input type="text" class="form-control" id="promoCode" name="promoCode" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="discount" class="form-label">Discount (%)</label>
                        <input type="number" class="form-control" id="discount" name="discount" min="0" max="100" step="0.01" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="validFrom" class="form-label">Valid From</label>
                        <input type="date" class="form-control" id="validFrom" name="validFrom" 
                               min="<%= today %>" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="validTo" class="form-label">Valid To</label>
                        <input type="date" class="form-control" id="validTo" name="validTo"
                               min="<%= today %>" required>
                    </div>
                    
                    <div class="modal-footer px-0 pb-0">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Add Promotion</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Edit Promotion Modal -->
<div class="modal fade" id="editPromotionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Edit Promotion</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form action="${pageContext.request.contextPath}/admin/managepromo" method="POST">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" id="editId">
                    
                    <div class="mb-3">
                        <label for="editPromoCode" class="form-label">Promo Code</label>
                        <input type="text" class="form-control" id="editPromoCode" name="promoCode" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="editDiscount" class="form-label">Discount (%)</label>
                        <input type="number" class="form-control" id="editDiscount" name="discount" min="0" max="100" step="0.01" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="editValidFrom" class="form-label">Valid From</label>
                        <input type="date" class="form-control" id="editValidFrom" name="validFrom"
                               min="<%= today %>" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="editValidTo" class="form-label">Valid To</label>
                        <input type="date" class="form-control" id="editValidTo" name="validTo"
                               min="<%= today %>" required>
                    </div>
                    
                    <div class="modal-footer px-0 pb-0">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Update Promotion</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function editPromotion(id, promoCode, discount, validFrom, validTo) {
    document.getElementById('editId').value = id;
    document.getElementById('editPromoCode').value = promoCode;
    document.getElementById('editDiscount').value = discount;
    document.getElementById('editValidFrom').value = validFrom;
    document.getElementById('editValidTo').value = validTo;
}

function deletePromotion(id) {
    if (confirm('Are you sure you want to delete this promotion?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/admin/managepromo';
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'delete';
        
        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'id';
        idInput.value = id;
        
        form.appendChild(actionInput);
        form.appendChild(idInput);
        document.body.appendChild(form);
        form.submit();
    }
}

// Add date validation
document.addEventListener('DOMContentLoaded', function() {
    const today = new Date().toISOString().split('T')[0];
    
    // Set minimum dates for add form
    document.getElementById('validFrom').min = today;
    document.getElementById('validTo').min = today;
    
    // Set minimum dates for edit form
    document.getElementById('editValidFrom').min = today;
    document.getElementById('editValidTo').min = today;
    
    // Validate dates when changed
    document.getElementById('validFrom').addEventListener('change', function() {
        document.getElementById('validTo').min = this.value;
    });
    
    document.getElementById('editValidFrom').addEventListener('change', function() {
        document.getElementById('editValidTo').min = this.value;
    });
});
</script>
