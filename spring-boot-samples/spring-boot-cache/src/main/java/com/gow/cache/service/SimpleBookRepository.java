package com.gow.cache.service;

import com.gow.cache.model.Book;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

/**
 * @author gow
 * @date 2021/7/3 0003
 */
@Component
public class SimpleBookRepository implements BookRepository {

    @Override
    @Cacheable(cacheNames = "cacheOne", key = "#isbn", sync = false, condition = "#isbn !=null")
    public Book getByIsbn(String isbn) {
        simulateSlowService();
        return new Book(isbn, "Some book");
    }

    @Override
    @Cacheable(cacheNames = "cacheTwo", key = "#isbn")
    public List<Book> getBooks(String isbn) {
        simulateSlowService();
        ArrayList<Book> books = new ArrayList<>();
        books.add(new Book(isbn, "共产党宣言"));
        return books;
    }

    @CachePut(cacheNames = "cacheOne", key = "#isbn")
    public Book updateBook(String isbn, Book descriptor) {
        // do update
        return descriptor;
    }

    @CacheEvict(cacheNames = "cacheOne", allEntries = true)
    public void loadBooks() {
        // Using the allEntries attribute to evict all entries from the cache.
    }

    @CacheEvict(cacheNames = "cacheOne", key = "#isbn")
    public void removeBook(String isbn) {
        // Using the allEntries attribute to evict all entries from the cache.
    }

    @Caching(evict = {@CacheEvict("cacheOne"), @CacheEvict(cacheNames = "cacheTwo", key = "#deposit")})
    public Book importBooks(String deposit, Date date) {
        return new Book(deposit, date.toString());
    }

    // Don't do this at home
    private void simulateSlowService() {
        try {
            long time = 3000L;
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
