do $$
begin
for i in 1..10000 loop
insert into public.inbox(id, source, type, payload) values(
  gen_random_uuid(),
  'batch_insert',
  'BookChanged',
  '{"current": {"id": 3, "name": "The Cherry Orchard", "authors": [{"id": 2, "country": "Russia", "lastName": "Chekhov", "firstName": "Anton", "middleName": "Pavlovich", "dateOfBirth": "1860-01-29"}], "currentLoan": {"id": 3, "userId": 2}, "publicationYear": 1905}, "previous": {"id": 3, "name": "The Cherry Orchard", "authors": [{"id": 2, "country": "Russia", "lastName": "Chekhov", "firstName": "Anton", "middleName": "Pavlovich", "dateOfBirth": "1860-01-29"}], "currentLoan": {"id": 3, "userId": 2}, "publicationYear": 1904}}'
);
end loop;
end;
$$;
