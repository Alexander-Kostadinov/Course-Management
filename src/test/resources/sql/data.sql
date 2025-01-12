CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO teachers(first_name, last_name, email)
VALUES ('Angel', 'Angelov', 'angel.angelov@example.com'),
       ('Alex', 'Aleksandrov', 'alex.aleksandrov@example.com');

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255) NOT NULL,
    teacher_id BIGINT NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

INSERT INTO courses(name, status, teacher_id)
VALUES ('Math', 'ACTIVE', 1),
       ('Physics', 'PENDING', 1),
       ('Java Basics', 'PENDING', 2);

CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO students(first_name, last_name, email)
VALUES ('Ivan', 'Ivanov', 'ivan.ivanov@example.com'),
       ('Petar', 'Petrov', 'petar.petrov@example.com');

CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (student_id) REFERENCES students(id)
);

INSERT INTO enrollments(status, course_id, student_id)
VALUES ('Successful', 1, 1),
       ('Unsuccessful', 2, 1),
       ('Unsuccessful', 2, 2);

CREATE TABLE IF NOT EXISTS student_courses(
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

INSERT INTO student_courses(student_id, course_id)
VALUES (1, 1),
       (1, 2),
       (2, 2);

CREATE TABLE IF NOT EXISTS grades(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    grade_value DOUBLE NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

INSERT INTO grades(grade_value, student_id, course_id, teacher_id)
VALUES (5, 1, 1, 1);

