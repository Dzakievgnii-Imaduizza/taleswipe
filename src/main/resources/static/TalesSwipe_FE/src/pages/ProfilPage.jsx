import { useRef, useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaChevronLeft, FaChevronRight, FaEye, FaHeart, FaCamera } from 'react-icons/fa';
import Navbar from '../components/Navbar';

const defaultAvatar = 'https://i.pravatar.cc/100?img=1';

function formatNumber(num) {
    if (num >= 1000000) return (num / 1000000).toFixed(1) + "M";
    if (num >= 1000) return (num / 1000).toFixed(1) + "K";
    return num;
}

export default function Profile() {
    const navigate = useNavigate();
    const novelSliderRef = useRef(null);

    // Auth & user info
    const userId = localStorage.getItem('userId');
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');

    // State
    const [profileName, setProfileName] = useState('');
    const [tempName, setTempName] = useState('');
    const [editingName, setEditingName] = useState(false);
    const [profileAvatar, setProfileAvatar] = useState(defaultAvatar);
    const [loading, setLoading] = useState(false);
    const [followers, setFollowers] = useState(0);
    const [following, setFollowing] = useState(0);
    const [followingAuthors, setFollowingAuthors] = useState([]);
    const [likes, setLikes] = useState(0);
    const [novels, setNovels] = useState([]);
    const [selectedNovelId, setSelectedNovelId] = useState(null);

    // --- Fetch Profile ---
    const fetchProfile = useCallback(() => {
        if (!userId || !token) return;
        setLoading(true);
        fetch(`http://localhost:8080/api/users/by-id/${userId}`, {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then(res => res.json())
            .then(json => {
                const user = json.data;
                setProfileName(user?.fullname || user?.name || 'User');
                setTempName(user?.fullname || user?.name || 'User');
                setProfileAvatar(user?.profilePicture || defaultAvatar);
                setFollowers(user?.followerCount || 0);
                setFollowing(user?.followingCount || 0);
                setLikes(user?.totalLikes || 0);
            })
            .catch(() => alert('Gagal mengambil data profil'))
            .finally(() => setLoading(false));
    }, [userId, token]);

    // Fetch following authors list
    const fetchFollowingAuthors = useCallback(() => {
        if (!userId || !token) return;
        fetch(`http://localhost:8080/api/users/by-id/${userId}/following`, {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(json => setFollowingAuthors(json.data || []))
            .catch(() => setFollowingAuthors([]));
    }, [userId, token]);

    useEffect(() => { fetchProfile(); fetchFollowingAuthors(); }, [fetchProfile, fetchFollowingAuthors]);

    // --- Fetch stories langsung setelah username/token siap ---
    useEffect(() => {
        if (!token || !username) return;
        fetch(`http://localhost:8080/api/stories/author/${username}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => setNovels(Array.isArray(data) ? data : data.data || []))
            .catch(() => setNovels([]));
    }, [token, username]);

    // --- Slider Scroll ---
    const scrollNovels = (dir) => {
        const amount = 260;
        novelSliderRef.current?.scrollBy({ left: dir === 'left' ? -amount : amount, behavior: 'smooth' });
    };

    // --- Handle edit novel ---
    const handleEdit = () => {
        if (selectedNovelId === null) {
            alert('Pilih salah satu novel terlebih dahulu.');
            return;
        }
        navigate(`/edit-novel/${selectedNovelId}`);
    };

    // --- Handle delete novel ---
    const handleDelete = async () => {
        if (selectedNovelId === null) {
            alert('Pilih salah satu novel terlebih dahulu.');
            return;
        }
        const confirmDelete = window.confirm('Yakin ingin menghapus novel ini?');
        if (confirmDelete) {
            try {
                const resp = await fetch(`http://localhost:8080/api/stories/${selectedNovelId}`, {
                    method: "DELETE",
                    headers: { Authorization: `Bearer ${token}` }
                });
                if (resp.ok) {
                    setNovels(prev => prev.filter(novel => (novel.storyId || novel.id) !== selectedNovelId));
                    setSelectedNovelId(null);
                } else {
                    alert("Gagal menghapus novel (server)");
                }
            } catch {
                alert("Gagal menghapus novel (network)");
            }
        }
    };

    // --- Handle save name ---
    const handleSaveName = async () => {
        if (tempName.trim().length < 3) {
            alert("Nama minimal 3 karakter.");
            return;
        }
        try {
            const res = await fetch(
                `http://localhost:8080/api/users/by-id/${userId}/update?name=${encodeURIComponent(tempName)}`,
                {
                    method: "PUT",
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            if (res.ok) {
                setProfileName(tempName);
                setEditingName(false);
                fetchProfile(); // refresh!
            } else {
                alert('Gagal update nama');
            }
        } catch {
            alert('Error update nama');
        }
    };

    // --- Handle upload avatar ---
    const handleUploadAvatar = async (file) => {
        if (!file?.type.startsWith("image/")) {
            alert("Mohon upload file gambar.");
            return;
        }
        const formData = new FormData();
        formData.append("file", file);
        try {
            const res = await fetch(
                `http://localhost:8080/api/users/by-id/${userId}/profile-picture`,
                {
                    method: "PUT",
                    headers: { Authorization: `Bearer ${token}` },
                    body: formData,
                }
            );
            if (res.ok) {
                const json = await res.json();
                setProfileAvatar(json.data?.data || defaultAvatar);
                fetchProfile(); // refresh!
            } else {
                alert('Gagal upload foto');
            }
        } catch {
            alert('Error upload foto');
        }
    };

    // --- Handle reel click: go to dashboard with params ---
    const handleReelClick = (clickedStoryId) => {
        const reelIds = novels.map(novel => novel.storyId || novel.id).join(',');
        navigate(`/dashboard?reels=${reelIds}&current=${clickedStoryId}`);
    };

    // --- Render
    return (
        <>
            <header>
                <Navbar />
            </header>
            <div className="min-h-screen bg-white text-black px-10 py-8">
                {loading && <div className="text-center text-gray-500 py-4">Memuat profil...</div>}
                <div className="grid grid-cols-3 gap-6">
                    {/* PROFILE INFO */}
                    <div className="bg-white rounded shadow p-6 flex flex-col items-center text-center">
                        <div className="relative w-24 h-24 mb-2">
                            <img
                                src={`http://localhost:8080${profileAvatar}`}
                                className="w-full h-full rounded-full object-cover border-2 border-gray-300"
                                alt="avatar"
                            />
                            <label className="absolute bottom-0 right-0 bg-indigo-500 p-1 rounded-full cursor-pointer">
                                <FaCamera className="text-white text-sm" />
                                <input
                                    type="file"
                                    accept="image/*"
                                    className="hidden"
                                    onChange={e => handleUploadAvatar(e.target.files[0])}
                                />
                            </label>
                        </div>
                        {editingName ? (
                            <div className="w-full">
                                <input
                                    className="text-center font-semibold text-lg border border-gray-300 rounded px-2 py-1 w-full"
                                    value={tempName}
                                    onChange={e => setTempName(e.target.value)}
                                />
                                <button
                                    className="text-sm mt-2 bg-indigo-500 text-white px-4 py-1 rounded"
                                    onClick={handleSaveName}
                                >
                                    Simpan
                                </button>
                            </div>
                        ) : (
                            <div>
                                <h2 className="text-lg font-semibold">{profileName}</h2>
                                <button
                                    className="text-sm text-indigo-500 mt-1 underline"
                                    onClick={() => setEditingName(true)}
                                >
                                    Edit Nama
                                </button>
                            </div>
                        )}

                        {/* FOLLOWERS, LIKE, FOLLOWING */}
                        <div className="grid grid-cols-2 gap-x-10 gap-y-2 text-sm text-center mt-4">
                            <div>
                                <p className="font-semibold">{formatNumber(followers)}</p>
                                <p className="text-gray-600">Followers</p>
                            </div>
                            <div>
                                <p className="font-semibold">{formatNumber(likes)}</p>
                                <p className="text-gray-600">Like</p>
                            </div>
                            <div>
                                <p className="font-semibold">{formatNumber(following)}</p>
                                <p className="text-gray-600">Following</p>
                            </div>
                        </div>

                        {/* DAFTAR FOLLOWING */}
                        <div className="w-full mt-6 text-left">
                            <h3 className="font-semibold mb-2 text-gray-700">Following</h3>
                            <ul className="max-h-32 overflow-y-auto pr-2 text-sm">
                                {followingAuthors.length === 0 ? (
                                    <li className="text-gray-400 italic">Belum mengikuti siapapun.</li>
                                ) : (
                                    followingAuthors.map(author => (
                                        <li key={author.userId} className="flex items-center gap-2 py-1">
                                            <img
                                                src={author.profilePicture || defaultAvatar}
                                                alt={author.fullname}
                                                className="w-6 h-6 rounded-full object-cover"
                                            />
                                            <span>{author.fullname || author.name} <span className="text-xs text-gray-400">({author.username})</span></span>
                                        </li>
                                    ))
                                )}
                            </ul>
                        </div>
                    </div>
                    {/* NOVEL SLIDER */}
                    <div className="col-span-2 bg-white rounded shadow p-6">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-lg font-semibold">Novel</h2>
                            <div className="flex gap-2">
                                <button onClick={() => scrollNovels('left')}><FaChevronLeft /></button>
                                <button onClick={() => scrollNovels('right')}><FaChevronRight /></button>
                            </div>
                        </div>
                        <div className="relative">
                            <div ref={novelSliderRef} className="flex overflow-x-auto gap-4 no-scrollbar pb-2">
                                {novels.length === 0 ? (
                                    <p className="text-gray-400 italic py-10">Belum ada novel.</p>
                                ) : (
                                    novels.map((novel) => (
                                        <div
                                            key={novel.storyId || novel.id}
                                            onClick={() => setSelectedNovelId(novel.storyId || novel.id)}
                                            onDoubleClick={() => navigate(`/read/${novel.storyId || novel.id}`)}
                                            className={`w-40 shrink-0 rounded shadow p-3 text-center cursor-pointer border-2 transition ${(novel.storyId === selectedNovelId || novel.id === selectedNovelId) ? 'border-indigo-500' : 'border-transparent'
                                                }`}
                                        >
                                            <img src={`http://localhost:8080${novel.coverUrl}`} alt={novel.title} className="w-full h-48 object-cover rounded mb-2" />
                                            <h3 className="text-sm font-semibold">{novel.title}</h3>
                                            <p className="text-xs text-gray-600">{Array.isArray(novel.genres) ? novel.genres.join(', ') : novel.genre}</p>
                                        </div>
                                    ))
                                )}
                            </div>
                            <div className="mt-4 flex justify-center gap-4">
                                <button onClick={handleEdit} className="bg-indigo-500 text-white text-sm px-4 py-2 rounded">Edit Novel</button>
                                <button onClick={() => navigate('/addnovel')} className="bg-indigo-500 text-white text-sm px-4 py-2 rounded">Tambah Novel</button>
                                <button onClick={handleDelete} className="bg-red-500 text-white text-sm px-4 py-2 rounded">Hapus Novel</button>
                            </div>
                        </div>
                    </div>
                </div>
                {/* REELS */}
                <div className="mt-10 bg-white rounded shadow p-6">
                    <h2 className="text-lg font-semibold mb-4">Reels</h2>
                    <div className="flex overflow-x-auto gap-4 no-scrollbar">
                        {novels.length === 0 ? (
                            <p className="text-gray-400 italic py-10">Belum ada reels.</p>
                        ) : (
                            novels.map((reel) => (
                                <div
                                    key={reel.storyId || reel.id}
                                    className="w-36 shrink-0 text-center cursor-pointer"
                                    onClick={() => handleReelClick(reel.storyId || reel.id)}
                                >
                                    <img src={`http://localhost:8080${reel.coverUrl}`} className="w-full h-60 object-cover rounded" alt={reel.title} />
                                    <h3 className="text-sm mt-2 font-semibold">{reel.title}</h3>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </div>
        </>
    );
}
