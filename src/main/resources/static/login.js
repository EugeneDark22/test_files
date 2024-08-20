document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch ('/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const token = await response.text();
            localStorage.setItem('token', token);
            window.location.href = '/file-info.html';
        } else {
            const errorText = await response.text();
            alert('Login failed: ' + errorText);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('An error occurred during login');
    }
});