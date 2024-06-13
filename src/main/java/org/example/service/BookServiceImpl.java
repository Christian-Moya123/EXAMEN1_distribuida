package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.Book;

import java.util.List;

@ApplicationScoped
public class BookServiceImpl implements IBookService{
    @Inject
    private EntityManager em;

    @Override
    public Book find(Long id) {
        return em.find(Book.class, id);
    }

    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b ORDER BY b.id ASC", Book.class);
        return query.getResultList();
    }

    @Override
    public Object create(Book book) {
        var transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(book);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Book update(Book book) {
        var transaction = em.getTransaction();
        try {
            transaction.begin();
            Book updatedBook = em.merge(book);
            transaction.commit();
            return updatedBook;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        var transaction = em.getTransaction();
        try {
            transaction.begin();
            Book book = find(id);
            if (book != null) {
                em.remove(book);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }
}
