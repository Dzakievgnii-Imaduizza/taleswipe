import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { FaBookmark, FaRegBookmark } from 'react-icons/fa';
import Navbar from '../components/Navbar';

export default function SearchPage() {
    const [searchParams] = useSearchParams();
    const [results, setResults] = useState([]);
    const [genre, setGenre] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const query = searchParams.get('q')?.toLowerCase() || '';
    const token = localStorage.getItem('token');

    // Fetch stories dari API search endpoint
    useEffect(() => {
        const fetchStories = async () => {
            setLoading(true);
            try {
                const res = await fetch(
                    `http://localhost:8080/api/stories/search?query=${encodeURIComponent(query)}`,
                    { headers: { 'Authorization': `Bearer ${token}` } }
                );
                if (res.ok) {
                    const data = await res.json();
                    // Filter by genre kalau dipilih
                    const filtered = genre
                        ? data.filter(story => (story.genres || []).map(g => g.toLowerCase()).includes(genre.toLowerCase()))
                        : data;
                    setResults(filtered);
                } else {
                    setResults([]);
                }
            } catch {
                setResults([]);
            }
            setLoading(false);
        };
        if (query) fetchStories();
    }, [query, genre, token]);

    // Handler untuk toggle bookmark story
    const toggleBookmark = async (story) => {
        const isBookmarked = story.bookmarkedByCurrentUser;
        const method = isBookmarked ? 'DELETE' : 'POST';
        setLoading(true);
        try {
            await fetch(
                `http://localhost:8080/api/stories/${story.storyId}/bookmark`,
                {
                    method,
                    headers: { 'Authorization': `Bearer ${token}` }
                }
            );
            // Setelah toggle, refresh list (ambil data terbaru dari API)
            const res = await fetch(
                `http://localhost:8080/api/stories/search?query=${encodeURIComponent(query)}`,
                { headers: { 'Authorization': `Bearer ${token}` } }
            );
            if (res.ok) {
                const data = await res.json();
                const filtered = genre
                    ? data.filter(story => (story.genres || []).map(g => g.toLowerCase()).includes(genre.toLowerCase()))
                    : data;
                setResults(filtered);
            }
        } catch {
            // Error handling opsional
        }
        setLoading(false);
    };

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-white px-8 py-6">
                <h1 className="text-xl font-semibold mb-4">
                    Hasil Penelusuran : <span className="font-bold">{query}</span>
                </h1>

                <div className="flex items-center gap-4 mb-4">
                    <label className="font-medium">Genre</label>
                    <select
                        value={genre}
                        onChange={(e) => setGenre(e.target.value)}
                        className="px-3 py-1 border rounded bg-gray-100 text-sm"
                    >
                        <option value="">Semua</option>
                        <option value="romance">Romance</option>
                        <option value="action">Action</option>
                        <option value="Fantasy">Fantasy</option>
                        <option value="slice of life">Slice of Life</option>
                        <option value="Thriller">Thriller</option>
                        <option value="Mystery">Mystery</option>
                        <option value="Science">Science</option>
                        <option value="Business">Sci-Fi</option>
                        {/* Tambahkan genre lain sesuai kebutuhan */}
                    </select>
                </div>

                <div className="bg-white p-6 rounded shadow space-y-6">
                    {loading ? (
                        <p className="text-gray-500">Loading...</p>
                    ) : results.length === 0 ? (
                        <p className="text-gray-500">Tidak ditemukan novel yang cocok.</p>
                    ) : (
                        results.map((novel) => (
                            <div key={novel.storyId} className="flex gap-4 border-b pb-4 relative">
                                <img src={novel.coverUrl} alt={novel.title} className="w-24 h-32 object-cover rounded shadow" />
                                <div className="flex-1">
                                    <h2 className="font-semibold text-lg">{novel.title}</h2>
                                    <p className="text-sm text-gray-500">Author: {novel.author?.name}</p>
                                    <p className="text-xs text-gray-400 italic">
                                        {(novel.genres || []).join(', ')}
                                    </p>
                                    <p className="mt-2 text-sm text-gray-700 line-clamp-3">{novel.description}</p>
                                    <button
                                        onClick={() => navigate(`/read/${novel.storyId}`)}
                                        className="mt-2 text-blue-600 text-sm hover:underline"
                                    >
                                        Baca sekarang →
                                    </button>
                                </div>
                                <div className="absolute top-2 right-2">
                                    <button
                                        onClick={() => toggleBookmark(novel)}
                                        className="text-yellow-500 hover:scale-110 transition"
                                        disabled={loading}
                                    >
                                        {novel.bookmarkedByCurrentUser ? <FaBookmark /> : <FaRegBookmark />}
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </>
    );
}
