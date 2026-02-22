package org.dbu.library.service

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.InMemoryLibraryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LibraryServiceTest {

    private lateinit var repository: InMemoryLibraryRepository
    private lateinit var service: DefaultLibraryService

    @BeforeEach
    fun setup() {
        repository = InMemoryLibraryRepository()
        service = DefaultLibraryService(repository)
        
        repository.addBook(Book("1", "Book 1", "Author 1", 2021))
        repository.addBook(Book("2", "Book 2", "Author 2", 2022))
        repository.addPatron(Patron("P1", "Patron 1"))
    }

    @Test
    fun `test borrow book successfully`() {
        val result = service.borrowBook("P1", "1")
        assertEquals(BorrowResult.SUCCESS, result)
        assertFalse(repository.findBook("1")!!.isAvailable)
        assertTrue(repository.findPatron("P1")!!.borrowedBooks.contains("1"))
    }

    @Test
    fun `test borrow book already borrowed`() {
        service.borrowBook("P1", "1")
        val result = service.borrowBook("P1", "1")
        assertEquals(BorrowResult.NOT_AVAILABLE, result)
    }

    @Test
    fun `test borrow book limit reached`() {
        repository.addBook(Book("3", "Book 3", "Author 3", 2023))
        repository.addBook(Book("4", "Book 4", "Author 4", 2024))
        repository.addBook(Book("5", "Book 5", "Author 5", 2025))
        repository.addBook(Book("6", "Book 6", "Author 6", 2026))

        service.borrowBook("P1", "1")
        service.borrowBook("P1", "2")
        service.borrowBook("P1", "3")
        service.borrowBook("P1", "4")
        service.borrowBook("P1", "5")
        
        val result = service.borrowBook("P1", "6")
        assertEquals(BorrowResult.LIMIT_REACHED, result)
    }

    @Test
    fun `test return book successfully`() {
        service.borrowBook("P1", "1")
        val success = service.returnBook("P1", "1")
        assertTrue(success)
        assertTrue(repository.findBook("1")!!.isAvailable)
        assertFalse(repository.findPatron("P1")!!.borrowedBooks.contains("1"))
    }

    @Test
    fun `test search books`() {
        val results = service.search("Author 1")
        assertEquals(1, results.size)
        assertEquals("Book 1", results[0].title)
    }

    @Test
    fun `test search books case insensitive`() {
        val results = service.search("author 1")
        assertEquals(1, results.size)
    }
}
