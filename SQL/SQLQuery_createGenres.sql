DROP TABLE GENRES;
CREATE TABLE GENRES (
    id INT PRIMARY KEY IDENTITY,
    genreName NVARCHAR(255) NOT NULL UNIQUE
);