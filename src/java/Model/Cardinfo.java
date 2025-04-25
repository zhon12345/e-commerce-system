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
@Table(name = "CARDINFO")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Cardinfo.findAll", query = "SELECT c FROM Cardinfo c"),
	@NamedQuery(name = "Cardinfo.findById", query = "SELECT c FROM Cardinfo c WHERE c.id = :id"),
	@NamedQuery(name = "Cardinfo.findByCardNumber", query = "SELECT c FROM Cardinfo c WHERE c.cardNumber = :cardNumber"),
	@NamedQuery(name = "Cardinfo.findByCardHolderName", query = "SELECT c FROM Cardinfo c WHERE c.cardHolderName = :cardHolderName"),
	@NamedQuery(name = "Cardinfo.findByExpirationDate", query = "SELECT c FROM Cardinfo c WHERE c.expirationDate = :expirationDate"),
	@NamedQuery(name = "Cardinfo.findByCvv", query = "SELECT c FROM Cardinfo c WHERE c.cvv = :cvv"),
	@NamedQuery(name = "Cardinfo.findByIsArchived", query = "SELECT c FROM Cardinfo c WHERE c.isArchived = :isArchived")})
public class Cardinfo implements Serializable {

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
	@Column(name = "IS_ARCHIVED")
	private Boolean isArchived;
	@OneToMany(mappedBy = "cardInfoId")
	private List<Orders> ordersList;
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Users userId;

	public Cardinfo() {
	}

	public Cardinfo(Integer id) {
		this.id = id;
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
		if (!(object instanceof Cardinfo)) {
			return false;
		}
		Cardinfo other = (Cardinfo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Cardinfo[ id=" + id + " ]";
	}

}
