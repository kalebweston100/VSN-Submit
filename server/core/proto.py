import asyncio
from scapy.layers.inet import IP, TCP, UDP
from scapy.compat import raw

class ServerProto(asyncio.BufferedProtocol):

    DEFAULT_BUFFER = 4096

    def __init__(self, server_ip, interface_ip, supersock, filter):
        self.server_ip = server_ip
        self.interface_ip = interface_ip
        self.supersock = supersock
        self.filter = filter

    def connection_made(self, transport):
        self.transport = transport

    def get_buffer(self, sizehint):
        if sizehint == -1:
            self.buffer = bytearray(self.DEFAULT_BUFFER)
        else:
            self.buffer = bytearray(sizehint)
        return self.buffer

    def buffer_updated(self, nbytes):
        packet = IP(bytes(self.buffer[:nbytes]))
        if self.filter.outgoing(packet):
            response = self.supersock.sr1(self.prep_forward(packet))
        else:
            response = None
        if response:
            self.transport.write(raw(self.prep_forward(response)))
        else:
            self.transport.write(b'0')

    def connection_lost(self, exception):
        self.transport.close()

    def prep_forward(self, packet):
        packet.src = self.server_ip
        del packet.chksum
        if packet.haslayer(TCP):
            del packet[TCP].chksum
        elif packet.haslayer(UDP):
            del packet[UDP].chksum
        built_packet = IP(packet.build())
        return built_packet

    def prep_response(self, packet):
        packet.dst = self.interface_ip
        del packet.chksum
        if packet.haslayer(TCP):
            del packet[TCP].chksum
        elif packet.haslayer(UDP):
            del packet[UDP].chksum
        built_packet = IP(packet.build())
        return built_packet
