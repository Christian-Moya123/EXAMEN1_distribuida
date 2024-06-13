package org.example.service;

import org.example.model.Book;

import java.util.List;

public interface IBookService {


        Object create(Book book);

        Book find(Long id);

        List<Book> findAll();

        Book update(Book book);

        void delete(Long id);

}
