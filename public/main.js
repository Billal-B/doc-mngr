const {app, BrowserWindow, ipcMain, dialog, ipcRenderer} = require('electron')
const path = require("path");
const fs = require("fs")

let window

const createMainWindow = () => {
    window = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            preload: path.join(__dirname, "preload.js")
        }
    })
    window.webContents.toggleDevTools()
    window.loadFile(path.join(__dirname, "index.html"))
}

ipcMain.on("select-folder", (event, args) => {
    dialog.showOpenDialog({
        properties: ["multiSelections", "openDirectory"]
    }).then(res => {
        const out = res.filePaths.map(value => ({
            path: value,
            numOfFiles: fs.readdirSync(value).length // FIXME: check this readdirSync method
        }))
        window.webContents.send("select-folder", out)
    })
})

app.whenReady().then(() => {
    createMainWindow()
})
