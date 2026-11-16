package web.com.dao;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import web.com.config.JPAConfig;
import web.com.models.Video;

public class VideoDAO {
    public Video get(int id) {
        EntityManager em = JPAConfig.getEntityManagerFactory().createEntityManager();
        try { return em.find(Video.class, id); } finally { em.close(); }
    }

    public List<Video> getAll() {
        EntityManager em = JPAConfig.getEntityManagerFactory().createEntityManager();
        try { return em.createQuery("SELECT v FROM Video v ORDER BY v.createdAt DESC, v.id DESC", Video.class).getResultList(); }
        finally { em.close(); }
    }

    public List<Video> getPage(int page, int pageSize) {
        EntityManager em = JPAConfig.getEntityManagerFactory().createEntityManager();
        try { return em.createQuery("SELECT v FROM Video v WHERE v.active = true ORDER BY v.createdAt DESC, v.id DESC", Video.class)
                .setFirstResult((page - 1) * pageSize).setMaxResults(pageSize).getResultList(); }
        finally { em.close(); }
    }

    public long countActive() {
        EntityManager em = JPAConfig.getEntityManagerFactory().createEntityManager();
        try { return em.createQuery("SELECT COUNT(v) FROM Video v WHERE v.active = true", Long.class).getSingleResult(); }
        finally { em.close(); }
    }

    public void save(Video video) {
        EntityManager em = JPAConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try { tx.begin(); if (video.getId() == 0) em.persist(video); else em.merge(video); tx.commit(); }
        catch (RuntimeException e) { if (tx.isActive()) tx.rollback(); throw e; }
        finally { em.close(); }
    }

    public boolean delete(int id) {
        EntityManager em = JPAConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin(); Video video = em.find(Video.class, id);
            if (video != null) em.remove(video);
            tx.commit(); return video != null;
        } catch (RuntimeException e) { if (tx.isActive()) tx.rollback(); throw e; }
        finally { em.close(); }
    }
}
