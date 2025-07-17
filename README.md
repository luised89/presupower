La gestión de presupuestos en construcción enfrenta desafíos clave: falta de automatización en el emparejamiento de materiales, cálculos manuales propensos a errores y dificultad para acceder a precios actualizados. Proponemos a “Presupower” como solución a estas problemáticas, combinando catálogos técnicos con IA para optimizar costos, garantizar precisión y agilizar la creación de presupuestos.

El propósito de “Presupower” es automatizar la gestión de presupuestos de construcción mediante el procesamiento inteligente de listas de materiales (APU - Análisis de Precios Unitarios). Se encuentra desarrollada bajo las siguientes características:

Base de datos: MySQL \n 
Puerto: 3306 \n
Nombre base de datos: “user-presupower” \n
Backend: java 17+ Spring Boot - Java \n
Frontend: Java swing - Java \n

├── src/  
│   ├── main/  
│   │   ├── java/  
│   │   │   ├── screen/          # Interfaces gráficas (JFrame)  
│   │   │   ├── funtion/         # Lógica de negocio (BD, validaciones)  
│   │   │   ├── component/       # Modelos (Material, Usuario, APU)  
│   │   │   ├── dpsk/            # Integración con DeepSeek API ← ¡Nuevo!  
│   │   ├── resources/           # Archivos de configuración  
│   │   │   ├── DSapiK.txt       # API Key de DeepSeek  
│   │   │   ├── datos.json       # Datos temporales de Excel  
│   ├── test/                    # Pruebas unitarias (JUnit)  
├── lib/                         # Dependencias externas (.jar)  
│   ├── gson-2.8.9.jar           # Procesamiento JSON  
│   ├── okhttp-4.9.3.jar         # Cliente HTTP para APIs  

Documentacion:
https://docs.google.com/document/d/1n0JBGc4D7OdEUWwI7eq-0ShQUWGXRBjgh1FHZoqJhj8/edit?usp=sharing
