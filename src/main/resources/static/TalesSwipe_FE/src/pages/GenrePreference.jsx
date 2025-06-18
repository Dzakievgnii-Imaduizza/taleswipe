import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useState, useCallback, useRef } from 'react';

export default function GenrePreference() {
    const { state: userData } = useLocation();
    const navigate = useNavigate();
    const [selectedGenres, setSelectedGenres] = useState([]);

    const deleteOnceRef = useRef(false); // ✅ Cegah multiple delete

    const deleteUserAndRedirect = useCallback(async () => {
        if (deleteOnceRef.current) return; // ✅ Jangan hapus dua kali
        deleteOnceRef.current = true;

        try {
            await fetch(`http://localhost:8080/api/users/${userData.username}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${userData.token}`
                }
            });
            console.log("Akun dihapus karena keluar halaman");
        } catch (err) {
            console.error("Gagal menghapus akun:", err);
        } finally {
            navigate('/signup');
        }
    }, [userData, navigate]);

    const handleCancel = async () => {
        const confirmed = window.confirm("Yakin ingin membatalkan dan menghapus akun?");
        if (!confirmed) return;
        await deleteUserAndRedirect();
    };

    const handleFinish = async () => {
        const finalData = {
            username: userData?.username,
            genres: selectedGenres
        };

        try {
            const res = await fetch('http://localhost:8080/api/users/preferences', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${userData.token}`
                },
                body: JSON.stringify(finalData)
            });

            const data = await res.json();

            if (res.ok) {
                console.log('Preferensi berhasil disimpan:', data);
                deleteOnceRef.current = true; // ✅ Jangan hapus kalau sudah selesai
                navigate('/signin');
            } else {
                alert(data?.error?.message || 'Gagal menyimpan preferensi');
            }
        } catch {
            alert('Tidak dapat terhubung ke server');
        }
    };

    // ✅ Trigger jika user menekan Back atau keluar dari halaman
    useEffect(() => {
        const handleBeforeUnload = () => {
            deleteUserAndRedirect();
        };

        const handleVisibilityChange = () => {
            if (document.visibilityState === 'hidden') {
                deleteUserAndRedirect();
            }
        };

        window.addEventListener('popstate', deleteUserAndRedirect);
        window.addEventListener('beforeunload', handleBeforeUnload);
        document.addEventListener('visibilitychange', handleVisibilityChange);

        return () => {
            window.removeEventListener('popstate', deleteUserAndRedirect);
            window.removeEventListener('beforeunload', handleBeforeUnload);
            document.removeEventListener('visibilitychange', handleVisibilityChange);
        };
    }, [deleteUserAndRedirect]);

    const toggleGenre = (genre) => {
        setSelectedGenres((prev) =>
            prev.includes(genre) ? prev.filter((g) => g !== genre) : [...prev, genre]
        );
    };

    // 🧩 UI tetap sama
    return (
        <div className="min-h-screen bg-white p-12">
            <h2 className="text-2xl font-bold mb-8 text-center">Choose Your Preferred Genres</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-6xl mx-auto">
                {Object.entries(genreGroups).map(([group, genres]) => (
                    <div key={group} className="border rounded p-4 space-y-3">
                        <h3 className="font-semibold text-lg mb-2">
                            {group === 'Fiction' ? 'Fiction Genres' :
                                group === 'NonFiction' ? 'Non-Fiction Genres' :
                                    'Special Collections & Formats'}
                        </h3>
                        {genres.map((genre) => (
                            <label key={genre} className="flex items-start gap-2 cursor-pointer">
                                <input
                                    type="checkbox"
                                    checked={selectedGenres.includes(genre)}
                                    onChange={() => toggleGenre(genre)}
                                />
                                <div>
                                    <span className="font-medium">{genre}</span>
                                    <p className="text-sm text-gray-500">
                                        {genreDescriptions[genre] || 'No description'}
                                    </p>
                                </div>
                            </label>
                        ))}
                    </div>
                ))}
            </div>
            <div className="flex justify-between max-w-6xl mx-auto mt-10">
                <button onClick={handleCancel} className="px-6 py-2 bg-gray-200 rounded">Batal</button>
                <button onClick={handleFinish} className="px-6 py-2 bg-indigo-500 text-white rounded">Selesai</button>
            </div>
        </div>
    );
}

const genreGroups = {
    Fiction: ['Fantasy', 'Science Fiction', 'Thriller', 'Mystery', 'Romance', 'Horror', 'Historical Fiction'],
    NonFiction: ['Biography', 'History', 'Science', 'Self-Help', 'Business', 'Travel', 'Culinary'],
    Special: ['Young Adult', 'Graphic Novels', 'Short Stories', 'Poetry']
};

const genreDescriptions = {
    Fantasy: 'Epic tales of magic, mythical creatures, and wondrous lands.',
    'Science Fiction': 'Explorations of future technologies, space, and speculative societies.',
    Thriller: 'Suspenseful plots, intense action, and unexpected twists.',
    Mystery: 'Intriguing puzzles, crime investigations, and deductive reasoning.',
    Romance: 'Emotional stories focusing on love and relationships.',
    Horror: 'Chilling narratives designed to evoke fear and dread.',
    'Historical Fiction': 'Stories set in the past, blending real events with fictional characters.',
    Biography: "Accounts of real people's lives and experiences.",
    History: 'Chronicles of past events, civilizations, and historical figures.',
    Science: 'Explanations of scientific concepts, discoveries, and theories.',
    'Self-Help': 'Guides for personal growth, well-being, and skill development.',
    Business: 'Insights into economics, management, and entrepreneurial ventures.',
    Travel: 'Experiences, guides, and narratives about exploring the world.',
    Culinary: 'Books about cooking, food history, and gastronomic culture.',
    'Young Adult': 'Coming-of-age themes for adolescent readers.',
    'Graphic Novels': 'Tales told through sequential art and text.',
    'Short Stories': 'Concise, focused narratives, often exploring a single theme.',
    Poetry: 'Artistic expression through verse, rhythm, and imagery.'
};
