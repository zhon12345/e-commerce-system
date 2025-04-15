/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author zhon12345
 */
@Entity
@Table(name = "CUSTOMERS")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Customers.findAll", query = "SELECT c FROM Customers c"),
	@NamedQuery(name = "Customers.findById", query = "SELECT c FROM Customers c WHERE c.id = :id"),
	@NamedQuery(name = "Customers.findByUsername", query = "SELECT c FROM Customers c WHERE c.username = :username"),
	@NamedQuery(name = "Customers.findByEmail", query = "SELECT c FROM Customers c WHERE c.email = :email"),
	@NamedQuery(name = "Customers.findByPassword", query = "SELECT c FROM Customers c WHERE c.password = :password")})
public class Customers implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Basic(optional = false)
  @Column(name = "USERNAME")
	private String username;
	@Basic(optional = false)
  @Column(name = "EMAIL")
	private String email;
	@Basic(optional = false)
  @Column(name = "PASSWORD")
	private String password;
	@OneToMany(mappedBy = "customerId")
	private List<Reviews> reviewsList;
	@OneToMany(mappedBy = "customerId")
	private List<Orders> ordersList;
	@OneToMany(mappedBy = "customerId")
	private List<Paymentinfo> paymentinfoList;
	@OneToMany(mappedBy = "customerId")
	private List<Customeraddresses> customeraddressesList;
	@OneToMany(mappedBy = "customerId")
	private List<Cart> cartList;

	public Customers() {
	}

	public Customers(Integer id) {
		this.id = id;
	}

	public Customers(Integer id, String username, String email, String password) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@XmlTransient
	public List<Reviews> getReviewsList() {
		return reviewsList;
	}

	public void setReviewsList(List<Reviews> reviewsList) {
		this.reviewsList = reviewsList;
	}

	@XmlTransient
	public List<Orders> getOrdersList() {
		return ordersList;
	}

	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
	}

	@XmlTransient
	public List<Paymentinfo> getPaymentinfoList() {
		return paymentinfoList;
	}

	public void setPaymentinfoList(List<Paymentinfo> paymentinfoList) {
		this.paymentinfoList = paymentinfoList;
	}

	@XmlTransient
	public List<Customeraddresses> getCustomeraddressesList() {
		return customeraddressesList;
	}

	public void setCustomeraddressesList(List<Customeraddresses> customeraddressesList) {
		this.customeraddressesList = customeraddressesList;
	}

	@XmlTransient
	public List<Cart> getCartList() {
		return cartList;
	}

	public void setCartList(List<Cart> cartList) {
		this.cartList = cartList;
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
		if (!(object instanceof Customers)) {
			return false;
		}
		Customers other = (Customers) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Customers[ id=" + id + " ]";
	}

}
