async function fetchFiles() {
    const response = await fetch('/api/files');
    const files = await response.json();
    const tableBody = document.querySelector('#fileTable tbody');
    tableBody.innerHTML = '';
    files.forEach(file => {
        const row = document.createElement('tr');
        row.innerHTML = `
                <td>${file.name}</td>
                <td>${file.size}</td>
                <td>${file.extension}</td>
                <td>${file.hash}</td>
                <td>${new Date(file.creationDate).toLocaleString()}</td>
            `;
        tableBody.appendChild(row);
    });
}

document.getElementById('uploadForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fileInput = document.getElementById('fileInput');
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    await fetch('/api/files/upload', {
        method: 'POST',
        body: formData
    });

    fetchFiles();
});

window.onload = fetchFiles;
