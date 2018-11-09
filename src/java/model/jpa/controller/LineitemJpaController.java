/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.jpa.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import model.Cart;
import model.Lineitem;
import model.Product;
import model.jpa.controller.exceptions.NonexistentEntityException;
import model.jpa.controller.exceptions.PreexistingEntityException;
import model.jpa.controller.exceptions.RollbackFailureException;

/**
 *
 * @author piyao
 */
public class LineitemJpaController implements Serializable {

    public LineitemJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Lineitem lineitem) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cart cartCartid = lineitem.getCartCartid();
            if (cartCartid != null) {
                cartCartid = em.getReference(cartCartid.getClass(), cartCartid.getCartid());
                lineitem.setCartCartid(cartCartid);
            }
            Product productProductid = lineitem.getProductProductid();
            if (productProductid != null) {
                productProductid = em.getReference(productProductid.getClass(), productProductid.getProductid());
                lineitem.setProductProductid(productProductid);
            }
            em.persist(lineitem);
            if (cartCartid != null) {
                cartCartid.getLineitemList().add(lineitem);
                cartCartid = em.merge(cartCartid);
            }
            if (productProductid != null) {
                productProductid.getLineitemList().add(lineitem);
                productProductid = em.merge(productProductid);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLineitem(lineitem.getLineitemid()) != null) {
                throw new PreexistingEntityException("Lineitem " + lineitem + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Lineitem lineitem) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Lineitem persistentLineitem = em.find(Lineitem.class, lineitem.getLineitemid());
            Cart cartCartidOld = persistentLineitem.getCartCartid();
            Cart cartCartidNew = lineitem.getCartCartid();
            Product productProductidOld = persistentLineitem.getProductProductid();
            Product productProductidNew = lineitem.getProductProductid();
            if (cartCartidNew != null) {
                cartCartidNew = em.getReference(cartCartidNew.getClass(), cartCartidNew.getCartid());
                lineitem.setCartCartid(cartCartidNew);
            }
            if (productProductidNew != null) {
                productProductidNew = em.getReference(productProductidNew.getClass(), productProductidNew.getProductid());
                lineitem.setProductProductid(productProductidNew);
            }
            lineitem = em.merge(lineitem);
            if (cartCartidOld != null && !cartCartidOld.equals(cartCartidNew)) {
                cartCartidOld.getLineitemList().remove(lineitem);
                cartCartidOld = em.merge(cartCartidOld);
            }
            if (cartCartidNew != null && !cartCartidNew.equals(cartCartidOld)) {
                cartCartidNew.getLineitemList().add(lineitem);
                cartCartidNew = em.merge(cartCartidNew);
            }
            if (productProductidOld != null && !productProductidOld.equals(productProductidNew)) {
                productProductidOld.getLineitemList().remove(lineitem);
                productProductidOld = em.merge(productProductidOld);
            }
            if (productProductidNew != null && !productProductidNew.equals(productProductidOld)) {
                productProductidNew.getLineitemList().add(lineitem);
                productProductidNew = em.merge(productProductidNew);
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
                Integer id = lineitem.getLineitemid();
                if (findLineitem(id) == null) {
                    throw new NonexistentEntityException("The lineitem with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Lineitem lineitem;
            try {
                lineitem = em.getReference(Lineitem.class, id);
                lineitem.getLineitemid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lineitem with id " + id + " no longer exists.", enfe);
            }
            Cart cartCartid = lineitem.getCartCartid();
            if (cartCartid != null) {
                cartCartid.getLineitemList().remove(lineitem);
                cartCartid = em.merge(cartCartid);
            }
            Product productProductid = lineitem.getProductProductid();
            if (productProductid != null) {
                productProductid.getLineitemList().remove(lineitem);
                productProductid = em.merge(productProductid);
            }
            em.remove(lineitem);
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

    public List<Lineitem> findLineitemEntities() {
        return findLineitemEntities(true, -1, -1);
    }

    public List<Lineitem> findLineitemEntities(int maxResults, int firstResult) {
        return findLineitemEntities(false, maxResults, firstResult);
    }

    private List<Lineitem> findLineitemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Lineitem.class));
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

    public Lineitem findLineitem(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Lineitem.class, id);
        } finally {
            em.close();
        }
    }

    public int getLineitemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Lineitem> rt = cq.from(Lineitem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
