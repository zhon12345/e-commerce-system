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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author yjee0
 */
@Entity
@Table(name = "ADDRESSES")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Addresses.findAll", query = "SELECT a FROM Addresses a"),
	@NamedQuery(name = "Addresses.findById", query = "SELECT a FROM Addresses a WHERE a.id = :id"),
	@NamedQuery(name = "Addresses.findByReceiverName", query = "SELECT a FROM Addresses a WHERE a.receiverName = :receiverName"),
	@NamedQuery(name = "Addresses.findByContactNumber", query = "SELECT a FROM Addresses a WHERE a.contactNumber = :contactNumber"),
	@NamedQuery(name = "Addresses.findByAddress1", query = "SELECT a FROM Addresses a WHERE a.address1 = :address1"),
	@NamedQuery(name = "Addresses.findByAddress2", query = "SELECT a FROM Addresses a WHERE a.address2 = :address2"),
	@NamedQuery(name = "Addresses.findByCity", query = "SELECT a FROM Addresses a WHERE a.city = :city"),
	@NamedQuery(name = "Addresses.findByState", query = "SELECT a FROM Addresses a WHERE a.state = :state"),
	@NamedQuery(name = "Addresses.findByPostalCode", query = "SELECT a FROM Addresses a WHERE a.postalCode = :postalCode"),
	@NamedQuery(name = "Addresses.findByIsArchived", query = "SELECT a FROM Addresses a WHERE a.isArchived = :isArchived")})
public class Addresses implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Basic(optional = false)
  @Column(name = "RECEIVER_NAME")
	private String receiverName;
	@Basic(optional = false)
  @Column(name = "CONTACT_NUMBER")
	private String contactNumber;
	@Basic(optional = false)
  @Column(name = "ADDRESS1")
	private String address1;
	@Column(name = "ADDRESS2")
	private String address2;
	@Basic(optional = false)
  @Column(name = "CITY")
	private String city;
	@Basic(optional = false)
  @Column(name = "STATE")
	private String state;
	@Basic(optional = false)
  @Column(name = "POSTAL_CODE")
	private String postalCode;
	@Column(name = "IS_ARCHIVED")
	private Boolean isArchived;
	@OneToMany(mappedBy = "addressId")
	private List<Orders> ordersList;
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Users userId;

	public Addresses() {
	}

	public Addresses(Integer id) {
		this.id = id;
	}

	public Addresses(Integer id, String receiverName, String contactNumber, String address1, String city, String state, String postalCode) {
		this.id = id;
		this.receiverName = receiverName;
		this.contactNumber = contactNumber;
		this.address1 = address1;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Boolean getIsArchived() {
		return isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}

	@XmlTransient
	public List<Orders> getOrdersList() {
		return ordersList;
	}

	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
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
		if (!(object instanceof Addresses)) {
			return false;
		}
		Addresses other = (Addresses) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Addresses[ id=" + id + " ]";
	}

}
