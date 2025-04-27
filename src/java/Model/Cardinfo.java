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
@Table(name = "CARDINFO")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Cardinfo.findAll", query = "SELECT c FROM Cardinfo c"),
	@NamedQuery(name = "Cardinfo.findById", query = "SELECT c FROM Cardinfo c WHERE c.id = :id"),
	@NamedQuery(name = "Cardinfo.findByCardNumber", query = "SELECT c FROM Cardinfo c WHERE c.cardNumber = :cardNumber"),
	@NamedQuery(name = "Cardinfo.findByCardName", query = "SELECT c FROM Cardinfo c WHERE c.cardName = :cardName"),
	@NamedQuery(name = "Cardinfo.findByExpMonth", query = "SELECT c FROM Cardinfo c WHERE c.expMonth = :expMonth"),
	@NamedQuery(name = "Cardinfo.findByExpYear", query = "SELECT c FROM Cardinfo c WHERE c.expYear = :expYear"),
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
	@Column(name = "CARD_NAME")
	private String cardName;
	@Basic(optional = false)
  @Column(name = "EXP_MONTH")
	private short expMonth;
	@Basic(optional = false)
  @Column(name = "EXP_YEAR")
	private short expYear;
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

	public Cardinfo(Integer id, short expMonth, short expYear) {
		this.id = id;
		this.expMonth = expMonth;
		this.expYear = expYear;
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

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public short getExpMonth() {
		return expMonth;
	}

	public void setExpMonth(short expMonth) {
		this.expMonth = expMonth;
	}

	public short getExpYear() {
		return expYear;
	}

	public void setExpYear(short expYear) {
		this.expYear = expYear;
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
