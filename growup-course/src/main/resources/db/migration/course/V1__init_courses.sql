-- V2__init_courses.sql
-- Tablas para Course Service (Puerto 8082)
-- Base de datos: growup_course

-- Tabla: courses
CREATE TABLE courses (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    image_url VARCHAR(500),
    category VARCHAR(255) NOT NULL,
    level VARCHAR(50),
    price DOUBLE PRECISION NOT NULL,
    duration DECIMAL(10,2),
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    publication_status VARCHAR(50) NOT NULL DEFAULT 'BORRADOR',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    instructor_id UUID NOT NULL,
    enrolled_count INTEGER DEFAULT 0,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_courses_instructor_id ON courses(instructor_id);
CREATE INDEX idx_courses_category ON courses(category);
CREATE INDEX idx_courses_publication_status ON courses(publication_status);
CREATE INDEX idx_courses_deleted_at ON courses(deleted_at);

-- Tabla: course_modules
CREATE TABLE course_modules (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    module_order INTEGER,
    course_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_course_modules_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE INDEX idx_course_modules_course_id ON course_modules(course_id);
CREATE INDEX idx_course_modules_deleted_at ON course_modules(deleted_at);

-- Tabla: module_topics
CREATE TABLE module_topics (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration INTEGER NOT NULL,
    module_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_module_topics_module FOREIGN KEY (module_id) REFERENCES course_modules(id)
);

CREATE INDEX idx_module_topics_module_id ON module_topics(module_id);
CREATE INDEX idx_module_topics_deleted_at ON module_topics(deleted_at);

-- Comentarios
COMMENT ON TABLE courses IS 'Tabla principal de cursos';
COMMENT ON TABLE course_modules IS 'Módulos que pertenecen a un curso';
COMMENT ON TABLE module_topics IS 'Temas que pertenecen a un módulo';
