SELECT name from v$services;
SELECT instance_name from v$instance;

CREATE USER marsh IDENTIFIED BY MARSH;
GRANT CONNECT, RESOURCE TO marsh;
ALTER USER marsh QUOTA UNLIMITED ON USERS;