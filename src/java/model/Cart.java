/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author piyao
 */
@Entity
@Table(name = "CART")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cart.findAll", query = "SELECT c FROM Cart c")
    , @NamedQuery(name = "Cart.findByCartid", query = "SELECT c FROM Cart c WHERE c.cartid = :cartid")
    , @NamedQuery(name = "Cart.findByNumberofproductlist", query = "SELECT c FROM Cart c WHERE c.numberofproductlist = :numberofproductlist")
    , @NamedQuery(name = "Cart.findByCarttotal", query = "SELECT c FROM Cart c WHERE c.carttotal = :carttotal")})
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "CARTID")
    private Integer cartid;
    @Column(name = "NUMBEROFPRODUCTLIST")
    private Integer numberofproductlist;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "CARTTOTAL")
    private Double carttotal;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "cartCartid")
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cartCartid")
    private List<Lineitem> lineitemList;
    @JoinColumn(name = "CUSTOMER_CUSTOMERID", referencedColumnName = "CUSTOMERID")
    @OneToOne(optional = false)
    private Customer customerCustomerid;

    public Cart() {
    }

    public Cart(Integer cartid) {
        this.cartid = cartid;
    }

    public Integer getCartid() {
        return cartid;
    }

    public void setCartid(Integer cartid) {
        this.cartid = cartid;
    }

    public Integer getNumberofproductlist() {
        return numberofproductlist;
    }

    public void setNumberofproductlist(Integer numberofproductlist) {
        this.numberofproductlist = numberofproductlist;
    }

    public Double getCarttotal() {
        return carttotal;
    }

    public void setCarttotal(Double carttotal) {
        this.carttotal = carttotal;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @XmlTransient
    public List<Lineitem> getLineitemList() {
        return lineitemList;
    }

    public void setLineitemList(List<Lineitem> lineitemList) {
        this.lineitemList = lineitemList;
    }

    public Customer getCustomerCustomerid() {
        return customerCustomerid;
    }

    public void setCustomerCustomerid(Customer customerCustomerid) {
        this.customerCustomerid = customerCustomerid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cartid != null ? cartid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cart)) {
            return false;
        }
        Cart other = (Cart) object;
        if ((this.cartid == null && other.cartid != null) || (this.cartid != null && !this.cartid.equals(other.cartid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Cart[ cartid=" + cartid + " ]";
    }
    
}
