/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.BaseModel;
import Model.Identite;
import Utils.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Eqima
 */
public class HibernateDAO {

    public Long save(BaseModel o) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.save(o);
            tr.commit();
        } catch (Exception e) {
        } finally {
            session.close();
        }
        return o.getIdPersonne();
    }
    
    public Long delete(BaseModel o) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.delete(o);
            tr.commit();
        } catch (Exception e) {
        } finally {
            session.close();
        }
        return o.getIdPersonne();
    }

    public Long update(BaseModel o) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tr = session.beginTransaction();
            session.update(o);
            tr.commit();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
        return o.getIdPersonne();
    }

    public List findAll(BaseModel o) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Criteria cr = session.createCriteria(o.getClass());
            return cr.list();
        } catch (Exception e) {
            return null;
        } finally {
            session.close();
        }

    }



    public BaseModel findAllByName(BaseModel o, String nom) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Criteria cr = session.createCriteria(o.getClass());
            cr.add(Restrictions.eq("nom", nom));
            return (BaseModel) cr.uniqueResult();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return null;
        } finally {
            session.close();
        }
    }
    public BaseModel Authentification(BaseModel o, String username,String password) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Criteria cr = session.createCriteria(o.getClass());
            cr.add(Restrictions.eq("username", username));
            cr.add(Restrictions.eq("password", password));
            return (BaseModel) cr.uniqueResult();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return null;
        } finally {
            session.close();
        }
    }
    public BaseModel findAllByID(BaseModel o, Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Criteria cr = session.createCriteria(o.getClass());
            cr.add(Restrictions.eq("idPersonne", id));
            return (BaseModel) cr.uniqueResult();
        } catch (HibernateException e) {
            System.out.println(e.fillInStackTrace());
            return null;
        } finally {
            session.close();
        }
    }
    public Long getLastRecordIdPersonne(BaseModel o) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
        Criteria cr = session.createCriteria(o.getClass());
        cr.addOrder(Order.desc("idPersonne"));
        cr.setMaxResults(1);
        o=(BaseModel) cr.uniqueResult();
        return o.getIdPersonne();
    } catch (Exception e) {
        System.out.println(e.fillInStackTrace());
        return null;
    } finally {
        session.close();
    }
}


}
