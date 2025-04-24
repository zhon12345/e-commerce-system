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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author zhon12345
 */
@Entity
@Table(name = "CART")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Cart.findAll", query = "SELECT c FROM Cart c"),
	@NamedQuery(name = "Cart.findById", query = "SELECT c FROM Cart c WHERE c.id = :id"),
	@NamedQuery(name = "Cart.findByQuantity", query = "SELECT c FROM Cart c WHERE c.quantity = :quantity"),
	@NamedQuery(name = "Cart.findByAddedDate", query = "SELECT c FROM Cart c WHERE c.addedDate = :addedDate")})
public class Cart implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Basic(optional = false)
  @Column(name = "QUANTITY")
	private int quantity;
	@Column(name = "ADDED_DATE")
  @Temporal(TemporalType.TIMESTAMP)
	private Date addedDate;
	@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
  @ManyToOne
	private Products productId;
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Users userId;

	public Cart() {
	}

	public Cart(Integer id) {
		this.id = id;
	}

	public Cart(Integer id, int quantity) {
		this.id = id;
		this.quantity = quantity;
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

	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public Products getProductId() {
		return productId;
	}

	public void setProductId(Products productId) {
		this.productId = productId;
	}

	public Users getUserId() {
		return userId;
	}

	public void setUserId(Users userId) {
		this.userId = userId;
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
		if (!(object instanceof Cart)) {
			return false;
		}
		Cart other = (Cart) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Cart[ id=" + id + " ]";
	}

}
