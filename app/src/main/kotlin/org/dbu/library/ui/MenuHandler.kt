package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService
import org.dbu.library.util.display

fun handleMenuAction(
    choice: String,
    service: LibraryService,
    repository: LibraryRepository
): Boolean {

    return when (choice) {

        "1" -> {
            addBook(service)
            true
        }

        "2" -> {
            registerPatron(repository)
            true
        }

        "3" -> {
            borrowBook(service)
            true
        }

        "4" -> {
            returnBook(service)
            true
        }

        "5" -> {
            search(service)
            true
        }

        "6" -> {
            listAllBooks(repository)
            true
        }

        "0" -> false

        else -> {
            println("Invalid option")
            true
        }
    }
}

fun addBook(service: LibraryService) {
    print("ISBN: ")
    val isbn = readln()
    print("Title: ")
    val title = readln()
    print("Author: ")
    val author = readln()
    print("Year: ")
    val year = readln().toIntOrNull() ?: 0

    val added = service.addBook(Book(isbn, title, author, year))
    println("Book added: $added")
}

fun registerPatron(repository: LibraryRepository) {
    print("Patron ID: ")
    val id = readln()
    print("Name: ")
    val name = readln()

    val added = repository.addPatron(Patron(id, name))
    println("Patron added: $added")
}

fun borrowBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln()
    print("ISBN: ")
    val isbn = readln()

    when (service.borrowBook(patronId, isbn)) {
        BorrowResult.SUCCESS -> println("Borrowed")
        BorrowResult.BOOK_NOT_FOUND -> println("Book not found")
        BorrowResult.PATRON_NOT_FOUND -> println("Patron not found")
        BorrowResult.NOT_AVAILABLE -> println("Not available")
        BorrowResult.LIMIT_REACHED -> println("Limit reached")
    }
}

fun returnBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln()
    print("ISBN: ")
    val isbn = readln()

    val success = service.returnBook(patronId, isbn)
    if (success) println("Returned") else println("Return failed")
}

fun search(service: LibraryService) {
    print("Query: ")
    val query = readln()
    val results = service.search(query)

    println("Search results:")
    if (results.isEmpty()) {
        println("No books found")
    } else {
        results.forEachIndexed { index, book ->
            println("${index + 1}. ${book.display()}")
        }
    }
}

fun listAllBooks(repository: LibraryRepository) {
    val books = repository.getAllBooks()
    println("All books in library:")
    if (books.isEmpty()) {
        println("Library is empty")
    } else {
        books.forEachIndexed { index, book ->
            val status = if (book.isAvailable) "[Available]" else "[Borrowed]"
            println("${index + 1}. ${book.display()} $status")
        }
    }
}