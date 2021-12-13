import socket
from scapy.layers.inet import IP, TCP, UDP
from scapy.compat import raw
from scapy.supersocket import SuperSocket

class RawSocket(SuperSocket):

    def __init__(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_RAW)
        self.outs = sock
        self.ins = sock

IP_ADDRESS = '10.0.0.164'
PORT = 8000

packet = IP(dst='8.8.8.8')/UDP(dport=8000)
sock = socket.socket(socket.AF_INET, socket.SOCK_RAW)
sock.bind(('127.0.0.1', 0))
sock.setsockopt(socket.IPPROTO_IP, socket.IP_HDRINCL, 1)
sock.send(raw(packet))
print(sock.recv())
if response:
    response.show()
