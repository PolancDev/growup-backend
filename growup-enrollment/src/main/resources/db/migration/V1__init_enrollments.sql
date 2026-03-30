-- V1__init_enrollments.sql
-- Tablas para Enrollment Service (Puerto 8083)
-- Base de datos: growup_enrollment

-- Tabla: enrollments
CREATE TABLE enrollments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    course_id UUID NOT NULL,
    progress INTEGER DEFAULT 0 NOT NULL,
    last_access_date TIMESTAMP WITH TIME ZONE,
    enrollment_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    next_lesson_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    CONSTRAINT uq_enrollment_user_course UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX idx_enrollments_course_id ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(enrollment_status);
CREATE INDEX idx_enrollments_deleted_at ON enrollments(deleted_at);

-- Tabla: reviews
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL,
    student_id UUID NOT NULL,
    rating INTEGER NOT NULL,
    comment VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    CONSTRAINT uq_review_course_student UNIQUE (course_id, student_id),
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5)
);

CREATE INDEX idx_reviews_course_id ON reviews(course_id);
CREATE INDEX idx_reviews_student_id ON reviews(student_id);
CREATE INDEX idx_reviews_deleted_at ON reviews(deleted_at);

-- Tabla: activities
CREATE TABLE activities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    action VARCHAR(255) NOT NULL,
    target VARCHAR(255) NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    type VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_activities_user_id ON activities(user_id);
CREATE INDEX idx_activities_time ON activities(time);
CREATE INDEX idx_activities_type ON activities(type);
CREATE INDEX idx_activities_deleted_at ON activities(deleted_at);

-- Comentarios
COMMENT ON TABLE enrollments IS 'Inscripciones de estudiantes a cursos';
COMMENT ON TABLE reviews IS 'Valoraciones y reseñas de cursos';
COMMENT ON TABLE activities IS 'Registro de actividades de aprendizaje';
