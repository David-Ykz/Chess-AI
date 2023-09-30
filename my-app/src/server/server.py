import socket

# Define host and port
host = '127.0.0.1'  # Use the appropriate IP address or '0.0.0.0' to listen on all available network interfaces
port = 8080  # Use the desired port number

# Create a socket object
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the host and port
server_socket.bind((host, port))

# Listen for incoming connections
server_socket.listen(1)  # Maximum number of queued connections

print(f"Server listening on {host}:{port}")

while True:
    # Accept a connection from a client
    client_socket, client_address = server_socket.accept()
    print(f"Accepted connection from {client_address}")

    # Handle the client's request
    data = client_socket.recv(1024).decode('utf-8')  # Receive data from the client
    print(f"Received data from client: {data}")

    # Send a response back to the client
    response = "Hello from Python server!"
    client_socket.send(response.encode('utf-8'))

    # Close the connection
    client_socket.close()