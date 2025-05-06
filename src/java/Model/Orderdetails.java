package Model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "ORDERDETAILS")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Orderdetails.findAll", query = "SELECT o FROM Orderdetails o"),
	@NamedQuery(name = "Orderdetails.findById", query = "SELECT o FROM Orderdetails o WHERE o.id = :id"),
	@NamedQuery(name = "Orderdetails.findByQuantity", query = "SELECT o FROM Orderdetails o WHERE o.quantity = :quantity"),
	@NamedQuery(name = "Orderdetails.findByPrice", query = "SELECT o FROM Orderdetails o WHERE o.price = :price")})
public class Orderdetails implements Serializable {

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
  @Column(name = "PRICE")
	private BigDecimal price;
	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Orders orderId;
	@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
  @ManyToOne
	private Products productId;

	public Orderdetails() {
	}

	public Orderdetails(Integer id) {
		this.id = id;
	}

	public Orderdetails(Integer id, int quantity, BigDecimal price) {
		this.id = id;
		this.quantity = quantity;
		this.price = price;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Orders getOrderId() {
		return orderId;
	}

	public void setOrderId(Orders orderId) {
		this.orderId = orderId;
	}

	public Products getProductId() {
		return productId;
	}

	public void setProductId(Products productId) {
		this.productId = productId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Orderdetails)) {
			return false;
		}
		Orderdetails other = (Orderdetails) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Orderdetails[ id=" + id + " ]";
	}

}
