const {
    contextBridge,
    ipcRenderer
} = require("electron");

const validChannels = ["select-folder"]

contextBridge.exposeInMainWorld(
    "api", {
        send: (channel, data) => {
            console.log("Send on channel " + channel)
            // whitelist channels
            if (validChannels.includes(channel)) {
                ipcRenderer.send(channel, data);
            }
        },
        receive: (channel, func) => {
            console.log("Receive on channel " + channel)
            if (validChannels.includes(channel)) {
                // Deliberately strip event as it includes `sender`
                ipcRenderer.on(channel, (event, ...args) => func(...args));
            }
        }
    }
);
