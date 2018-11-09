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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author piyao
 */
@Entity
@Table(name = "LINEITEM")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lineitem.findAll", query = "SELECT l FROM Lineitem l")
    , @NamedQuery(name = "Lineitem.findByLineitemid", query = "SELECT l FROM Lineitem l WHERE l.lineitemid = :lineitemid")
    , @NamedQuery(name = "Lineitem.findByQuantity", query = "SELECT l FROM Lineitem l WHERE l.quantity = :quantity")
    , @NamedQuery(name = "Lineitem.findByPrice", query = "SELECT l FROM Lineitem l WHERE l.price = :price")})
public class Lineitem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "LINEITEMID")
    private Integer lineitemid;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Integer price;
    @JoinColumn(name = "CART_CARTID", referencedColumnName = "CARTID")
    @ManyToOne(optional = false)
    private Cart cartCartid;
    @JoinColumn(name = "PRODUCT_PRODUCTID", referencedColumnName = "PRODUCTID")
    @ManyToOne(optional = false)
    private Product productProductid;

    public Lineitem() {
    }

    public Lineitem(Integer lineitemid) {
        this.lineitemid = lineitemid;
    }

    public Integer getLineitemid() {
        return lineitemid;
    }

    public void setLineitemid(Integer lineitemid) {
        this.lineitemid = lineitemid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Cart getCartCartid() {
        return cartCartid;
    }

    public void setCartCartid(Cart cartCartid) {
        this.cartCartid = cartCartid;
    }

    public Product getProductProductid() {
        return productProductid;
    }

    public void setProductProductid(Product productProductid) {
        this.productProductid = productProductid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lineitemid != null ? lineitemid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lineitem)) {
            return false;
        }
        Lineitem other = (Lineitem) object;
        if ((this.lineitemid == null && other.lineitemid != null) || (this.lineitemid != null && !this.lineitemid.equals(other.lineitemid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Lineitem[ lineitemid=" + lineitemid + " ]";
    }
    
}
