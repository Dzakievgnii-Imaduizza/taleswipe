import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';

export default function SignUp() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        password: ''
    });
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const fullName = `${formData.firstName} ${formData.lastName}`.trim();

        try {
            const response = await fetch('http://localhost:8080/api/users/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: formData.username,
                    name: fullName,
                    email: formData.email,
                    password: formData.password
                })
            });

            const data = await response.json();

            if (response.ok) {
                localStorage.setItem('token', data.data.token); // ✅ simpan token
                navigate('/genre-preference', {
                    state: {
                        username: formData.username,
                        token: data.data.token
                    }
                });
            }
            else {
                const msg = data?.error?.message || data?.message || 'Registrasi gagal';
                setError(msg);
            }
        } catch (err) {
            console.error('Registration error:', err);
            setError('Terjadi kesalahan saat registrasi');
        }
    };

    return (
        <div className="flex items-center justify-center h-screen bg-white">
            <div className="w-full max-w-md p-8 bg-white rounded shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">Sign up</h2>
                {error && <p className="text-red-600 text-sm mb-2 text-center">{error}</p>}
                <form className="space-y-4" onSubmit={handleSubmit}>
                    <input name="username" type="text" placeholder="Username" className="w-full p-2 border rounded" required onChange={handleChange} />
                    <div className="flex space-x-2">
                        <input name="firstName" type="text" placeholder="First name" className="w-1/2 p-2 border rounded" required onChange={handleChange} />
                        <input name="lastName" type="text" placeholder="Last name" className="w-1/2 p-2 border rounded" required onChange={handleChange} />
                    </div>
                    <input name="email" type="email" placeholder="example.email@gmail.com" className="w-full p-2 border rounded" required onChange={handleChange} />
                    <input name="password" type="password" placeholder="Password" className="w-full p-2 border rounded" required onChange={handleChange} />
                    <div className="flex items-center">
                        <input type="checkbox" className="mr-2" required />
                        <span className="text-sm">By signing up, I agree with the <a className="text-blue-600" href="#">Terms of Use</a> & <a className="text-blue-600" href="#">Privacy Policy</a></span>
                    </div>
                    <button className="w-full bg-indigo-500 text-white py-2 rounded" type="submit">Sign up</button>
                </form>
                <p className="mt-4 text-center text-sm">
                    Already have an account? <Link to="/signin" className="text-blue-600">Log in</Link>
                </p>
            </div>
        </div>
    );
}
