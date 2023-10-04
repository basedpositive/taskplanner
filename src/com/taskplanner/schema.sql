CREATE TABLE users (
	user_id SERIAL PRIMARY KEY,
	username VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
	role_id SERIAL PRIMARY KEY,
	role_name VARCHAR(50) NOT NULL UNIQUE
);
INSERT INTO roles (role_name) VALUES ('admin'), ('user');

CREATE TABLE user_role (
	user_role_id SERIAL PRIMARY KEY,
	user_id INT REFERENCES users(user_id),
	role_id INT REFERENCES roles(role_id)
);