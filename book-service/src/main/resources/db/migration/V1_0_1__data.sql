insert into book (name, publication_year) values ('The Gambler', 1866);
insert into book (name, publication_year) values ('The Brothers Karamazov', 1879);
insert into book (name, publication_year) values ('The Cherry Orchard', 1904);
insert into book (name, publication_year) values ('The Master and Margarita', 1966);
insert into book (name, publication_year) values ('And Quiet Flows the Don', 1928);

insert into author (first_name, middle_name, last_name, country, date_of_birth) values ('Fyodor', 'Mikhailovich', 'Dostoevsky', 'Russia', '1821-11-11');
insert into author (first_name, middle_name, last_name, country, date_of_birth) values ('Anton', 'Pavlovich', 'Chekhov', 'Russia', '1860-01-29');
insert into author (first_name, middle_name, last_name, country, date_of_birth) values ('Mikhail', 'Afanasyevich', 'Bulgakov', 'Russia', '1891-05-15');
insert into author (first_name, middle_name, last_name, country, date_of_birth) values ('Mikhail', 'Aleksandrovich', 'Sholokhov', 'Russia', '1905-05-24');

insert into book_author (book_id, author_id) values (1, 1);
insert into book_author (book_id, author_id) values (2, 1);
insert into book_author (book_id, author_id) values (3, 2);
insert into book_author (book_id, author_id) values (4, 3);
insert into book_author (book_id, author_id) values (5, 4);

insert into book_loan (book_id, user_id) values (1, 1);
insert into book_loan (book_id, user_id) values (2, 2);
insert into book_loan (book_id, user_id) values (3, 2);
