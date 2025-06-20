import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';

const GENRE_OPTIONS = ["Romance", "Fantasy", "Action", "Drama", "Horror", "Historical", "Thriller", "Sciene Fition", "Slice of Life"];

export default function EditNovel() {
    const { id } = useParams();
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    // State
    const [original, setOriginal] = useState({
        title: '',
        description: '',
        genres: [],
        pages: [''],
    });

    const [novel, setNovel] = useState({
        id: '',
        title: '',
        description: '',
        coverUrl: '',
        genres: [],
        pages: [''],
    });
    const [newCover, setNewCover] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!id) return;
        setLoading(true);
        fetch(`http://localhost:8080/api/stories/${id}`, {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then(res => {
                if (!res.ok) throw new Error('Gagal fetch');
                return res.json();
            })
            .then(res => {
                const data = res.data || res || {};
                setNovel({
                    id: data.storyId || data.id || '',
                    title: data.title || '',
                    description: data.description || '',
                    coverUrl: data.coverUrl || '',
                    genres: Array.isArray(data.genres) ? data.genres : [],
                    pages: data.pages?.map(p => p.content) || [''],
                });
                setOriginal({
                    title: data.title || '',
                    description: data.description || '',
                    genres: Array.isArray(data.genres) ? data.genres : [],
                    pages: data.pages?.map(p => p.content) || [''],
                });
            })
            .catch(e => {
                setError('Gagal mengambil data novel');
                console.error(e);
            })
            .finally(() => setLoading(false));
    }, [id, token]);

    const handleCoverUpload = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setNewCover(file);
        const reader = new FileReader();
        reader.onloadend = () => setNovel(prev => ({ ...prev, coverUrl: reader.result }));
        reader.readAsDataURL(file);
    };

    const handleAddPage = () => setNovel(prev => ({ ...prev, pages: [...prev.pages, ''] }));
    const handleDeletePage = () => {
        if (novel.pages.length > 1)
            setNovel(prev => ({ ...prev, pages: prev.pages.slice(0, -1) }));
    };
    const handleChangePage = (index, value) => {
        const updated = [...novel.pages];
        updated[index] = value.slice(0, 1000);
        setNovel(prev => ({ ...prev, pages: updated }));
    };

    // Checkbox genre
    const handleGenreCheckbox = (genre) => {
        setNovel(prev => {
            const genres = prev.genres.includes(genre)
                ? prev.genres.filter(g => g !== genre)
                : [...prev.genres, genre];
            return { ...prev, genres };
        });
    };

    const handleSave = async () => {
        setError('');
        if (!novel.title.trim()) return setError('Judul tidak boleh kosong.');
        if (!novel.description.trim()) return setError('Sinopsis tidak boleh kosong.');
        if (!novel.genres.length) return setError('Pilih minimal satu genre.');
        if (novel.pages.every(p => p.trim() === '')) return setError('Minimal satu halaman harus memiliki isi.');

        setLoading(true);

        try {
            let body;
            let headers;
            if (newCover) {
                body = new FormData();
                body.append('title', novel.title);
                body.append('description', novel.description);
                novel.genres.forEach(g => body.append('genres', g));;
                novel.pages.forEach(p => body.append('pageContents', p));
                body.append('cover', newCover);
                headers = { Authorization: `Bearer ${token}` };
            } else {
                body = JSON.stringify({
                    title: novel.title,
                    description: novel.description,
                    genres: novel.genres,
                    pageContents: novel.pages,
                });
                headers = {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                };
            }

            const res = await fetch(`http://localhost:8080/api/stories/${id}`, {
                method: 'PUT',
                headers,
                body,
            });

            if (res.ok) {
                navigate('/profile');
            } else {
                const err = await res.json();
                setError(err?.message || 'Gagal menyimpan perubahan.');
            }
        } catch {
            setError('Gagal menyimpan perubahan (network)');
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <header>
                <Navbar />
            </header>
            <div className="max-w-4xl mx-auto p-8 space-y-6 bg-white rounded shadow" key={novel.id || 'default'}>
                <h1 className="text-2xl font-bold">Edit Novel</h1>
                {loading && <div className="text-gray-500">Memuat data...</div>}
                {/* COVER */}
                <div>
                    <label className="block text-sm font-medium mb-2">Cover</label>
                    <input type="file" accept="image/*" onChange={handleCoverUpload} />
                    {novel.coverUrl && (
                        <img src={`http://localhost:8080${novel.coverUrl}`} alt="preview" className="w-40 h-56 mt-4 object-cover rounded shadow" />
                    )}
                </div>
                {/* JUDUL */}
                <div>
                    <label className="block text-sm font-medium mb-1">Judul Novel</label>
                    <input
                        type="text"
                        value={novel.title}
                        onChange={e => setNovel(prev => ({ ...prev, title: e.target.value }))}
                        className="w-full border rounded p-2"
                        placeholder={original.title || 'Judul novel...'}
                    />
                </div>
                {/* SINOPSIS */}
                <div>
                    <label className="block text-sm font-medium mb-1">Sinopsis</label>
                    <textarea
                        value={novel.description}
                        onChange={e => setNovel(prev => ({ ...prev, description: e.target.value }))}
                        className="w-full border rounded p-2"
                        rows={3}
                        placeholder={original.description || 'Sinopsis novel...'}
                    />
                </div>
                {/* GENRE */}
                <div>
                    <label className="block text-sm font-medium mb-1">Genre (bisa pilih lebih dari satu)</label>
                    <div className="flex gap-4 flex-wrap">
                        {GENRE_OPTIONS.map(genre => (
                            <label key={genre} className="inline-flex items-center gap-2">
                                <input
                                    type="checkbox"
                                    checked={novel.genres.includes(genre)}
                                    onChange={() => handleGenreCheckbox(genre)}
                                    className="accent-indigo-600"
                                />
                                {genre}
                            </label>
                        ))}
                    </div>
                    {!novel.genres.length && <div className="text-xs text-gray-500 mt-1">* Pilih minimal satu genre.</div>}
                </div>
                {/* CERITA */}
                <div>
                    <label className="block text-sm font-medium mb-2">Cerita (max 1000 karakter per halaman)</label>
                    {novel.pages.map((content, i) => (
                        <textarea
                            key={i}
                            value={content}
                            onChange={e => handleChangePage(i, e.target.value)}
                            className="w-full border rounded p-2 mb-3"
                            rows={4}
                            placeholder={original.pages[i] || `Halaman ${i + 1}`}
                            maxLength={1000}
                        />
                    ))}
                </div>
                {/* BUTTON TAMBAH/HAPUS HALAMAN */}
                <div className="flex gap-4">
                    <button type="button" onClick={handleAddPage} className="bg-green-500 text-white px-4 py-2 rounded">+ Halaman</button>
                    <button type="button" onClick={handleDeletePage} className="bg-red-500 text-white px-4 py-2 rounded">- Halaman</button>
                </div>
                {/* ERROR */}
                {error && <div className="text-red-600 text-sm mt-2">{error}</div>}
                {/* SAVE */}
                <button
                    onClick={handleSave}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-6 rounded mt-4"
                    disabled={loading}
                >
                    Simpan Perubahan
                </button>
            </div>
        </>
    );
}
