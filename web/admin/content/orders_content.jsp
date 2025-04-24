<%-- /WEB-INF/views/admin/content/orders_content.jsp --%>
<%-- This file contains only the content specific to the Order Management page --%>

<h2 class="mb-3 border-bottom pb-2 text-body"><i class="fas fa-receipt"></i> Order Management</h2>

<div class="table-responsive">
    <table class="table table-striped table-hover table-bordered align-middle">
        <thead class="table-light">
           <tr>
               <th>Order ID</th>
               <th>Customer</th>
               <th>Order Date</th>
               <th>Total Price</th>
               <th>Status</th>
               <th>Actions</th>
           </tr>
       </thead>
        <tbody>
            <tr>
                <td>101</td>
                <td>customerREX</td>
                <td>2025-04-20 10:30</td>
                <td>RM 125.49</td>
                <td><span class="badge bg-warning text-dark">Processing</span></td>
                <td>
                    <a href="#" class="btn btn-sm btn-success action-btn" title="View Details"><i class="fas fa-eye"></i></a>
                    <a href="#" class="btn btn-sm btn-info action-btn" title="Update Status"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn btn-sm btn-danger action-btn" title="Cancel Order" onclick="if(confirm('Are you sure?')){ showToast('Order cancelled successfully.', 'warning'); } return false;"><i class="fas fa-times-circle"></i></a>
                </td>
            </tr>
             <tr>
                <td>102</td>
                <td>customer2</td>
                <td>2025-04-21 09:15</td>
                <td>RM 75.00</td>
                <td><span class="badge bg-info">Shipped</span></td>
                <td>
                    <a href="#" class="btn btn-sm btn-success action-btn" title="View Details"><i class="fas fa-eye"></i></a>
                    <a href="#" class="btn btn-sm btn-info action-btn" title="Update Status"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn btn-sm btn-danger action-btn" title="Cancel Order" onclick="if(confirm('Are you sure?')){ showToast('Order cancelled successfully.', 'warning'); } return false;"><i class="fas fa-times-circle"></i></a>
                </td>
            </tr>
             </tbody>
    </table>
</div>
