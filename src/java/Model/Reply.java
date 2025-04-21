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
@Table(name = "REPLY")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Reply.findAll", query = "SELECT r FROM Reply r"),
	@NamedQuery(name = "Reply.findById", query = "SELECT r FROM Reply r WHERE r.id = :id"),
	@NamedQuery(name = "Reply.findByReplyText", query = "SELECT r FROM Reply r WHERE r.replyText = :replyText"),
	@NamedQuery(name = "Reply.findByIsArchived", query = "SELECT r FROM Reply r WHERE r.isArchived = :isArchived"),
	@NamedQuery(name = "Reply.findByReplyDate", query = "SELECT r FROM Reply r WHERE r.replyDate = :replyDate")})
public class Reply implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Basic(optional = false)
  @Column(name = "REPLY_TEXT")
	private String replyText;
	@Column(name = "IS_ARCHIVED")
	private Boolean isArchived;
	@Column(name = "REPLY_DATE")
  @Temporal(TemporalType.TIMESTAMP)
	private Date replyDate;
	@JoinColumn(name = "REVIEW_ID", referencedColumnName = "ID")
  @ManyToOne
	private Reviews reviewId;
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @ManyToOne
	private Users userId;

	public Reply() {
	}

	public Reply(Integer id) {
		this.id = id;
	}

	public Reply(Integer id, String replyText) {
		this.id = id;
		this.replyText = replyText;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReplyText() {
		return replyText;
	}

	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}

	public Boolean getIsArchived() {
		return isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}

	public Date getReplyDate() {
		return replyDate;
	}

	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}

	public Reviews getReviewId() {
		return reviewId;
	}

	public void setReviewId(Reviews reviewId) {
		this.reviewId = reviewId;
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
		if (!(object instanceof Reply)) {
			return false;
		}
		Reply other = (Reply) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Reply[ id=" + id + " ]";
	}

}
