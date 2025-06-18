import { useParams, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import StoryPage from '../components/StoryPage';
import CommentSection from '../components/CommentSection';
import { FaBookmark, FaRegBookmark } from 'react-icons/fa';

export default function ReadPage() {
    const { id } = useParams(); // <-- id = storyId
    const navigate = useNavigate();
    const [novel, setNovel] = useState(null);
    const [loading, setLoading] = useState(false);
    const [bookmarking, setBookmarking] = useState(false);
    const token = localStorage.getItem('token');

    // Fetch novel dari backend
    const fetchNovel = async () => {
        setLoading(true);
        try {
            const res = await fetch(`http://localhost:8080/api/stories/${id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) throw new Error('Not found');
            const data = await res.json();
            setNovel(data);
        } catch {
            setNovel(null);
        }
        setLoading(false);
    };

    useEffect(() => {
        fetchNovel();
        // eslint-disable-next-line
    }, [id]);

    // Toggle bookmark dari API
    const toggleBookmark = async () => {
        if (!novel) return;
        setBookmarking(true);
        try {
            const method = novel.bookmarkedByCurrentUser ? 'DELETE' : 'POST';
            await fetch(
                `http://localhost:8080/api/stories/${id}/bookmark`,
                {
                    method,
                    headers: { 'Authorization': `Bearer ${token}` }
                }
            );
            // Ambil data terbaru setelah update
            await fetchNovel();
        } catch {
            // Optional: tampilkan error
        }
        setBookmarking(false);
    };


    if (loading) {
        return (
            <div className="text-center mt-20 text-lg text-gray-600">
                Loading...
            </div>
        );
    }

    if (!novel) {
        return (
            <div className="text-center mt-20 text-lg text-red-500">
                Novel not found.
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-100 text-gray-900">
            <Navbar />

            <div className="p-4">
                <button
                    onClick={() => navigate(-1)}
                    className="px-4 py-2 bg-gray-200 text-gray-800 rounded shadow hover:bg-gray-300 transition"
                >
                    ← Kembali
                </button>
            </div>

            <main className="grid grid-cols-1 lg:grid-cols-3 gap-4 p-1">
                {/* Sidebar Book Info */}
                <section className="col-span-1">
                    <div className="bg-white p-4 rounded shadow h-[680px] flex flex-col items-center">
                        <div className="flex justify-center w-full mb-4">
                            <img src={`http://localhost:8080${novel.coverUrl}`} alt="cover" className="w-64 h-[400px] object-cover rounded" />
                        </div>
                        <h2 className="text-lg font-semibold mb-2 text-center">{novel.title}</h2>
                        <p className="text-sm text-gray-600 mb-1 text-center">Author: {novel.author?.name}</p>
                        <p className="text-sm text-gray-600 mb-1 text-center">
                            Genre: {(novel.genres || []).join(', ')}
                        </p>
                        <p className="text-sm text-gray-600 mb-3 text-center">
                            Likes: {novel.reactions?.likeCount ?? 0} | Bookmarks: {novel.reactions?.bookmarkCount ?? 0}
                        </p>
                        <p className="text-justify text-sm">{novel.description}</p>

                        <div className="mt-auto w-full">
                            <button
                                onClick={toggleBookmark}
                                disabled={bookmarking}
                                className={`w-full py-2 px-4 rounded font-semibold text-sm shadow flex items-center justify-center gap-2 transition 
                                    ${novel.bookmarkedByCurrentUser ? 'bg-red-500 text-white' : 'bg-blue-500 text-white'}`}
                            >
                                {novel.bookmarkedByCurrentUser ? <FaBookmark /> : <FaRegBookmark />}
                                {novel.bookmarkedByCurrentUser ? 'Hapus Bookmark' : 'Tambahkan Bookmark'}
                            </button>
                        </div>
                    </div>
                </section>

                {/* Story Section */}
                <section className="col-span-1 flex justify-center">
                    <StoryPage pages={novel.pages} />
                </section>

                {/* Comment Section */}
                <section className="col-span-1 mt-20">
                    <CommentSection
                        novelId={novel.storyId}
                        initialComments={novel.comments}
                    />
                </section>
            </main>
        </div>
    );
}
