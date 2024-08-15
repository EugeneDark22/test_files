async function fetchFiles() {
    const response = await fetch('/api/files');
    const files = await response.json();
    const tableBody = document.querySelector('#fileTable tbody');
    const admin = await isAdmin();
    tableBody.innerHTML = '';
    files.forEach(file => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${file.name}</td>
            <td>${file.size}</td>
            <td>${file.extension}</td>
            <td>${file.hash}</td>
            <td>${new Date(file.creationDate).toLocaleString()}</td>
            <td>
                ${admin ? `<button onclick="deleteFile(${file.id})">Delete</button>` : ''}
            </td>
        `;
        tableBody.appendChild(row);
    });
}

async function isAdmin() {
    try {
        const response = await fetch('/api/user/role');
        const roles = await response.json();
        return roles.includes('ROLE_ADMIN');
    } catch (error) {
        console.error('Failed to check role', error);
        return false;
    }
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

async function deleteFile(fileId) {
    if (confirm('Are you sure you want to delete this file?')) {
        await fetch(`/api/admin/files/${fileId}`, {
            method: 'DELETE'
        });
        fetchFiles();
    }
}

window.onload = fetchFiles;
