# Proxmox-App-5ISS

This Proxmox API include the following files:

## Generator

To generate containers according to the available resources on Proxmox.

## API

Contains all the constants needed for managing our 2 servers, all the methods to login, obtain information about servers and manage containers.

## Manager

To monitor the containers:
- delete the useless containers.
- list all the containers.

To analyze the containers:
- obtain the name, CPU, disk and RAM
- migrate containers according to the RAM they used on the server.
- delete the oldest container according to the load on the server.
