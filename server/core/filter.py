from scapy.layers.inet import IP, TCP, UDP

class Filter:

    HTTP = 80
    HTTPS = 443
    ALLOWED_TCP = [HTTP, HTTPS]

    DNS = 53
    TLS_DNS = 853
    ALLOWED_UDP = [DNS, TLS_DNS]

    def outgoing(self, packet):
        valid = False
        if packet.haslayer(TCP):
            if packet.dport in self.ALLOWED_TCP:
                valid = True
        elif packet.haslayer(UDP):
            if packet.dport in self.ALLOWED_UDP:
                valid = True
        return valid
