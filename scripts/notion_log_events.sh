#!/bin/bash
# Script para registrar eventos técnicos en Notion Project Logbook
# Requiere: NOTION_API_KEY y NOTION_LOGBOOK_DB_ID

# Verificar variables
if [ -z "$NOTION_API_KEY" ] || [ -z "$NOTION_LOGBOOK_DB_ID" ]; then
    echo "Error: Faltan variables de entorno"
    echo "NOTION_API_KEY: ${NOTION_API_KEY:-NO CONFIGURADA}"
    echo "NOTION_LOGBOOK_DB_ID: ${NOTION_LOGBOOK_DB_ID:-NO CONFIGURADA}"
    exit 1
fi

NOTION_HEADERS=(
    -H "Authorization: Bearer $NOTION_API_KEY"
    -H "Content-Type: application/json"
    -H "Notion-Version: 2022-06-28"
)

# 1. REGISTRAR DECISIÓN TÉCNICA: Cambio de Java 26 a Java 17
echo "=== Registrando Decisión Técnica: Cambio de Java 26 a Java 17 ==="

curl -X POST "https://api.notion.com/v1/pages" \
    "${NOTION_HEADERS[@]}" \
    -d '{
        "parent": {
            "database_id": "'"$NOTION_LOGBOOK_DB_ID"'"
        },
        "properties": {
            "Name": {
                "title": [
                    {
                        "text": {
                            "content": "Cambio de Java 26 a Java 17 para demo"
                        }
                    }
                ]
            },
            "Type": {
                "select": {
                    "name": "Decision"
                }
            },
            "Description": {
                "rich_text": [
                    {
                        "text": {
                            "content": "Se decidió cambiar de Java 26 a Java 17 para la demo. Motivo: Incompatibilidad de Lombok y Spring Boot 3.2.2 con Java 26. Java 17 es la versión LTS recomendada para Spring Boot 3.x."
                        }
                    }
                ]
            },
            "Date": {
                "date": {
                    "start": "2026-05-01"
                }
            }
        }
    }'

echo -e "\n"

# 2. REGISTRAR SOLUCIÓN: Configuración de microservicios para demo local
echo "=== Registrando Solución: Configuración de demo local sin Docker ==="

curl -X POST "https://api.notion.com/v1/pages" \
    "${NOTION_HEADERS[@]}" \
    -d '{
        "parent": {
            "database_id": "'"$NOTION_LOGBOOK_DB_ID"'"
        },
        "properties": {
            "Name": {
                "title": [
                    {
                        "text": {
                            "content": "Configuración de microservicios para demo local sin Docker"
                        }
                    }
                ]
            },
            "Type": {
                "select": {
                    "name": "Solution"
                }
            },
            "Status": {
                "select": {
                    "name": "Done"
                }
            },
            "Area": {
                "multi_select": [
                    { "name": "Backend" },
                    { "name": "Configuration" },
                    { "name": "Infrastructure" }
                ]
            },
            "Description": {
                "rich_text": [
                    {
                        "text": {
                            "content": "Se configuró growup-auth para demo local sin Docker:\n- Se cambió a base de datos H2 en memoria (modificando pom.xml y application.yml)\n- Se deshabilitó temporalmente OAuth2 (Keycloak) para permitir el arranque\n- Esto permite ejecutar la demo localmente sin dependencias de contenedores"
                        }
                    }
                ]
            },
            "Date": {
                "date": {
                    "start": "2026-05-01"
                }
            }
        }
    }'

echo -e "\n"

# 3. REGISTRAR ESTADO DE LA DEMO
echo "=== Registrando Estado de la Demo ==="

curl -X POST "https://api.notion.com/v1/pages" \
    "${NOTION_HEADERS[@]}" \
    -d '{
        "parent": {
            "database_id": "'"$NOTION_LOGBOOK_DB_ID"'"
        },
        "properties": {
            "Name": {
                "title": [
                    {
                        "text": {
                            "content": "Estado de servicios - Demo local activa"
                        }
                    }
                ]
            },
            "Type": {
                "select": {
                    "name": "Solution"
                }
            },
            "Status": {
                "select": {
                    "name": "Done"
                }
            },
            "Area": {
                "multi_select": [
                    { "name": "Backend" },
                    { "name": "Demo" }
                ]
            },
            "Description": {
                "rich_text": [
                    {
                        "text": {
                            "content": "Estado actual de la demo local:\n- growup-discovery corriendo en puerto 8761 (Eureka Server)\n- growup-auth corriendo en puerto 8081\n- Microservicios principales operativos para demostración"
                        }
                    }
                ]
            },
            "Date": {
                "date": {
                    "start": "2026-05-01"
                }
            }
        }
    }'

echo -e "\n"

# 4. REGISTRAR COMANDOS CLAVE (como solución/tip técnico)
echo "=== Registrando Comandos Clave de Configuración ==="

curl -X POST "https://api.notion.com/v1/pages" \
    "${NOTION_HEADERS[@]}" \
    -d '{
        "parent": {
            "database_id": "'"$NOTION_LOGBOOK_DB_ID"'"
        },
        "properties": {
            "Name": {
                "title": [
                    {
                        "text": {
                            "content": "Configuración de JAVA_HOME para Maven"
                        }
                    }
                ]
            },
            "Type": {
                "select": {
                    "name": "Solution"
                }
            },
            "Status": {
                "select": {
                    "name": "Done"
                }
            },
            "Area": {
                "multi_select": [
                    { "name": "Backend" },
                    { "name": "Configuration" }
                ]
            },
            "Description": {
                "rich_text": [
                    {
                        "text": {
                            "content": "Comando clave para configurar el entorno antes de usar Maven con Java 17:\nset JAVA_HOME=C:\\Program Files\\java\\jdk-17\n\nEste paso es necesario para asegurar que Maven use la versión correcta de Java (17) y no la 26 que estaba causando incompatibilidades."
                        }
                    }
                ]
            },
            "Date": {
                "date": {
                    "start": "2026-05-01"
                }
            }
        }
    }'

echo -e "\n=== Registro completado ==="
