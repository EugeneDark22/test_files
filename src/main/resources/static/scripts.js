async function fetchFiles() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html'; // Перенаправление на страницу входа, если токен отсутствует
        return;
    }

    const response = await fetch('/api/files', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        }
    });
    const files = await response.json();
    const tableBody = document.querySelector('#fileTable tbody');
    const admin = await isAdmin(token);
    const uploadForm = document.getElementById('uploadForm');

    if (admin) {
        uploadForm.style.display = 'block';
    }

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

async function isAdmin(token) {
    try {
        const response = await fetch('/api/user/role', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
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

    const token = localStorage.getItem('token');
    await fetch('/api/files/upload', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        body: formData
    });

    fetchFiles();
});

async function deleteFile(fileId) {
    if (confirm('Are you sure you want to delete this file?')) {
        const token = localStorage.getItem('token');
        await fetch(`/api/admin/files/${fileId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        fetchFiles();
    }
}

window.onload = fetchFiles;
