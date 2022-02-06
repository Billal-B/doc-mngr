const selectFolderButton = document.getElementById("select-folder-button")
const selectFolderTable = document.getElementById("select-folder-table")

selectFolderButton.addEventListener("click", () => {
    window.api.send("select-folder")
})

window.api.receive("select-folder", (folders) => {
    selectFolderTable.innerHTML = folders.map(folder => `
<tr>
    <th>${folder.path}</th>
    <th>${folder.numOfFiles}</th>
</tr>`
    ).join('')
})