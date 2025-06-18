import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/useAuth';
import { FaBookmark, FaSearch } from 'react-icons/fa';
import { useState, useEffect } from 'react';

const defaultAvatar = '/default-avatar.png'; // Atau public path ke fallback

export default function Navbar() {
    const { isLoggedIn, logout } = useAuth();
    const navigate = useNavigate();
    const [showMenu, setShowMenu] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [user, setUser] = useState(null);

    useEffect(() => {
        if (!isLoggedIn) {
            setUser(null);
            return;
        }
        const token = localStorage.getItem('token');
        fetch('http://localhost:8080/api/users/me', {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        })
            .then(res => res.ok ? res.json() : null)
            .then(data => {
                // Data ada di data.data!
                setUser(data?.data || null);
            })
            .catch(() => setUser(null));
    }, [isLoggedIn]);

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const handleProfile = () => navigate('/profile');
    const handleDashboard = () => navigate('/dashboard');

    return (
        <header className="bg-[#0e0e0e] text-white px-8 py-4 flex items-center justify-between relative">
            {/* Left: Logo */}
            <div onClick={handleDashboard} className="flex items-center gap-3 shrink-0 cursor-pointer">
                <img src="/logo.png" alt="logo" className="w-7 h-7" />
                <span className="text-white font-semibold text-xl">TalesSwipe</span>
            </div>
            {/* Center: Search */}
            <div className="flex-1 flex justify-center px-6">
                <div className="relative w-full max-w-xl">
                    <FaSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={14} />
                    <input
                        type="text"
                        placeholder="Search..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter' && searchTerm.trim()) {
                                navigate(`/search?q=${encodeURIComponent(searchTerm.trim())}`);
                            }
                        }}
                        className="w-full pl-10 pr-4 py-2 bg-[#f1f1f3] text-sm text-gray-800 placeholder-gray-400 rounded-md shadow-sm focus:outline-none"
                    />
                </div>
            </div>
            {/* Right */}
            {isLoggedIn ? (
                <div className="flex items-center gap-6 shrink-0 relative">
                    {/* Bookmark */}
                    <FaBookmark
                        className="text-xl cursor-pointer hover:text-yellow-400 transition"
                        onClick={() => navigate('/bookmark')}
                    />
                    {/* Avatar + Dropdown */}
                    <div className="relative">
                        {/* Tampilkan avatar hanya jika user ready */}
                        {user && (
                            <img
                                src={
                                    user.profilePicture
                                        ? `http://localhost:8080${user.profilePicture}`
                                        : defaultAvatar
                                }
                                alt="user avatar"
                                className="w-9 h-9 rounded-full object-cover border-2 border-white cursor-pointer"
                                onClick={() => setShowMenu(!showMenu)}
                            />
                        )}
                        {showMenu && (
                            <div className="absolute right-0 mt-2 w-40 bg-white text-black rounded shadow-lg z-10">
                                <button onClick={handleProfile} className="w-full px-4 py-2 text-left hover:bg-gray-100">
                                    Profile
                                </button>
                                <button onClick={handleLogout} className="w-full px-4 py-2 text-left hover:bg-gray-100">
                                    Logout
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            ) : (
                <div className="flex gap-3">
                    <Link to="/signin" className="text-sm hover:underline">Login</Link>
                    <Link
                        to="/signup"
                        className="bg-indigo-500 text-white text-sm px-4 py-2 rounded hover:bg-indigo-600"
                    >
                        Sign up
                    </Link>
                </div>
            )}
        </header>
    );
}
