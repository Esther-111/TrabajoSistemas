    import streamlit as st
    import requests
    import os
    from dotenv import load_dotenv

    # Importamos TODAS las IAs que vayamos a usar
    from langchain_google_genai import ChatGoogleGenerativeAI
    from langchain_ollama import ChatOllama
    from langchain_groq import ChatGroq

    # Permite convertir funciones de Python en herramientas ejecutables por la IA (@tool)
    from langchain_core.tools import tool

    # Estructuras de datos para clasificar el historial de la conversación (Rol: Sistema, Usuario, IA)
    from langchain_core.messages import HumanMessage, AIMessage, SystemMessage

    # Construye el motor principal del agente usando la arquitectura ReAct (Razonamiento + Acción)
    from langgraph.prebuilt import create_react_agent

    # Tipado estricto de Python para definir variables complejas en los parámetros de las herramientas
    from typing import List, Dict

    # 1. Cargar la contraseña
    load_dotenv()

    # Buzon temporal para poedr dibujar graficos
    graficos_pendientes = []


    # --- 1. INTERFAZ: MENÚ LATERAL PARA ELEGIR LA IA ---
    st.sidebar.title("⚙️ Configuración del Agente")
    st.sidebar.markdown("Elige el modelo para hacer la comparativa:")

    # Creamos el desplegable
    modelo_elegido = st.sidebar.selectbox(
        "Inteligencia Artificial:",
        ("Gemini (Google)", "Ollama (Local)", "Groq (Nube)")
    )

    # --- 2. INICIALIZAR EL CEREBRO ELEGIDO ---
    if modelo_elegido == "Gemini (Google)":
        clave_gemini = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY")

        # Nos aseguramos de que la clave no falta en el .env
        if not clave_gemini:
            st.error("⚠️ Falta la clave de Google (GEMINI_API_KEY) en el archivo .env")
            st.stop()

        llm = ChatGoogleGenerativeAI(
            model="gemini-2.5-flash",
            api_key=clave_gemini,
            temperature=0
        )

    elif modelo_elegido == "Ollama (Local)":
        # Ollama no necesita clave, pero necesita que el programa esté instalado en tu PC
        llm = ChatOllama(model="llama3.1", temperature=0)

    elif modelo_elegido == "Groq (Nube)":
        clave_groq = os.getenv("GROQ_API_KEY")

        # Nos aseguramos de que la clave no falta en el .env
        if not clave_groq:
            st.error("⚠️ Falta la clave de Groq (GROQ_API_KEY) en el archivo .env")
            st.stop()

        llm = ChatGroq(
            temperature=0,
            api_key=clave_groq,
            model_name="llama-3.3-70b-versatile"
        )


    # --- 3. CREACIÓN DE LAS HERRAMIENTAS ---

    @tool
    def consultar_medicamentos():
        """Consulta todos los medicamentos en la base de datos."""
        try:
            respuesta = requests.get("http://localhost:8081/api/medicamentos")
            if respuesta.status_code == 200:
                return str(respuesta.json()) # ¡Esto devuelve los datos reales!
            else:
                return f"Error en el servidor: {respuesta.status_code}"
        except Exception as e:
            return "No se pudo conectar con el servidor Java."

    @tool
    def registrar_medicamento(
        idLaboratorio: str,
        nombreComercial: str,
        principioActivo: str,
        dosis: str,
        formaFarmaceutica: str,
        cantidadStock: int,
        precio: float,
        fechaCaducidad: str
    ):
        """Útil para registrar o añadir un nuevo medicamento a la base de datos de la farmacia.
        Si el usuario no da TODOS estos datos en su petición, PREGUNTAMOS los datos que faltan antes de ejecutar la herramienta.
        La fechaCaducidad debe tener estrictamente el formato YYYY-MM-DD."""
        try:
            # las claves de la izquierda son EXACTAMENTE nuestras variables de Java
            nuevo_medicamento = {
                "idLaboratorio": idLaboratorio,
                "nombreComercial": nombreComercial,
                "principioActivo": principioActivo,
                "dosis": dosis,
                "formaFarmaceutica": formaFarmaceutica,
                "cantidadStock": cantidadStock,
                "precio": precio,
                "fechaCaducidad": fechaCaducidad
            }

            # Hacemos la petición POST al servidor
            respuesta = requests.post(
                "http://localhost:8081/api/medicamentos",
                json=nuevo_medicamento
            )

            if respuesta.status_code in [200, 201]:
                return f"Éxito total. El medicamento {nombreComercial} ha sido guardado en la base de datos."
            else:
                return f"Error al guardar. Código: {respuesta.status_code}. Detalle: {respuesta.text}"
        except Exception as e:
            return "El servidor backend de Java está apagado o no responde."

    @tool
    def eliminar_medicamento(id_medicamento: str):
        """Útil para eliminar un medicamento del stock.
        REQUIERE el ID exacto del medicamento. Si el usuario pide eliminar un medicamento por su nombre (ej: 'elimina el paracetamol'), DEBEMOS usar primero la herramienta consultar_medicamentos para buscar cuál es su ID exacto, y después usar esta herramienta para borrarlo."""
        try:
            # Hacemos la petición DELETE al servidor Java
            respuesta = requests.delete(f"http://localhost:8081/api/medicamentos/{id_medicamento}")

            # Spring Boot suele devolver 200 (OK) o 204 (No Content) cuando borra con éxito
            if respuesta.status_code in [200, 202, 204]:
                return f"Éxito total. El medicamento con ID {id_medicamento} ha sido eliminado."
            else:
                return f"Error al eliminar. Código: {respuesta.status_code}. Detalle: {respuesta.text}"
        except Exception as e:
            return "El servidor backend de Java está apagado o no responde."

    @tool
    def actualizar_medicamento(
        id_medicamento: str,
        id_laboratorio: str,
        nombre_comercial: str,
        principio_activo: str,
        dosis: str,
        forma_farmaceutica: str,
        cantidad_stock: int,
        precio: float,
        fecha_caducidad: str
    ) -> str:
        """Actualiza los datos completos de un medicamento existente en la base de datos."""
        try:
            url = f"http://localhost:8081/api/medicamentos/{id_medicamento}"

            payload = {
                "idLaboratorio": id_laboratorio,
                "nombreComercial": nombre_comercial,
                "principioActivo": principio_activo,
                "dosis": dosis,
                "formaFarmaceutica": forma_farmaceutica,
                "cantidadStock": cantidad_stock,
                "precio": precio,
                "fechaCaducidad": fecha_caducidad
            }

            #Usamos requests.put en lugar de post o get
            respuesta = requests.put(url, json=payload)

            if respuesta.status_code == 200:
                return f"Medicamento con ID {id_medicamento} actualizado correctamente."
            elif respuesta.status_code == 404:
                return "Error: No se ha encontrado el medicamento para actualizar."
            elif respuesta.status_code == 400:
                return "Error 400: Faltan datos o el formato es incorrecto."
            else:
                return f"Error en el servidor al actualizar: {respuesta.status_code}"
        except Exception as e:
            return f"Error de conexión con el servidor Java: {str(e)}"

    @tool
    def consultar_laboratorios():
        """Útil para consultar la lista de laboratorios o empresas proveedoras de la farmacia."""
        try:
            # Suponemos que la ruta sigue el mismo estándar REST que los medicamentos
            respuesta = requests.get("http://localhost:8081/api/laboratorios")

            if respuesta.status_code == 200:
                return respuesta.text
            else:
                return f"Error al consultar laboratorios. Código: {respuesta.status_code}"
        except Exception as e:
            return "El servidor backend de Java está apagado o no responde."

    @tool
    def registrar_laboratorio(
        nombreEmpresa: str,
        telefonoContacto: str,
        emailPedidos: str,
        direccionFiscal: str
    ):
        """Útil para registrar o añadir un nuevo laboratorio proveedor a la base de datos.
        Si el usuario no da TODOS estos datos en su petición, PREGUNTAMOS los datos que faltan antes de ejecutar la herramienta."""
        try:

            nuevo_laboratorio = {
                "nombreEmpresa": nombreEmpresa,
                "telefonoContacto": telefonoContacto,
                "emailPedidos": emailPedidos,
                "direccionFiscal": direccionFiscal
            }

            respuesta = requests.post(
                "http://localhost:8081/api/laboratorios",
                json=nuevo_laboratorio
            )

            if respuesta.status_code in [200, 201]:
                return f"Éxito total. El laboratorio {nombreEmpresa} ha sido registrado."
            else:
                return f"Error al guardar. Código: {respuesta.status_code}. Detalle: {respuesta.text}"
        except Exception as e:
            return "El servidor backend de Java está apagado o no responde."

    @tool
    def eliminar_laboratorio(id_laboratorio: str) -> str:
        """Elimina un laboratorio de la base de datos utilizando su ID."""
        try:

            url = f"http://localhost:8081/api/laboratorios/{id_laboratorio}"
            respuesta = requests.delete(url)

            # 200 (OK) o 204 (No Content) son respuestas de éxito comunes al borrar
            if respuesta.status_code in [200, 204]:
                return f"Laboratorio con ID {id_laboratorio} eliminado correctamente."
            elif respuesta.status_code == 404:
                return "Error: No se ha encontrado ningún laboratorio con ese ID."
            else:
                return f"Error al eliminar: Código {respuesta.status_code}"
        except Exception as e:
            return f"Error de conexión con el servidor Java: {str(e)}"

    @tool
    def actualizar_laboratorio(
        id_laboratorio: str,
        nombre_empresa: str,
        telefono_contacto: str,
        email_pedidos: str,
        direccion_fiscal: str
    ) -> str:
        """Actualiza los datos completos de un laboratorio existente en la base de datos."""
        try:
            url = f"http://localhost:8081/api/laboratorios/{id_laboratorio}"

            payload = {
                "nombreEmpresa": nombre_empresa,
                "telefonoContacto": telefono_contacto,
                "emailPedidos": email_pedidos,
                "direccionFiscal": direccion_fiscal
            }

            respuesta = requests.put(url, json=payload)

            # Spring Boot suele devolver 200 OK cuando el PUT es exitoso
            if respuesta.status_code == 200:
                return f"Laboratorio con ID {id_laboratorio} actualizado correctamente."
            elif respuesta.status_code == 404:
                return "Error: No se ha encontrado el laboratorio para actualizar."
            elif respuesta.status_code == 400:
                return "Error 400: Petición malformada. Revisa los datos enviados."
            else:
                return f"Error en el servidor al actualizar: {respuesta.status_code}"
        except Exception as e:
            return f"Error de conexión con el servidor Java: {str(e)}"

    @tool
    def consultar_pedidos():
        """Útil para consultar el historial de pedidos de abastecimiento realizados a los laboratorios."""
        try:
            respuesta = requests.get("http://localhost:8081/api/pedidos")
            if respuesta.status_code == 200:
                return respuesta.text
            else:
                return f"Error al consultar. Código: {respuesta.status_code}"
        except Exception as e:
            return "El servidor backend de Java está apagado o no responde."

    @tool
    def registrar_pedido(idLaboratorio: str, listaMedicamentos: List[Dict]):
        """Útil para registrar un nuevo pedido de abastecimiento a un laboratorio.
        REGLAS ESTRICTAS PARA USAR ESTA HERRAMIENTA:
        1. Si el usuario pide el nombre del laboratorio o del medicamento, DEBES usar antes las herramientas consultar_laboratorios y consultar_medicamentos para averiguar los IDs exactos.
        2. El parámetro 'listaMedicamentos' debe ser obligatoriamente una lista de diccionarios, donde cada diccionario tiene exactamente dos claves: 'idMedicamento' (string) y 'cantidad' (entero).
        Ejemplo de formato: [{"idMedicamento": "abc...", "cantidad": 50}]"""
        try:

            nuevo_pedido = {
                "idLaboratorio": idLaboratorio,
                "listaMedicamentos": listaMedicamentos
            }

            respuesta = requests.post(
                "http://localhost:8081/api/pedidos",
                json=nuevo_pedido
            )

            if respuesta.status_code in [200, 201]:
                return "Éxito total. El pedido ha sido registrado correctamente en la base de datos."
            else:
                return f"Error al guardar. Código: {respuesta.status_code}. Detalle: {respuesta.text}"
        except Exception as e:
            return "El servidor backend de Java está apagado o no responde."

    @tool
    def registrar_desecho(id_medicamento: str, cantidad_perdida: int) -> str:
        """Registra una orden de desecho para retirar medicamentos del inventario (ej. por caducidad o rotura)."""
        try:
            url = "http://localhost:8081/api/desechos"

            payload = {
                "idMedicamento": id_medicamento,
                "cantidadPerdida": cantidad_perdida
            }
            respuesta = requests.post(url, json=payload)

            if respuesta.status_code in [200, 201]:
                return f"Desecho registrado con éxito. Se han restado {cantidad_perdida} unidades del stock de ese medicamento."
            elif respuesta.status_code == 400:
                return "Error 400: Petición malformada. Revisa los datos enviados."
            elif respuesta.status_code == 404:
                return "Error 404: No se ha encontrado el medicamento."
            elif respuesta.status_code == 500:
                return "Error 500: Fallo en el servidor (posiblemente falta de stock suficiente para restar)."
            else:
                return f"Error al registrar el desecho: Código {respuesta.status_code}"
        except Exception as e:
            return f"Error de conexión con el servidor Java: {str(e)}"

    @tool
    def mostrar_grafico_gastos():
        """Útil cuando el usuario pide ver las estadísticas de gastos, cuánto se ha gastado por laboratorio o facturación."""
        global graficos_pendientes
        try:
            respuesta = requests.get("http://localhost:8081/api/estadisticas/gastos")
            if respuesta.status_code == 200:
                datos_json = respuesta.json()
                datos_limpios = {item[list(item.keys())[0]]: item[list(item.keys())[1]] for item in datos_json}

                # Guardamos los datos en la lista global en lugar de intentar dibujarlos aquí
                graficos_pendientes.append(datos_limpios)

                return "Gráfico de gastos obtenido con éxito. Dile al usuario que se muestra en pantalla."
            else:
                return "Error al consultar las estadísticas."
        except Exception as e:
            return "El servidor de Java está apagado."

    @tool
    def mostrar_grafico_desechos():
        """Útil cuando el usuario pide ver las estadísticas de desechos, mermas, pérdidas o medicamentos caducados por mes."""
        global graficos_pendientes
        try:
            respuesta = requests.get("http://localhost:8081/api/estadisticas/desechos")
            if respuesta.status_code == 200:
                datos_json = respuesta.json()
                datos_limpios = {item[list(item.keys())[0]]: item[list(item.keys())[1]] for item in datos_json}

                graficos_pendientes.append(datos_limpios)

                return "Gráfico de desechos obtenido con éxito. Dile al usuario que se muestra en pantalla."
            else:
                return "Error al consultar las estadísticas."
        except Exception as e:
            return "El servidor de Java está apagado."

    # Lista de brazos del Agente
    herramientas = [
        consultar_medicamentos,
        registrar_medicamento,
        eliminar_medicamento,
        actualizar_medicamento,
        consultar_laboratorios,
        registrar_laboratorio,
        eliminar_laboratorio,
        actualizar_laboratorio,
        consultar_pedidos,
        registrar_pedido,
        registrar_desecho,
        mostrar_grafico_gastos,
        mostrar_grafico_desechos
    ]

    # --- 4. CONFIGURACIÓN DEL AGENTE ---
    #El ejecutor coge automáticamente el 'llm' que se configuró arriba en el menú lateral
    ejecutor = create_react_agent(llm, herramientas)

    # --- 5. INTERFAZ VISUAL DE STREAMLIT ---
    st.set_page_config(page_title="ERP Farmacia", page_icon="💊")
    st.title("💊 Asistente del ERP - Farmacia")
    st.write("Bienvenido al sistema. Pídeme que consulte el stock o que registre un nuevo medicamento.")

    if "historial" not in st.session_state:
        st.session_state.historial = []

    for mensaje in st.session_state.historial:
        with st.chat_message(mensaje["rol"]):
            st.markdown(mensaje["texto"])

    texto_usuario = st.chat_input("Ej: Registra 50 cajas de Paracetamol a 3.50 euros")

    if texto_usuario:
        with st.chat_message("user"):
            st.markdown(texto_usuario)
        st.session_state.historial.append({"rol": "user", "texto": texto_usuario})

        historial_ia = [
            SystemMessage(content="Eres el asistente del ERP de una farmacia. Usa tus herramientas para buscar o guardar información en el backend de Java. Sé conversacional, profesional y directo.")
        ]

        for msg in st.session_state.historial:
            if msg["rol"] == "user":
                historial_ia.append(HumanMessage(content=msg["texto"]))
            else:
                historial_ia.append(AIMessage(content=msg["texto"]))

        with st.spinner("Procesando la orden con el sistema central..."):
            resultado = ejecutor.invoke({"messages": historial_ia})
            # Extraemos el contenido crudo
            contenido_crudo = resultado["messages"][-1].content

            # Si es una lista compleja, sacamos solo el texto limpio
            if isinstance(contenido_crudo, list):
                respuesta_texto = contenido_crudo[0].get("text", "")
            # Si ya es un texto normal, lo dejamos tal cual
            else:
                respuesta_texto = contenido_crudo

        with st.chat_message("assistant"):
            st.markdown(respuesta_texto)

            # --- AQUÍ AÑADIMOS LA LÓGICA DEL BUZÓN ---
            if len(graficos_pendientes) > 0:
                for grafico_datos in graficos_pendientes:
                    # Dibujamos el gráfico en la burbuja del robot
                    st.bar_chart(grafico_datos)

                # Limpiamos la lista para la próxima vez
                graficos_pendientes.clear()

        # Guardamos el texto en el historial
        st.session_state.historial.append({"rol": "assistant", "texto": respuesta_texto})