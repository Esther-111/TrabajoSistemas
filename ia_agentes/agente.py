import streamlit as st
import requests
import os
from dotenv import load_dotenv

# Importamos TODAS las IAs que vayamos a usar
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_ollama import ChatOllama
# Cuando tengáis la tercera IA, su import irá aquí

from langchain_core.tools import tool
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langgraph.prebuilt import create_react_agent
from typing import List, Dict

# 1. Cargar las contraseñas del .env
load_dotenv()

# --- 2. INTERFAZ: MENÚ LATERAL PARA ELEGIR LA IA ---
st.sidebar.title("⚙️ Configuración del Agente")
st.sidebar.markdown("Elige el modelo para hacer la comparativa:")

# Creamos el desplegable
modelo_elegido = st.sidebar.selectbox(
    "Inteligencia Artificial:",
    ("Gemini (Google)", "Ollama (Local)")
    # Aquí añadiréis el nombre de la tercera IA más adelante
)

# --- 3. INICIALIZAR EL CEREBRO ELEGIDO ---
# --- 3. INICIALIZAR EL CEREBRO ELEGIDO ---
if modelo_elegido == "Gemini (Google)":
    # Le obligamos a leer la variable del archivo .env
    clave_gemini = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY")

    # Se la pasamos directamente al modelo para que no dé el error ValidationError
    llm = ChatGoogleGenerativeAI(
        model="gemini-1.5-flash",
        api_key=clave_gemini
    )
elif modelo_elegido == "Ollama (Local)":
    # Ollama no necesita clave, pero necesita que el programa esté instalado en tu PC
    llm = ChatOllama(model="llama3")


# --- 4. CREACIÓN DE LAS HERRAMIENTAS ---

@tool
def consultar_medicamentos():
    """Consulta todos los medicamentos en la base de datos."""
    try:
        # Petición a tu servidor Java
        respuesta = requests.get("http://localhost:8080/api/medicamentos")
        if respuesta.status_code == 200:
            return str(respuesta.json())
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
    Si el usuario no te da TODOS estos datos en su petición, PREGÚNTALE los datos que faltan antes de ejecutar la herramienta.
    La fechaCaducidad debe tener estrictamente el formato YYYY-MM-DD."""
    try:
        # El diccionario espejo: las claves (izquierda) son EXACTAMENTE tus variables de Java
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

        # Hacemos la petición POST a tu servidor
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
    REQUIERE el ID exacto del medicamento. Si el usuario te pide eliminar un medicamento por su nombre (ej: 'elimina el paracetamol'), DEBES usar primero la herramienta consultar_medicamentos para buscar cuál es su ID exacto, y después usar esta herramienta para borrarlo."""
    try:
        # Hacemos la petición DELETE a tu servidor Java
        respuesta = requests.delete(f"http://localhost:8081/api/medicamentos/{id_medicamento}")

        # Spring Boot suele devolver 200 (OK) o 204 (No Content) cuando borra con éxito
        if respuesta.status_code in [200, 202, 204]:
            return f"Éxito total. El medicamento con ID {id_medicamento} ha sido eliminado."
        else:
            return f"Error al eliminar. Código: {respuesta.status_code}. Detalle: {respuesta.text}"
    except Exception as e:
        return "El servidor backend de Java está apagado o no responde."

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
    Si el usuario no te da TODOS estos datos en su petición, PREGÚNTALE los datos que faltan antes de ejecutar la herramienta."""
    try:
        # El diccionario espejo exacto de tu LaboratorioCrearDTO
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
    1. Si el usuario te pide el nombre del laboratorio o del medicamento, DEBES usar antes las herramientas consultar_laboratorios y consultar_medicamentos para averiguar los IDs exactos.
    2. El parámetro 'listaMedicamentos' debe ser obligatoriamente una lista de diccionarios, donde cada diccionario tiene exactamente dos claves: 'idMedicamento' (string) y 'cantidad' (entero).
    Ejemplo de formato: [{"idMedicamento": "abc...", "cantidad": 50}]"""
    try:
        # El diccionario espejo exacto de tu PedidoCrearDTO
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

# ¡Añadimos la nueva herramienta a la lista de brazos del Agente!
herramientas = [
    consultar_medicamentos,
    registrar_medicamento,
    eliminar_medicamento,
    consultar_laboratorios,
    registrar_laboratorio,
    consultar_pedidos,
    registrar_pedido
]

# --- 3. CONFIGURACIÓN DEL AGENTE ---
#llm = ChatGoogleGenerativeAI(model="gemini-2.5-flash", temperature=0)
# Usamos llama3.1 para que trabaje localmente que sí soporta 'tools'
llm = ChatOllama(model="llama3.1", temperature=0)
ejecutor = create_react_agent(llm, herramientas)

# --- 4. INTERFAZ VISUAL DE STREAMLIT ---
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

        # Si es una lista compleja (el paquete feo), sacamos solo el texto limpio
        if isinstance(contenido_crudo, list):
            respuesta_texto = contenido_crudo[0].get("text", "")
        # Si ya es un texto normal, lo dejamos tal cual
        else:
            respuesta_texto = contenido_crudo

    with st.chat_message("assistant"):
        st.markdown(respuesta_texto)
    st.session_state.historial.append({"rol": "assistant", "texto": respuesta_texto})