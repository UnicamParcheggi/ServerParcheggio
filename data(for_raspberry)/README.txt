(GUIDA PER RASPBERRY PI 3 B)

- Installare sul raspberry i seguenti pacchetti (alcuni potrebbero non essere necessari):
bluetooth
bluez
bluez-utils
bluez-firmware
blueman
libbluetooth3
libbluetooth-dev

- Abilitare il bluetooth e impostarlo per ricevere connessioni da client:
(dove hci0 è l'interfaccia corrente del bluetooth, per scoprire il nome della propria basta digitare: "hciconfig dev")

sudo hciconfig hci0 up
sudo hciconfig hci0 piscan
sudo hciconfig hci0 noauth
sudo hciconfig hci0 noencrypt

(se si hanno dei problemi nell' abilitare l'interfaccia bluetooth potrebbe essere utile controllare se essa sia bloccata digitando rfkill list, in casi si abbia un output diverso da:
Soft blocked: no
Hard blocked: no
bisogna sbloccare l'interfaccia digitando:
 rfkill unblock bluetooth)

- Modificare il file:
 /etc/systemd/system/bluetooth.target.wants/bluetooth.service

aggiungendo (senza apici) "-C --noplugin=sap" alla fine di:
 ExecStart=/usr/lib/bluetooth/bluetoothd

ed aggiungendo la seguente riga (subito sotto ExecStart=...):
 ExecStartPost=/usr/bin/sdptool add SP

(un esempio della modifica da fare è nella cartella ./localsetting.bak")

- Creare rfcomm.service (il contenuto del file è nella cartella ./localsetting.bak),
per mettere il BT del raspberry in ascolto e metterlo in:
 /etc/systemd/system/

In seguito bisogna abilitare l'avvio automatico del servizio appena creato:
 sudo systemctl enable rfcomm 

Avviamo subito il servizio
 sudo systemctl enable rfcomm

(per controllarne lo status basta digitare: sudo systemctl status rfcomm)

- Il server dovrà essere compilato con le librerie bluecove compilate per arm. (si trovano nella cartella ./localsetting.bak)

