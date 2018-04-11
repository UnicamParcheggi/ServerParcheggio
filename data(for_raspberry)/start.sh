#!/bin/bash

echo Configurazione bluetooth tramite hciconfig.
sudo hciconfig hci0 up
sudo hciconfig hci0 piscan
sudo hciconfig hci0 noauth
sudo hciconfig hci0 noencrypt
echo Confirugazione finita, avvio il server...

sudo java -jar ServerParcheggio.jar
