# java-filmorate
Приложение с фильмами на Java.

### Sql схема проекта:
![sql-schema](sql-schema.png)

### Примеры запросов:
Получение имен друзей пользователя по id
```sql
SELECT name
FROM users
WHERE id IN (SELECT request_recepient_id
             FROM friends
             WHERE status=true) OR
      id IN (SELECT request_sender_id
             FROM friends
             WHERE status=true)
```
Получение 
