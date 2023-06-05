package com.isetrip.jbotify.database;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public final class HibernateSet<E> implements Set<E> {

    private final Class<E> entityType;
    private final SessionFactory sessionFactory;

    public HibernateSet(Class<E> entityType) {
        this.entityType = entityType;
        this.sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Override
    public int size() {
        Session session = sessionFactory.openSession();
        try {
            Criteria criteria = session.createCriteria(entityType);
            return criteria.list().size();
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
            Criteria criteria = session.createCriteria(entityType);
            List<E> entities = criteria.list();
            return entities.iterator();
        } finally {
            session.close();
        }
    }

    @Override
    public Object[] toArray() {
        Session session = sessionFactory.openSession();
        try {
            Criteria criteria = session.createCriteria(entityType);
            List<E> entities = criteria.list();
            return entities.toArray();
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Session session = sessionFactory.openSession();
        try {
            Criteria criteria = session.createCriteria(entityType);
            List<E> entities = criteria.list();
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
            Criteria criteria = session.createCriteria(entityType);
            criteria.add(org.hibernate.criterion.Restrictions.in("id", collection));
            List<E> entities = criteria.list();
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
            Criteria criteria = session.createCriteria(entityType);
            List<E> entities = criteria.list();
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
            Field field = entityType.getDeclaredField(fieldName);
            field.setAccessible(true);
            Criteria criteria = session.createCriteria(entityType);
            criteria.add(org.hibernate.criterion.Restrictions.eq(fieldName, value));
            List<E> entities = criteria.list();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    public boolean update(E entity) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            E existingEntity = (E) session.merge(entity);
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

    public void close() {
        sessionFactory.close();
    }
}

