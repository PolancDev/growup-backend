package com.growup.course.domain.port.out;

import com.growup.course.domain.model.InstructorInfo;

import java.util.UUID;

/**
 * Puerto de Salida para consultar información del instructor.
 * Permite desacoplar Course de User y obtener solo la información necesaria.
 */
public interface InstructorLookupPort {
    
    /**
     * Obtiene la información pública del instructor por su ID.
     * @param instructorId ID del instructor
     * @return InstructorInfo con la información pública del instructor, o null si no existe
     */
    InstructorInfo findInstructorInfoById(UUID instructorId);
}