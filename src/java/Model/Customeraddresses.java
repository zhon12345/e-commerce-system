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
 * @author zhon12345
 */
@Entity
@Table(name = "CUSTOMERADDRESSES")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Customeraddresses.findAll", query = "SELECT c FROM Customeraddresses c"),
	@NamedQuery(name = "Customeraddresses.findById", query = "SELECT c FROM Customeraddresses c WHERE c.id = :id"),
	@NamedQuery(name = "Customeraddresses.findByReceiverName", query = "SELECT c FROM Customeraddresses c WHERE c.receiverName = :receiverName"),
	@NamedQuery(name = "Customeraddresses.findByPhoneNumber", query = "SELECT c FROM Customeraddresses c WHERE c.phoneNumber = :phoneNumber"),
	@NamedQuery(name = "Customeraddresses.findByHomeAddress", query = "SELECT c FROM Customeraddresses c WHERE c.homeAddress = :homeAddress")})
public class Customeraddresses implements Serializable {

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
  @Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	@Basic(optional = false)
  @Column(name = "HOME_ADDRESS")
	private String homeAddress;
	@OneToMany(mappedBy = "addressId")
	private List<Orders> ordersList;
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Users userId;

	public Customeraddresses() {
	}

	public Customeraddresses(Integer id) {
		this.id = id;
	}

	public Customeraddresses(Integer id, String receiverName, String phoneNumber, String homeAddress) {
		this.id = id;
		this.receiverName = receiverName;
		this.phoneNumber = phoneNumber;
		this.homeAddress = homeAddress;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
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
		if (!(object instanceof Customeraddresses)) {
			return false;
		}
		Customeraddresses other = (Customeraddresses) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Customeraddresses[ id=" + id + " ]";
	}

}
