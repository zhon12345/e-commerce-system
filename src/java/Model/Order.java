package Model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "ORDERS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Order.findAll", query = "SELECT o FROM Order o"),
    @NamedQuery(name = "Order.findById", query = "SELECT o FROM Order o WHERE o.id = :id"),
    @NamedQuery(name = "Order.findByOrderDate", query = "SELECT o FROM Order o WHERE o.orderDate = :orderDate"),
    @NamedQuery(name = "Order.findByTotalAmount", query = "SELECT o FROM Order o WHERE o.totalAmount = :totalAmount"),
    @NamedQuery(name = "Order.findByStatus", query = "SELECT o FROM Order o WHERE o.status = :status")})
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "ORDER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    
    @Basic(optional = false)
    @Column(name = "TOTAL_AMOUNT")
    private double totalAmount;
    
    @Column(name = "STATUS")
    private String status;
    
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemsList;
    
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Users user;
    
    @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID")
    @ManyToOne
    private Addresses address;

    public Order() {
    }

    public Order(Integer id) {
        this.id = id;
    }

    // Getters and setters for all fields
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    // ... (continue with getters/setters for all fields)

    @XmlTransient
    public List<OrderItem> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItem> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Addresses getAddress() {
        return address;
    }

    public void setAddress(Addresses address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Order)) {
            return false;
        }
        Order other = (Order) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Model.Order[ id=" + id + " ]";
    }
}