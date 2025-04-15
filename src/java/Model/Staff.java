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
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author zhon12345
 */
@Entity
@Table(name = "STAFF")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Staff.findAll", query = "SELECT s FROM Staff s"),
	@NamedQuery(name = "Staff.findById", query = "SELECT s FROM Staff s WHERE s.id = :id"),
	@NamedQuery(name = "Staff.findByUsername", query = "SELECT s FROM Staff s WHERE s.username = :username"),
	@NamedQuery(name = "Staff.findByEmail", query = "SELECT s FROM Staff s WHERE s.email = :email"),
	@NamedQuery(name = "Staff.findByPassword", query = "SELECT s FROM Staff s WHERE s.password = :password"),
	@NamedQuery(name = "Staff.findByIsManager", query = "SELECT s FROM Staff s WHERE s.isManager = :isManager")})
public class Staff implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
	private Integer id;
	@Basic(optional = false)
  @Column(name = "USERNAME")
	private String username;
	@Basic(optional = false)
  @Column(name = "EMAIL")
	private String email;
	@Basic(optional = false)
  @Column(name = "PASSWORD")
	private String password;
	@Basic(optional = false)
  @Column(name = "IS_MANAGER")
	private Boolean isManager;

	public Staff() {
	}

	public Staff(Integer id) {
		this.id = id;
	}

	public Staff(Integer id, String username, String email, String password, Boolean isManager) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.isManager = isManager;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsManager() {
		return isManager;
	}

	public void setIsManager(Boolean isManager) {
		this.isManager = isManager;
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
		if (!(object instanceof Staff)) {
			return false;
		}
		Staff other = (Staff) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model.Staff[ id=" + id + " ]";
	}

}
