import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '/src/contexts/useAuth.js';
import { useState } from 'react';

export default function SignIn() {
    const navigate = useNavigate();
    const { login } = useAuth();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://localhost:8080/api/users/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();
            console.log('Login response:', data);

            if (response.ok && data?.data) {
                console.log("Data yang masuk:", data.data);
                localStorage.setItem('token', data.data.token);
                localStorage.setItem('username', data.data.username); // <-- INI AMBIL DARI FIELD YANG BENAR
                localStorage.setItem('userId', data.data.userId);
                login(data.data.token);
                navigate('/dashboard');
            }




            else {
                const message = data?.message || 'Login gagal';
                setError(message);
            }
        } catch (err) {
            console.error('Login error:', err);
            setError('Terjadi kesalahan saat login');
        }
    };

    return (
        <div className="flex items-center justify-center h-screen bg-white">
            <div className="w-full max-w-md p-8 bg-white rounded shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">Sign in</h2>
                {error && <p className="text-red-600 text-sm mb-2 text-center">{error}</p>}
                <form className="space-y-4" onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Enter your username"
                        className="w-full p-2 border rounded"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Enter your password"
                        className="w-full p-2 border rounded"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <div className="flex justify-between items-center text-sm">
                        <label><input type="checkbox" className="mr-1" /> Remember me</label>
                        <a href="#" className="text-blue-600">Forgot password?</a>
                    </div>
                    <button className="w-full bg-indigo-500 text-white py-2 rounded" type="submit">Sign in</button>
                </form>
                <p className="mt-4 text-center text-sm">
                    Don't have an account? <Link to="/signup" className="text-blue-600">Sign up</Link>
                </p>
            </div>
        </div>
    );
}
