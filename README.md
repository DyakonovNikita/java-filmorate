# java-filmorate
Приложение с фильмами на Java.

### Sql схема проекта:
![sql-schema](sql.png)

### Примеры запросов:
Получение имен друзей пользователя c id=1
```sql
SELECT name
FROM users
WHERE id IN (SELECT request_recepient_id
             FROM friends
             WHERE status=true
                   AND request_sender_id=1) OR
      id IN (SELECT request_sender_id
             FROM friends
             WHERE status=true
                   AND request_recepient_id=1);
```
Получение рейтинга mpa фильма с id=1
```sql
SELECT name
FROM rating_mpa
WHERE id=(SELECT rating_mpa_id
          FROM films
          WHERE id=1);
```
