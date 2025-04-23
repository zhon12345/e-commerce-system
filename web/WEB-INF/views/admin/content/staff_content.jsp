<%-- /WEB-INF/views/admin/content/staff_content.jsp --%>
<%-- This file contains only the content specific to the Staff Management page --%>

<h2 class="mb-3 border-bottom pb-2 text-body"><i class="fas fa-user-shield"></i> Staff Management</h2>

<button type="button" class="btn btn-primary mb-3 btn-add" data-bs-toggle="modal" data-bs-target="#addStaffModal">
   <i class="fas fa-plus"></i> Add Staff
</button>

<div class="table-responsive">
    <table class="table table-striped table-hover table-bordered align-middle">
        <thead class="table-light">
           <tr>
               <th>ID</th>
               <th>Username</th>
               <th>Email</th>
               <th>Role</th>
               <th>Actions</th>
           </tr>
       </thead>
        <tbody>
            <tr>
               <td>3</td>
               <td>staffREX</td>
               <td>staff1@example.com</td>
               <td>Staff</td>
               <td>
                   <a href="#" class="btn btn-sm btn-info action-btn" title="Edit"><i class="fas fa-edit"></i></a>
                   <a href="#" class="btn btn-sm btn-danger action-btn" title="Delete" onclick="if(confirm('Are you sure?')){ showToast('Staff member deleted successfully.', 'success'); } return false;"><i class="fas fa-trash"></i></a>
               </td>
           </tr>
            <tr>
               <td>4</td>
               <td>staff2</td>
               <td>staff2@example.com</td>
               <td>Staff</td>
               <td>
                   <a href="#" class="btn btn-sm btn-info action-btn" title="Edit"><i class="fas fa-edit"></i></a>
                   <a href="#" class="btn btn-sm btn-danger action-btn" title="Delete" onclick="if(confirm('Are you sure?')){ showToast('Staff member deleted successfully.', 'success'); } return false;"><i class="fas fa-trash"></i></a>
               </td>
           </tr>
            </tbody>
    </table>
</div>
