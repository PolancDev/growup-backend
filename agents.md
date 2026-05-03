# 📋 Contexto y Reglas del Proyecto (Fullstack) - GrowUp

Este documento es la "Constitución" del proyecto. Cualquier agente de IA debe seguir estas reglas estrictamente.

## 🏗️ Resumen del Proyecto

**GrowUp** es una plataforma de aprendizaje digital con arquitectura moderna y escalable.

## 🏛️ Arquitectura

- **Backend:** Arquitectura Hexagonal (Puertos y Adaptadores). Desacoplamiento total.
- **Frontend:** Micro-frontends (Shell Angular) + Remotos (Angular/React) vía Module Federation.

## 🏗️ Stack Tecnológico

- **Frontend:** Angular 21, React 19, Vite 6, Tailwind CSS v4, PrimeNG/PrimeReact.
- **Backend:** Java 17, Spring Boot 3.x, PostgreSQL, MapStruct, OpenAPI (API-First), JWT.

## 🧪 Estándares de Testing

- **Frontend:** Vitest/Jest + Testing Library. Ubicación: junto al componente.
- **Backend:** JUnit 5 + Mockito (Given-When-Then). DB: Testcontainers.
- **Mandato:** No se entrega código sin pasar `npm run test` o `./mvnw test`.

## 🚀 CI/CD y Calidad

- **Pipeline:** GitHub Actions. Prohibido tocar `.github/workflows` sin permiso.
- **Principios:** SOLID, Clean Code, KISS y DRY. Nombres descriptivos siempre.

## 📂 Workflow Diario

1. **Inicio:** Leer este archivo y `todo.md`.
2. **Sincronización:** Verificar estado de tests.
3. **Cierre:** Actualizar `todo.md` y ejecutar Protocolo de Memoria (NotebookLM).

## 🛠️ Comandos Útiles

- **Front:** `npm install`, `npm run lint`, `npm run test`, `npm run build`.
- **Back:** `./mvnw clean compile`, `./mvnw test`, `./mvnw spring-boot:run`.

## 🤖 Gestión de Memoria y NotebookLM (MCP + Skill)

Este proyecto utiliza una combinación de **herramientas MCP** y una **Skill local** para persistir el conocimiento a largo plazo y evitar regresiones.

**Cuaderno Principal:** https://notebooklm.google.com/notebook/0f215aeb-0c9b-49ba-9562-25461ae02500

- **Herramientas de Guardado (MCP):**
  - `mcp_notebooklm_notebook_add_text`: Úsala para añadir resúmenes técnicos en Markdown a un cuaderno de NotebookLM. Es la más rápida para el cierre de jornada.
  - `mcp_notebooklm_notebook_create`: Úsala si necesitas crear un cuaderno nuevo para un módulo específico.
- **Herramientas de Consulta y Gestión (Skill Local):**
  - **Ubicación:** `./.agents/skills/notebooklm`
  - **Comandos:**
    - `python scripts/run.py ask_question.py --question "..."`: Para consultas ricas en contexto de tus documentos.
    - `python scripts/run.py notebook_manager.py list`: Para ver qué cuadernos están orquestados por la skill de navegación.
- **Protocolo de Cierre (Obligatorio):**
  Al finalizar cada hito o jornada, debes generar un resumen técnico para **NotebookLM** que incluya:
  1. **Arquitectura:** Cambios en servicios o flujo de datos.
  2. **Lógica de Negocio:** Explicación de reglas complejas implementadas.
  3. **Troubleshooting:** Soluciones a errores de configuración o bugs difíciles.
- **Acción:** Ejecuta `mcp_notebooklm_notebook_add_text` pasando el resumen en Markdown. **No esperes confirmación manual para documentar si la tarea ha terminado.**
- **Consulta:** Antes de empezar tareas complejas, usa la herramienta `mcp_notebooklm_notebook_query` o la skill `ask_question.py` para buscar si existen notas previas sobre el módulo en cuestión.

## 🔗 Recursos Clave

- **Contrato API:** `growup-ops/specs/openapi.yaml`
- **Dockerfiles:** `growup-ops/deploy/`

# 🤖 Directorio de Subagentes

Este archivo define los perfiles disponibles. Para tareas complejas, consultar el archivo de coordinación.

## 📋 Lista de Agentes
- **[DevAngular](./DevAngular.MD):** Especialista en lógica, Signals y arquitectura Zoneless.
- **[DevStyles](./DevStyles.MD):** Especialista en UI/UX, Tailwind CSS y sistemas de diseño.
- **[DevHTML](./DevHTML.MD):** Especialista en accesibilidad, semántica y SEO técnico.

---

## ⚙️ Protocolo de Operación
Para cualquier solicitud de desarrollo de componentes o vistas:
1. **Consultar el archivo [Orchestrator.md](./.opencode/agents/orquestador-front.MD)** para entender el flujo de trabajo entre agentes.
2. Seguir las directrices de cada archivo `.MD` individual.
3. El resultado final debe ser la suma coordinada de los tres especialistas.