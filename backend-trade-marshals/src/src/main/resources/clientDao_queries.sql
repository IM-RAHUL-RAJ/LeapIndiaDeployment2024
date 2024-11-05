
SELECT email
FROM client 
WHERE email = 'sowmya@gmail.com';

SELECT c.client_id, c.email, c.password, c.name, c.date_of_birth, c.country, c.is_admin, ci.type, ci.value
FROM client c
INNER JOIN client_identification ci
ON c.client_id = ci.client_id
WHERE c.email= 'sowmya@gmail.com'
ORDER BY c.client_id;

SELECT type, value
FROM client_identification
ORDER BY client_id;