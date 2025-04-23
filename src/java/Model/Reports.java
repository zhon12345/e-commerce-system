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
@Table(name = "REPORTS")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Reports.findAll", query = "SELECT r FROM Reports r"),
	@NamedQuery(name = "Reports.findById", query = "SELECT r FROM Reports r WHERE r.id = :id"),
	@NamedQuery(name = "Reports.findByReportType", query = "SELECT r FROM Reports r WHERE r.reportType = :reportType"),
	@NamedQuery(name = "Reports.findByGeneratedDate", query = "SELECT r FROM Reports r WHERE r.generatedDate = :generatedDate"),
	@NamedQuery(name = "Reports.findByDetails", query = "SELECT r FROM Reports r WHERE r.details = :details")})
public class Reports implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Basic(optional = false)
  @Column(name = "REPORT_TYPE")
	private String reportType;
	@Column(name = "GENERATED_DATE")
  @Temporal(TemporalType.TIMESTAMP)
	private Date generatedDate;
	@Column(name = "DETAILS")
	private String details;
	@JoinColumn(name = "GENERATED_BY_USER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Users generatedByUserId;

	public Reports() {
	}

	public Reports(Integer id) {
		this.id = id;
	}

	public Reports(Integer id, String reportType) {
		this.id = id;
		this.reportType = reportType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Users getGeneratedByUserId() {
		return generatedByUserId;
	}

	public void setGeneratedByUserId(Users generatedByUserId) {
		this.generatedByUserId = generatedByUserId;
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
		if (!(object instanceof Reports)) {
			return false;
		}
		Reports other = (Reports) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Reports[ id=" + id + " ]";
	}

}
