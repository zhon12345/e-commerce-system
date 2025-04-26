package Model;

import jakarta.persistence.*;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "ORDER_ITEMS")
@XmlRootElement
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "QUANTITY")
    private int quantity;
    
    @Basic(optional = false)
    @Column(name = "UNIT_PRICE")
    private double unitPrice;
    
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Order order;
    
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Product product;

    // Constructors, getters, setters, equals, hashCode, toString methods...
}