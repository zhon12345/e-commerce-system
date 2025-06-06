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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
	@Basic(optional = false)
	@Column(name = "DISCOUNT")
	@DecimalMax(value = "1.00")
	@DecimalMin(value = "0.01")
	private BigDecimal discount;
	@Basic(optional = false)
  @Column(name = "VALID_FROM")
  @Temporal(TemporalType.DATE)
	private Date validFrom;
	@Basic(optional = false)
  @Column(name = "VALID_TO")
  @Temporal(TemporalType.DATE)
	private Date validTo;
	@Column(name = "IS_ACTIVE", insertable = false)
	private Boolean isActive;
	@OneToMany(mappedBy = "promoId")
	private List<Orders> ordersList;

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

	@XmlTransient
	public List<Orders> getOrdersList() {
		return ordersList;
	}

	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
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
