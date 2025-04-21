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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author zhon12345
 */
@Entity
@Table(name = "PROMOTIONS")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Promotions.findAll", query = "SELECT p FROM Promotions p"),
	@NamedQuery(name = "Promotions.findById", query = "SELECT p FROM Promotions p WHERE p.id = :id"),
	@NamedQuery(name = "Promotions.findByPromoCode", query = "SELECT p FROM Promotions p WHERE p.promoCode = :promoCode"),
	@NamedQuery(name = "Promotions.findByDiscount", query = "SELECT p FROM Promotions p WHERE p.discount = :discount"),
	@NamedQuery(name = "Promotions.findByValidFrom", query = "SELECT p FROM Promotions p WHERE p.validFrom = :validFrom"),
	@NamedQuery(name = "Promotions.findByValidTo", query = "SELECT p FROM Promotions p WHERE p.validTo = :validTo"),
	@NamedQuery(name = "Promotions.findByIsActive", query = "SELECT p FROM Promotions p WHERE p.isActive = :isActive")})
public class Promotions implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Basic(optional = false)
  @Column(name = "PROMO_CODE")
	private String promoCode;
	// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
	@Basic(optional = false)
  @Column(name = "DISCOUNT")
	private BigDecimal discount;
	@Basic(optional = false)
  @Column(name = "VALID_FROM")
  @Temporal(TemporalType.DATE)
	private Date validFrom;
	@Basic(optional = false)
  @Column(name = "VALID_TO")
  @Temporal(TemporalType.DATE)
	private Date validTo;
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	public Promotions() {
	}

	public Promotions(Integer id) {
		this.id = id;
	}

	public Promotions(Integer id, String promoCode, BigDecimal discount, Date validFrom, Date validTo) {
		this.id = id;
		this.promoCode = promoCode;
		this.discount = discount;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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
		if (!(object instanceof Promotions)) {
			return false;
		}
		Promotions other = (Promotions) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Promotions[ id=" + id + " ]";
	}

}
