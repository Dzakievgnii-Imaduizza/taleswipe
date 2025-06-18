import { useState, useEffect } from 'react';
import { AuthContext } from './AuthContext';

export default function AuthProvider({ children }) {
    const [token, setToken] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        if (storedToken) {
            setToken(storedToken);
        }
        setIsLoading(false);
    }, []);

    const login = (newToken) => {
        localStorage.setItem('token', newToken);
        setToken(newToken);
    };

    const logout = () => {
        localStorage.removeItem('token');
        setToken(null);
    };

    const isLoggedIn = !!token;

    return (
        <AuthContext.Provider value={{ isLoggedIn, token, login, logout, isLoading }}>
            {children}
        </AuthContext.Provider>
    );
}
