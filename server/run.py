import asyncio
from core.proto import ServerProto
from core.filter import Filter
from scapy.supersocket import L3RawSocket

SERVER_IP = '10.0.0.164'
INTERFACE_IP = '192.168.2.2'
PORT = 8000

async def main(server_ip, port, interface_ip):
    supersock = L3RawSocket()
    filter = Filter()
    loop = asyncio.get_running_loop()
    server = await loop.create_server(lambda: ServerProto(server_ip, interface_ip, supersock, filter), server_ip, port)
    async with server:
        await server.serve_forever()

if __name__ == '__main__':
    asyncio.run(main(SERVER_IP, PORT, INTERFACE_IP))
