/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jpa.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Cart;
import model.Payment;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Customer;
import model.jpa.controller.exceptions.IllegalOrphanException;
import model.jpa.controller.exceptions.NonexistentEntityException;
import model.jpa.controller.exceptions.PreexistingEntityException;
import model.jpa.controller.exceptions.RollbackFailureException;

/**
 *
 * @author piyao
 */
public class CustomerJpaController implements Serializable {

    public CustomerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Customer customer) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (customer.getPaymentList() == null) {
            customer.setPaymentList(new ArrayList<Payment>());
        }
        List<String> illegalOrphanMessages = null;
        Cart cartCartidOrphanCheck = customer.getCartCartid();
        if (cartCartidOrphanCheck != null) {
            Customer oldCustomerOfCartCartid = cartCartidOrphanCheck.getCustomer();
            if (oldCustomerOfCartCartid != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Cart " + cartCartidOrphanCheck + " already has an item of type Customer whose cartCartid column cannot be null. Please make another selection for the cartCartid field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cart cartCartid = customer.getCartCartid();
            if (cartCartid != null) {
                cartCartid = em.getReference(cartCartid.getClass(), cartCartid.getCartid());
                customer.setCartCartid(cartCartid);
            }
            Cart cart = customer.getCart();
            if (cart != null) {
                cart = em.getReference(cart.getClass(), cart.getCartid());
                customer.setCart(cart);
            }
            List<Payment> attachedPaymentList = new ArrayList<Payment>();
            for (Payment paymentListPaymentToAttach : customer.getPaymentList()) {
                paymentListPaymentToAttach = em.getReference(paymentListPaymentToAttach.getClass(), paymentListPaymentToAttach.getPaymentid());
                attachedPaymentList.add(paymentListPaymentToAttach);
            }
            customer.setPaymentList(attachedPaymentList);
            em.persist(customer);
            if (cartCartid != null) {
                cartCartid.setCustomer(customer);
                cartCartid = em.merge(cartCartid);
            }
            if (cart != null) {
                Customer oldCustomerCustomeridOfCart = cart.getCustomerCustomerid();
                if (oldCustomerCustomeridOfCart != null) {
                    oldCustomerCustomeridOfCart.setCart(null);
                    oldCustomerCustomeridOfCart = em.merge(oldCustomerCustomeridOfCart);
                }
                cart.setCustomerCustomerid(customer);
                cart = em.merge(cart);
            }
            for (Payment paymentListPayment : customer.getPaymentList()) {
                Customer oldCustomerCustomeridOfPaymentListPayment = paymentListPayment.getCustomerCustomerid();
                paymentListPayment.setCustomerCustomerid(customer);
                paymentListPayment = em.merge(paymentListPayment);
                if (oldCustomerCustomeridOfPaymentListPayment != null) {
                    oldCustomerCustomeridOfPaymentListPayment.getPaymentList().remove(paymentListPayment);
                    oldCustomerCustomeridOfPaymentListPayment = em.merge(oldCustomerCustomeridOfPaymentListPayment);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCustomer(customer.getCustomerid()) != null) {
                throw new PreexistingEntityException("Customer " + customer + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Customer customer) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customer persistentCustomer = em.find(Customer.class, customer.getCustomerid());
            Cart cartCartidOld = persistentCustomer.getCartCartid();
            Cart cartCartidNew = customer.getCartCartid();
            Cart cartOld = persistentCustomer.getCart();
            Cart cartNew = customer.getCart();
            List<Payment> paymentListOld = persistentCustomer.getPaymentList();
            List<Payment> paymentListNew = customer.getPaymentList();
            List<String> illegalOrphanMessages = null;
            if (cartCartidNew != null && !cartCartidNew.equals(cartCartidOld)) {
                Customer oldCustomerOfCartCartid = cartCartidNew.getCustomer();
                if (oldCustomerOfCartCartid != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Cart " + cartCartidNew + " already has an item of type Customer whose cartCartid column cannot be null. Please make another selection for the cartCartid field.");
                }
            }
            if (cartOld != null && !cartOld.equals(cartNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Cart " + cartOld + " since its customerCustomerid field is not nullable.");
            }
            for (Payment paymentListOldPayment : paymentListOld) {
                if (!paymentListNew.contains(paymentListOldPayment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Payment " + paymentListOldPayment + " since its customerCustomerid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cartCartidNew != null) {
                cartCartidNew = em.getReference(cartCartidNew.getClass(), cartCartidNew.getCartid());
                customer.setCartCartid(cartCartidNew);
            }
            if (cartNew != null) {
                cartNew = em.getReference(cartNew.getClass(), cartNew.getCartid());
                customer.setCart(cartNew);
            }
            List<Payment> attachedPaymentListNew = new ArrayList<Payment>();
            for (Payment paymentListNewPaymentToAttach : paymentListNew) {
                paymentListNewPaymentToAttach = em.getReference(paymentListNewPaymentToAttach.getClass(), paymentListNewPaymentToAttach.getPaymentid());
                attachedPaymentListNew.add(paymentListNewPaymentToAttach);
            }
            paymentListNew = attachedPaymentListNew;
            customer.setPaymentList(paymentListNew);
            customer = em.merge(customer);
            if (cartCartidOld != null && !cartCartidOld.equals(cartCartidNew)) {
                cartCartidOld.setCustomer(null);
                cartCartidOld = em.merge(cartCartidOld);
            }
            if (cartCartidNew != null && !cartCartidNew.equals(cartCartidOld)) {
                cartCartidNew.setCustomer(customer);
                cartCartidNew = em.merge(cartCartidNew);
            }
            if (cartNew != null && !cartNew.equals(cartOld)) {
                Customer oldCustomerCustomeridOfCart = cartNew.getCustomerCustomerid();
                if (oldCustomerCustomeridOfCart != null) {
                    oldCustomerCustomeridOfCart.setCart(null);
                    oldCustomerCustomeridOfCart = em.merge(oldCustomerCustomeridOfCart);
                }
                cartNew.setCustomerCustomerid(customer);
                cartNew = em.merge(cartNew);
            }
            for (Payment paymentListNewPayment : paymentListNew) {
                if (!paymentListOld.contains(paymentListNewPayment)) {
                    Customer oldCustomerCustomeridOfPaymentListNewPayment = paymentListNewPayment.getCustomerCustomerid();
                    paymentListNewPayment.setCustomerCustomerid(customer);
                    paymentListNewPayment = em.merge(paymentListNewPayment);
                    if (oldCustomerCustomeridOfPaymentListNewPayment != null && !oldCustomerCustomeridOfPaymentListNewPayment.equals(customer)) {
                        oldCustomerCustomeridOfPaymentListNewPayment.getPaymentList().remove(paymentListNewPayment);
                        oldCustomerCustomeridOfPaymentListNewPayment = em.merge(oldCustomerCustomeridOfPaymentListNewPayment);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = customer.getCustomerid();
                if (findCustomer(id) == null) {
                    throw new NonexistentEntityException("The customer with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customer customer;
            try {
                customer = em.getReference(Customer.class, id);
                customer.getCustomerid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The customer with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Cart cartOrphanCheck = customer.getCart();
            if (cartOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customer (" + customer + ") cannot be destroyed since the Cart " + cartOrphanCheck + " in its cart field has a non-nullable customerCustomerid field.");
            }
            List<Payment> paymentListOrphanCheck = customer.getPaymentList();
            for (Payment paymentListOrphanCheckPayment : paymentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customer (" + customer + ") cannot be destroyed since the Payment " + paymentListOrphanCheckPayment + " in its paymentList field has a non-nullable customerCustomerid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cart cartCartid = customer.getCartCartid();
            if (cartCartid != null) {
                cartCartid.setCustomer(null);
                cartCartid = em.merge(cartCartid);
            }
            em.remove(customer);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Customer> findCustomerEntities() {
        return findCustomerEntities(true, -1, -1);
    }

    public List<Customer> findCustomerEntities(int maxResults, int firstResult) {
        return findCustomerEntities(false, maxResults, firstResult);
    }

    private List<Customer> findCustomerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Customer.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Customer findCustomer(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    public int getCustomerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Customer> rt = cq.from(Customer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
