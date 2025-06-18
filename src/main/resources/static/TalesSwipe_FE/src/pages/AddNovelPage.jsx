import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaTrash } from 'react-icons/fa';
import Navbar from '../components/Navbar';

// Helper ambil token dari localStorage
const getToken = () => localStorage.getItem("token");

export default function AddNovel() {
    const navigate = useNavigate();

    // State untuk cover image (file asli & preview URL)
    const [coverFile, setCoverFile] = useState(null);
    const [coverPreview, setCoverPreview] = useState(null);

    // State data novel
    const [title, setTitle] = useState('');
    const [synopsis, setSynopsis] = useState('');
    const [pages, setPages] = useState(['']);
    const [currentPage, setCurrentPage] = useState(0);

    // Genre
    const availableGenres = [
        'Fantasy',
        'Romance',
        'Slice of Life',
        'Drama',
        'Science Fiction',
        'Mystery',
        'Thriller',
        'Historical',
    ];
    const [selectedGenres, setSelectedGenres] = useState([]);

    // Handle ganti cover
    const handleCoverChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setCoverFile(file);
            setCoverPreview(URL.createObjectURL(file));
        }
    };

    // Handle page content
    const handlePageChange = (e) => {
        const updatedPages = [...pages];
        updatedPages[currentPage] = e.target.value.slice(0, 1000);
        setPages(updatedPages);
    };

    // Tambah/Hapus halaman
    const addPage = () => {
        setPages([...pages, '']);
        setCurrentPage(pages.length);
    };

    const removePage = () => {
        if (pages.length > 1) {
            const updatedPages = [...pages];
            updatedPages.splice(currentPage, 1);
            setPages(updatedPages);
            setCurrentPage((prev) => Math.max(prev - 1, 0));
        }
    };

    // SUBMIT ke backend API
    const handleSubmit = async () => {
        try {
            const token = getToken();
            // 1. Kirim data novel tanpa cover
            const storyResp = await createStoryApi({
                title,
                synopsis,
                genres: selectedGenres,
                pageContents: pages,
                token
            });

            // 2. Kalau cover diupload, upload cover pakai storyId dari backend
            if (coverFile && storyResp && storyResp.storyId) {
                await uploadCoverApi(storyResp.storyId, coverFile, token);
            }

            alert('Novel berhasil ditambahkan!');
            navigate('/profile');
        } catch (err) {
            alert(err.message || "Gagal menambah novel");
        }
    };

    return (
        <>
            <header>
                <Navbar />
            </header>
            <div className="min-h-screen bg-white px-10 py-8">
                <h1 className="text-2xl font-semibold text-center mb-6">Tambah Novel</h1>

                <div className="grid grid-cols-3 gap-10">
                    {/* LEFT: Cover */}
                    <div className="flex flex-col items-center gap-4">
                        <label className="text-sm font-medium">Cover</label>
                        <div className="w-48 h-72 border rounded flex items-center justify-center bg-gray-50 overflow-hidden relative shadow">
                            {coverPreview ? (
                                <img src={coverPreview} alt="cover" className="w-full h-full object-cover" />
                            ) : (
                                <label className="text-3xl cursor-pointer">+
                                    <input
                                        type="file"
                                        accept="image/*"
                                        className="hidden"
                                        onChange={handleCoverChange}
                                    />
                                </label>
                            )}
                        </div>
                        {coverPreview && (
                            <button onClick={() => { setCoverFile(null); setCoverPreview(null); }} className="text-gray-500 hover:text-red-600 mt-1">
                                <FaTrash />
                            </button>
                        )}
                    </div>

                    {/* CENTER: Info */}
                    <div className="flex flex-col justify-start gap-6">
                        <div>
                            <label className="block text-sm font-medium mb-1">Judul</label>
                            <input
                                type="text"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                className="w-full border px-4 py-2 rounded shadow-sm"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Sinopsis</label>
                            <textarea
                                value={synopsis}
                                onChange={(e) => setSynopsis(e.target.value)}
                                className="w-full h-24 border px-4 py-2 rounded shadow-sm resize-none"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Genre</label>
                            <div className="grid grid-cols-2 gap-2">
                                {availableGenres.map((genre) => (
                                    <label key={genre} className="flex items-center gap-2 text-sm">
                                        <input
                                            type="checkbox"
                                            value={genre}
                                            checked={selectedGenres.includes(genre)}
                                            onChange={(e) => {
                                                const value = e.target.value;
                                                setSelectedGenres((prev) =>
                                                    prev.includes(value)
                                                        ? prev.filter((g) => g !== value)
                                                        : [...prev, value]
                                                );
                                            }}
                                        />
                                        {genre}
                                    </label>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* RIGHT: Halaman cerita */}
                    <div className="flex flex-col items-center gap-4">
                        <label className="text-sm font-medium">Cerita</label>
                        <div className="w-72 h-[460px] bg-[url('/paper-texture.png')] bg-cover p-6 text-justify text-sm rounded shadow overflow-y-auto">
                            <textarea
                                value={pages[currentPage]}
                                onChange={handlePageChange}
                                maxLength={1000}
                                className="w-full h-full bg-transparent focus:outline-none resize-none"
                            />
                        </div>

                        <div className="flex items-center gap-4 text-sm mt-1">
                            <button
                                onClick={() => setCurrentPage((p) => Math.max(p - 1, 0))}
                                className="text-xl"
                                disabled={currentPage === 0}
                            >
                                ←
                            </button>
                            <span>{currentPage + 1} / {pages.length}</span>
                            <button
                                onClick={() => setCurrentPage((p) => Math.min(p + 1, pages.length - 1))}
                                className="text-xl"
                                disabled={currentPage === pages.length - 1}
                            >
                                →
                            </button>
                        </div>

                        <div className="flex flex-col gap-2 mt-2">
                            <button onClick={addPage} className="bg-indigo-500 text-white px-4 py-2 text-sm rounded">
                                Tambah halaman
                            </button>
                            <button onClick={removePage} className="bg-indigo-500 text-white px-4 py-2 text-sm rounded">
                                Hapus Halaman
                            </button>
                        </div>
                    </div>
                </div>

                {/* FOOTER ACTIONS */}
                <div className="flex justify-between mt-10 px-20">
                    <button onClick={() => navigate(-1)} className="bg-indigo-500 text-white px-6 py-2 rounded shadow">
                        Batal
                    </button>
                    <button onClick={handleSubmit} className="bg-indigo-500 text-white px-6 py-2 rounded shadow">
                        Selesai
                    </button>
                </div>
            </div>
        </>
    );
}

// --- Helper function API call ---

async function createStoryApi({ title, synopsis, genres, pageContents, token }) {
    const payload = {
        title,
        description: synopsis,
        genres,
        pageContents, // <- field harus pageContents!
    };
    const response = await fetch("http://localhost:8080/api/stories", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(payload),
    });
    if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || "Gagal menambah novel");
    }
    return await response.json(); // StoryResponse (pastikan ada .storyId)
}

async function uploadCoverApi(storyId, file, token) {
    const formData = new FormData();
    formData.append("file", file);
    const response = await fetch(`http://localhost:8080/api/stories/${storyId}/cover`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: formData,
    });
    if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || "Gagal upload cover");
    }
    return await response.json(); // { data: '/uploads/xxx.jpg', ... }
}
