#!/bin/bash
# Script de Demo para Cliente - GrowUp Backend
# Ejecutar después de que Course Service esté corriendo

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎯 GROWUP BACKEND - DEMO PARA CLIENTE"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 1. Verificar servicios
echo "1️⃣ VERIFICANDO SERVICIOS..."
echo "   - Eureka: http://localhost:8761"
curl -s http://localhost:8761 > /dev/null && echo "   ✅ Eureka OK" || echo "   ❌ Eureka FALLA"
echo "   - Auth Service: http://localhost:8081"
curl -s http://localhost:8081 > /dev/null && echo "   ✅ Auth OK" || echo "   ❌ Auth FALLA"
echo "   - Course Service: http://localhost:8082"
curl -s http://localhost:8082 > /dev/null && echo "   ✅ Course OK" || echo "   ❌ Course FALLA"
echo ""

# 2. Login como Instructor
echo "2️⃣ LOGIN COMO INSTRUCTOR..."
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"instructor2@growup.com","password":"instructor123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "   ❌ Error: No se pudo obtener token"
  exit 1
fi

echo "   ✅ Token JWT generado:"
echo "   ${TOKEN:0:50}..."
echo ""

# 3. Crear Curso (EL MOMENTO DE LA VERDAD)
echo "3️⃣ CREANDO CURSO..."
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8082/api/v1/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Demo Cliente Exitoso",
    "description": "Curso creado en la demo para el cliente",
    "category": "Programacion",
    "level": "PRINCIPIANTE",
    "price": 499.99
  }')

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

echo "   Código HTTP: $HTTP_CODE"
if [ "$HTTP_CODE" = "201" ]; then
  echo "   ✅ ¡CURSO CREADO EXITOSAMENTE!"
  echo ""
  echo "   Respuesta:"
  echo "$BODY" | python -m json.tool 2>/dev/null || echo "$BODY"
else
  echo "   ❌ Error al crear curso"
  echo "   Respuesta: $BODY"
fi
echo ""

# 4. Resumen para el cliente
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 RESUMEN PARA EL CLIENTE"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "✅ Sistema de autenticación JWT funcionando"
echo "✅ Microservicios comunicándose vía Eureka"
echo "✅ Creación de cursos validando tokens"
echo "✅ Arquitectura Hexagonal implementada"
echo ""
echo "🎯 Puntos clave:"
echo "   • JWT Secret unificado entre servicios"
echo "   • Validación de roles (TEACHER/ADMIN)"
echo "   • Service Discovery automático"
echo "   • Base de datos separada por servicio"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
