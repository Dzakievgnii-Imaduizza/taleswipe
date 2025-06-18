import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Author({ username, name, profilePicture, onFollowChange }) {
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const [followed, setFollowed] = useState(false);
    const [followersCount, setFollowersCount] = useState(0);

    useEffect(() => {
        if (!username || !token) return;
        fetch(`http://localhost:8080/api/users/by-username/${username}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(json => {
                const author = json.data;
                setFollowersCount(author.followerCount || 0);
                setFollowed(author.followedByCurrentUser || false);
            });
    }, [username, token]);

    const handleFollowClick = async () => {
        if (!username || !token) return;
        const url = `http://localhost:8080/api/users/${username}/follow`;
        try {
            if (followed) {
                await fetch(url, {
                    method: "DELETE",
                    headers: { Authorization: `Bearer ${token}` }
                });
                setFollowersCount(prev => Math.max(prev - 1, 0));
                setFollowed(false);
            } else {
                await fetch(url, {
                    method: "POST",
                    headers: { Authorization: `Bearer ${token}` }
                });
                setFollowersCount(prev => prev + 1);
                setFollowed(true);
            }
            if (onFollowChange) onFollowChange();
        } catch {
            alert('Gagal update follow status!');
        }
    };


    if (!name && !profilePicture) return null;

    return (
        <div className="relative bg-white p-4 rounded shadow mb-4">
            <h3 className="text-sm font-semibold mb-3">Author</h3>
            <div
                className="flex items-center gap-3 mb-3 cursor-pointer"
                onClick={() => navigate(`/author/${encodeURIComponent(username || name)}`)}
                title="Lihat profil author"
            >
                <img
                    src={`http://localhost:8080${profilePicture}`}
                    alt={name}
                    className="w-10 h-10 rounded-full object-cover"
                />
                <div>
                    <div className="text-sm font-bold">{name}</div>
                    <div className="text-xs text-gray-500">{followersCount} Followers</div>
                </div>
            </div>
            <button
                onClick={handleFollowClick}
                className={`px-3 py-1 rounded text-sm transition ${followed ? 'bg-red-500 text-white' : 'bg-indigo-500 text-white'}`}
            >
                {followed ? 'Unfollow' : 'Follow'}
            </button>
        </div>
    );
}

const ReportIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" className="w-5 h-5">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.29 3.86L1.82 18a1 1 0 00.86 1.5h18.64a1 1 0 00.86-1.5L13.71 3.86a1 1 0 00-1.72 0zM12 9v4m0 4h.01" />
    </svg>
);
