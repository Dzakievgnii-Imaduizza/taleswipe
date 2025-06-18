import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';

export default function BookmarkPage() {
    const [bookmarkedNovels, setBookmarkedNovels] = useState([]);
    const [loading, setLoading] = useState(true);
    const [unbookmarkingId, setUnbookmarkingId] = useState(null); // Untuk handle loading saat unbookmark
    const navigate = useNavigate();

    // Fetch bookmarks user dari backend
    const fetchBookmarks = async () => {
        setLoading(true);
        try {
            const token = localStorage.getItem('token');
            const res = await fetch('http://localhost:8080/api/users/me/bookmarks', {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) throw new Error('Gagal fetch bookmarks');
            const data = await res.json();
            setBookmarkedNovels(data.data); // <-- Gunakan data.data!
        } catch {
            setBookmarkedNovels([]);
        }
        setLoading(false);
    };

    // Hapus bookmark per story
    const handleUnbookmark = async (storyId) => {
        setUnbookmarkingId(storyId);
        try {
            const token = localStorage.getItem('token');
            const res = await fetch(`http://localhost:8080/api/stories/${storyId}/bookmark`, {
                method: 'DELETE', // BENAR
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) throw new Error('Gagal unbookmark');
            // Hapus dari state lokal agar langsung update tanpa fetch ulang
            setBookmarkedNovels((prev) => prev.filter(novel => novel.storyId !== storyId));
            // Atau bisa fetchBookmarks() jika ingin refetch
            // fetchBookmarks();
        } catch {
            alert('Gagal menghapus bookmark.');
        }
        setUnbookmarkingId(null);
    };

    useEffect(() => {
        fetchBookmarks();
        // Listen global event untuk update jika ada perubahan bookmark
        const handler = () => fetchBookmarks();
        window.addEventListener('bookmarkUpdated', handler);
        return () => window.removeEventListener('bookmarkUpdated', handler);
    }, []);

    const goToRead = (id) => {
        navigate(`/read/${id}`);
    };

    return (
        <div className="min-h-screen bg-white">
            <Navbar />
            <div className="p-6 max-w-5xl mx-auto">
                <h1 className="text-2xl font-bold mb-6 text-center">Bookmark</h1>
                <div className="bg-white p-6 rounded shadow space-y-6">
                    {loading ? (
                        <p className="text-center text-gray-500">Loading bookmark...</p>
                    ) : bookmarkedNovels.length === 0 ? (
                        <p className="text-center text-gray-500">Belum ada bookmark.</p>
                    ) : (
                        bookmarkedNovels.map((novel) => (
                            <div key={novel.storyId} className="flex gap-4 border-b pb-4">
                                <img
                                    src={novel.coverUrl}
                                    alt="cover"
                                    className="w-24 h-32 object-cover rounded shadow"
                                />
                                <div className="flex-1">
                                    <h2 className="font-semibold text-lg">{novel.title}</h2>
                                    <p className="text-sm text-gray-500">
                                        Author: {novel.author.name}
                                    </p>
                                    <p className="text-xs text-gray-400 italic">
                                        {Array.isArray(novel.genres) ? novel.genres.join(', ') : ''}
                                    </p>
                                    <p className="mt-2 text-sm text-gray-700 line-clamp-3">
                                        {novel.description}
                                    </p>
                                    <button
                                        onClick={() => goToRead(novel.storyId)}
                                        className="mt-2 text-blue-600 text-sm hover:underline"
                                    >
                                        Baca sekarang →
                                    </button>
                                    <button
                                        onClick={() => handleUnbookmark(novel.storyId)}
                                        className="mt-2 ml-4 text-red-600 text-sm hover:underline"
                                        disabled={unbookmarkingId === novel.storyId}
                                    >
                                        {unbookmarkingId === novel.storyId ? 'Menghapus...' : 'Hapus Bookmark'}
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}
