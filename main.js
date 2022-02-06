const {app, BrowserWindow, ipcMain, dialog} = require('electron')
const path = require("path");
const fs = require("fs")

let win

const createMainWindow = () => {
    win = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            preload: path.join(__dirname, "preload.js")
        }
    })
    win.webContents.toggleDevTools()
    win.loadFile("index.html")
}

ipcMain.on("select-folder", (event, args) => {
    const selectedFolders = dialog.showOpenDialog({
        properties: ["multiSelections", "openDirectory"]
    }).then(res => {
        const out = res.filePaths.map(value => ({
            path: value,
            numOfFiles: fs.readdirSync(value).length // FIXME: check this readdirSync method
        }))
        win.webContents.send("select-folder", out)
    })
})

app.whenReady().then(() => {
    createMainWindow()
})
