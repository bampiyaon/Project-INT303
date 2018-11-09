/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author piyao
 */
@Entity
@Table(name = "SHIPPING")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Shipping.findAll", query = "SELECT s FROM Shipping s")
    , @NamedQuery(name = "Shipping.findByBuildingno", query = "SELECT s FROM Shipping s WHERE s.buildingno = :buildingno")
    , @NamedQuery(name = "Shipping.findByFloor", query = "SELECT s FROM Shipping s WHERE s.floor = :floor")
    , @NamedQuery(name = "Shipping.findByRoom", query = "SELECT s FROM Shipping s WHERE s.room = :room")})
public class Shipping implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "BUILDINGNO")
    private String buildingno;
    @Column(name = "FLOOR")
    private Integer floor;
    @Column(name = "ROOM")
    private Boolean room;

    public Shipping() {
    }

    public Shipping(String buildingno) {
        this.buildingno = buildingno;
    }

    public String getBuildingno() {
        return buildingno;
    }

    public void setBuildingno(String buildingno) {
        this.buildingno = buildingno;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Boolean getRoom() {
        return room;
    }

    public void setRoom(Boolean room) {
        this.room = room;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (buildingno != null ? buildingno.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Shipping)) {
            return false;
        }
        Shipping other = (Shipping) object;
        if ((this.buildingno == null && other.buildingno != null) || (this.buildingno != null && !this.buildingno.equals(other.buildingno))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Shipping[ buildingno=" + buildingno + " ]";
    }
    
}
