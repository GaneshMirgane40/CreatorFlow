CREATE USER 'creatorflow'@'localhost' IDENTIFIED BY 'creator123';
GRANT ALL PRIVILEGES ON creatorflow.* TO 'creatorflow'@'localhost';
FLUSH PRIVILEGES;
SELECT user, host FROM mysql.user WHERE user = 'creatorflow';
