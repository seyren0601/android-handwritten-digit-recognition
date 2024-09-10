import getpass
import os
import json
from fastapi import FastAPI, WebSocket
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI
from langserve import add_routes
from langchain_community.chat_message_histories import SQLChatMessageHistory
from langchain_core.runnables.history import RunnableWithMessageHistory
import asyncio
import time
from handler import CustomHandler
from queue import Queue
from threading import Thread
from fastapi.responses import StreamingResponse

with open('cs_and_api.json', 'r') as file:
    data = json.load(file)
    api_key = data['api_key']
    connection_string = data['connection_string']

streamer_queue = Queue()
my_handler = CustomHandler(streamer_queue)
model = ChatOpenAI(model="gpt-4", api_key=api_key, streaming=True, callbacks=[my_handler])

def generate(request):
    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", "You are a helpful assistant."),
            MessagesPlaceholder(variable_name="history"),
            ("human", "{question}"),
        ]
    )

    chain = prompt | model

    chain_with_history = RunnableWithMessageHistory(
        chain,
        lambda: SQLChatMessageHistory(
            session_id="streaming_test",
            connection_string=connection_string
        ),
        input_messages_key="question",
        history_messages_key="history",
    )
    
    chain_with_history.invoke({"question":request})
    
def start_generation(request):
    thread = Thread(target=generate, kwargs={"request":request})
    thread.start()
    
async def response_generator(request):
    start_generation(request)
    
    while True:
        value = streamer_queue.get()
        
        if value == None:
            break
        else:
            yield value
        streamer_queue.task_done()
        
        await asyncio.sleep(.1)
    

app = FastAPI(
    title="LangChain Server",
    version="1.0",
    description="A simple API server using LangChain's Runnable interfaces"
)

def get_chain():
    model = ChatOpenAI(model="gpt-4", api_key=api_key, streaming=True)

    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", "You are a helpful assistant."),
            MessagesPlaceholder(variable_name="history"),
            ("human", "{question}"),
        ]
    )

    chain = prompt | model

    chain_with_history = RunnableWithMessageHistory(
        chain,
        lambda: SQLChatMessageHistory(
            session_id="socket_test",
            connection_string=connection_string
        ),
        input_messages_key="question",
        history_messages_key="history",
    )
    return chain_with_history

@app.websocket("/chatbot")
async def chatbot_endpoint(websocket:WebSocket):
    await websocket.accept()
    
    while True:
        request = await websocket.receive()

        chain_with_history = get_chain()

        for token in chain_with_history.stream({"question":request.get('text')}):
            print(token.content)
            await websocket.send_text(token.content)
        
        websocket.close()
        
@app.get("/chatbot/stream")
async def chatbot_stream(request):
    print(f'Query received: {request}')
    return StreamingResponse(response_generator(request), media_type='text/event-stream')

if __name__ == "__main__":
    import uvicorn
    

    uvicorn.run(app, host="localhost", port=8000)