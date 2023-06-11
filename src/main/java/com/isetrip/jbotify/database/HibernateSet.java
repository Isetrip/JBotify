package com.isetrip.jbotify.database;

import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.Serializable;
import java.util.*;

public final class HibernateSet<E> implements Set<E> {

    private final Class<E> entityType;
    private final SessionFactory sessionFactory;

    public HibernateSet(Class<E> entityType) {
        this.entityType = entityType;

        StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder();

        MetadataSources sources;

        if (entityType.isAnnotationPresent(Entity.class)) {
            sources = new MetadataSources(registry.configure().build());
            sources.addAnnotatedClass(entityType);
        } else {
            sources = new MetadataSources(registry.configure("hibernate.cfg.xml").build());
        }

        this.sessionFactory = sources.buildMetadata().buildSessionFactory();
    }

    @Override
    public int size() {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<E> root = criteriaQuery.from(entityType);
            criteriaQuery.select(criteriaBuilder.count(root));
            return session.createQuery(criteriaQuery).getSingleResult().intValue();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(entityType, (Serializable) o) != null;
        } finally {
            session.close();
        }
    }

    @Override
    public Iterator<E> iterator() {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityType);
            Root<E> root = criteriaQuery.from(entityType);
            criteriaQuery.select(root);
            List<E> entities = session.createQuery(criteriaQuery).getResultList();
            return entities.iterator();
        } finally {
            session.close();
        }
    }

    @Override
    public Object[] toArray() {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityType);
            Root<E> root = criteriaQuery.from(entityType);
            criteriaQuery.select(root);
            List<E> entities = session.createQuery(criteriaQuery).getResultList();
            return entities.toArray();
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityType);
            Root<E> root = criteriaQuery.from(entityType);
            criteriaQuery.select(root);
            List<E> entities = session.createQuery(criteriaQuery).getResultList();
            return entities.toArray(a);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean add(E e) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(e);
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean remove(Object o) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(o);
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityType);
            Root<E> root = criteriaQuery.from(entityType);
            criteriaQuery.select(root);
            criteriaQuery.where(root.get("id").in(collection));
            List<E> entities = session.createQuery(criteriaQuery).getResultList();
            return entities.size() == collection.size();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            for (E entity : collection) {
                session.save(entity);
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityType);
            Root<E> root = criteriaQuery.from(entityType);
            criteriaQuery.select(root);
            List<E> entities = session.createQuery(criteriaQuery).getResultList();
            for (E entity : entities) {
                if (!collection.contains(entity)) {
                    session.delete(entity);
                }
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            for (Object entity : collection) {
                session.delete(entity);
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public void clear() {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.createQuery("DELETE FROM " + entityType.getSimpleName()).executeUpdate();
            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<E> findByField(String fieldName, Object value) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityType);
            Root<E> root = criteriaQuery.from(entityType);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get(fieldName), value));
            List<E> entities = session.createQuery(criteriaQuery).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    public E update(E entity) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            E existingEntity = session.get(entityType, ((Serializable) entity));
            if (existingEntity != null) {
                session.merge(entity);
            } else {
                session.save(entity);
            }

            session.getTransaction().commit();
            return entity;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    public void close() {
        sessionFactory.close();
    }
}