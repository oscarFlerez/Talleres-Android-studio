CREATE DATABASE IF NOT EXISTS crudphpjson
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE crudphpjson;

CREATE TABLE IF NOT EXISTS Usuarios (
    email VARCHAR(254) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(150) NOT NULL
);