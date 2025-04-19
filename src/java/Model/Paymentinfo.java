/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author zhon12345
 */
@Entity
@Table(name = "PAYMENTINFO")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Paymentinfo.findAll", query = "SELECT p FROM Paymentinfo p"),
	@NamedQuery(name = "Paymentinfo.findById", query = "SELECT p FROM Paymentinfo p WHERE p.id = :id"),
	@NamedQuery(name = "Paymentinfo.findByCardNumber", query = "SELECT p FROM Paymentinfo p WHERE p.cardNumber = :cardNumber"),
	@NamedQuery(name = "Paymentinfo.findByCardHolderName", query = "SELECT p FROM Paymentinfo p WHERE p.cardHolderName = :cardHolderName"),
	@NamedQuery(name = "Paymentinfo.findByExpirationDate", query = "SELECT p FROM Paymentinfo p WHERE p.expirationDate = :expirationDate"),
	@NamedQuery(name = "Paymentinfo.findByCvv", query = "SELECT p FROM Paymentinfo p WHERE p.cvv = :cvv"),
	@NamedQuery(name = "Paymentinfo.findByPaymentMethod", query = "SELECT p FROM Paymentinfo p WHERE p.paymentMethod = :paymentMethod")})
public class Paymentinfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Column(name = "CARD_NUMBER")
	private String cardNumber;
	@Column(name = "CARD_HOLDER_NAME")
	private String cardHolderName;
	@Column(name = "EXPIRATION_DATE")
  @Temporal(TemporalType.DATE)
	private Date expirationDate;
	@Column(name = "CVV")
	private String cvv;
	@Basic(optional = false)
  @Column(name = "PAYMENT_METHOD")
	private String paymentMethod;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentInfoId")
	private List<Orders> ordersList;
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Users userId;

	public Paymentinfo() {
	}

	public Paymentinfo(Integer id) {
		this.id = id;
	}

	public Paymentinfo(Integer id, String paymentMethod) {
		this.id = id;
		this.paymentMethod = paymentMethod;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
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
		if (!(object instanceof Paymentinfo)) {
			return false;
		}
		Paymentinfo other = (Paymentinfo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Paymentinfo[ id=" + id + " ]";
	}

}
