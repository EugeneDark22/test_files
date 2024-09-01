document.addEventListener("DOMContentLoaded", function () {
    const uploadForm = document.getElementById("uploadForm");
    const searchForm = document.getElementById("searchForm");
    const fileTableBody = document.querySelector("#fileTable tbody");

    // Завантаження файлів при завантаженні сторінки
    fetchFiles();

    // Обробник завантаження файлу
    uploadForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const fileInput = document.getElementById("fileInput");
        const formData = new FormData();
        formData.append("file", fileInput.files[0]);

        const token = localStorage.getItem("token");
        await fetch("/api/files/upload", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token
            },
            body: formData
        });

        fetchFiles();
    });

    // Обробник пошуку файлів за вмістом
    searchForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const keyword = document.getElementById("searchKeyword").value;
        if (keyword) {
            fetch(`/api/files/search?keyword=${encodeURIComponent(keyword)}`, {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            })
                .then(response => response.json())
                .then(files => {
                    fileTableBody.innerHTML = "";
                    files.forEach(file => {
                        const row = document.createElement("tr");

                        row.innerHTML = `
                        <td>${file.name}</td>
                        <td>${file.size}</td>
                        <td>${file.extension}</td>
                        <td>${file.hash}</td>
                        <td>${new Date(file.creationDate).toLocaleString()}</td>
                        <td><button onclick="downloadFile('${file.path}')">Download</button></td>
                        ${isAdmin() ? `<td><button onclick="deleteFile(${file.id})">Delete</button></td>` : ''}
                    `;
                        fileTableBody.appendChild(row);
                    });
                })
                .catch(error => console.error("Error fetching files:", error));
        }
    });
});

async function fetchFiles() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html'; // Перенаправлення на сторінку входу, якщо токен відсутній
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
            <td><button onclick="downloadFile('${file.path}')">Download</button></td>
            ${admin ? `<td><button onclick="deleteFile(${file.id})">Delete</button></td>` : ''}
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

async function deleteFile(fileId) {
    if (confirm('Are you sure you want to delete this file?')) {
        const token = localStorage.getItem('token');
        await fetch(`/api/files/${fileId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        fetchFiles();
    }
}

function downloadFile(filePath) {
    window.location.href = `/api/files/download?path=${encodeURIComponent(filePath)}`;
}
