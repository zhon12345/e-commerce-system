package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author YapSJin
 */

public class ProductCRUDFrame extends JFrame {
    private ProductDAO productDAO;
    private JTable productTable;
    private ProductTableModel tableModel;

    public ProductCRUDFrame() {
        try {
            productDAO = new ProductDAO();
            initializeUI();
            loadProducts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeUI() {
        setTitle("Admin Product Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        tableModel = new ProductTableModel();
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton refreshButton = new JButton("Refresh");

        // Set layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add event listeners
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteProduct());
        refreshButton.addActionListener(e -> loadProducts());
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            tableModel.setProducts(products);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddProductDialog() {
        ProductDialog dialog = new ProductDialog(this, "Add New Product", true);
        dialog.setVisible(true);
        
        if (dialog.isSubmitted()) {
            try {
                productDAO.addProduct(dialog.getProduct());
                loadProducts();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = tableModel.getProductAt(selectedRow);
        ProductDialog dialog = new ProductDialog(this, "Edit Product", true, product);
        dialog.setVisible(true);
        
        if (dialog.isSubmitted()) {
            try {
                productDAO.updateProduct(dialog.getProduct());
                loadProducts();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this product?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Product product = tableModel.getProductAt(selectedRow);
                productDAO.deleteProduct(product.getId());
                loadProducts();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductCRUDFrame frame = new ProductCRUDFrame();
            frame.setVisible(true);
        });
    }
}

class ProductTableModel extends AbstractTableModel {
    private List<Product> products = new ArrayList<>();
    private String[] columnNames = {"ID", "Name", "Description", "Price", "Stock", "Category ID"};

    public void setProducts(List<Product> products) {
        this.products = products;
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        return products.get(row);
    }

    @Override
    public int getRowCount() {
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = products.get(rowIndex);
        switch (columnIndex) {
            case 0: return product.getId();
            case 1: return product.getName();
            case 2: return product.getDescription();
            case 3: return product.getPrice();
            case 4: return product.getStock();
            case 5: return product.getCategoryId();
            default: return null;
        }
    }
}

class ProductDialog extends JDialog {
    private boolean submitted = false;
    private Product product;
    
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField stockField;
    private JTextField categoryIdField;

    public ProductDialog(JFrame parent, String title, boolean modal) {
        this(parent, title, modal, new Product(0, "", "", 0.0, 0, 0));
    }

    public ProductDialog(JFrame parent, String title, boolean modal, Product product) {
        super(parent, title, modal);
        this.product = product;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField(product.getName());
        panel.add(nameField);

        panel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(product.getDescription());
        panel.add(new JScrollPane(descriptionArea));

        panel.add(new JLabel("Price:"));
        priceField = new JTextField(String.valueOf(product.getPrice()));
        panel.add(priceField);

        panel.add(new JLabel("Stock:"));
        stockField = new JTextField(String.valueOf(product.getStock()));
        panel.add(stockField);

        panel.add(new JLabel("Category ID:"));
        categoryIdField = new JTextField(String.valueOf(product.getCategoryId()));
        panel.add(categoryIdField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            submitted = true;
            updateProductFromFields();
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateProductFromFields() {
        product.setName(nameField.getText());
        product.setDescription(descriptionArea.getText());
        product.setPrice(Double.parseDouble(priceField.getText()));
        product.setStock(Integer.parseInt(stockField.getText()));
        product.setCategoryId(Integer.parseInt(categoryIdField.getText()));
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Product getProduct() {
        return product;
    }
}