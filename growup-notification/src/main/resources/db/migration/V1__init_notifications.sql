-- V1__init_notifications.sql
-- Tablas para Notification Service (Puerto 8084)
-- Base de datos: growup_notification

-- Tabla: notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    read BOOLEAN DEFAULT FALSE NOT NULL,
    type VARCHAR(50) NOT NULL,
    link VARCHAR(500),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(read);
CREATE INDEX idx_notifications_date ON notifications(date);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_deleted_at ON notifications(deleted_at);

-- Comentarios
COMMENT ON TABLE notifications IS 'Notificaciones del sistema';
