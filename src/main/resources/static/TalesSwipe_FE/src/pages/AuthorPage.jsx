import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { FaEye, FaHeart } from 'react-icons/fa';
import AuthorProfile from '../components/AuthorProfile';

const getToken = () => localStorage.getItem('token');

export default function AuthorPage() {
    const { authorName } = useParams();
    const [author, setAuthor] = useState(null);
    const [novels, setNovels] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        setLoading(true);

        // Fetch author data
        fetch(`http://localhost:8080/api/users/by-username/${authorName}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        })
            .then(res => res.json())
            .then(res => {
                if (res.data) setAuthor(res.data);
            });

        // Fetch novels
        fetch(`http://localhost:8080/api/stories/author/${authorName}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        })
            .then(res => res.json())
            .then(data => {
                setNovels(data);
                setLoading(false);
            });

    }, [authorName]);

    // Handler untuk follow/unfollow
    const handleFollowToggle = async () => {
        if (!author) return;
        const endpoint = `http://localhost:8080/api/users/${authorName}/follow`;
        const method = author.followedByCurrentUser ? 'DELETE' : 'POST';

        await fetch(endpoint, {
            method,
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        // Refresh author data
        fetch(`http://localhost:8080/api/users/by-username/${authorName}`, {
            headers: { 'Authorization': `Bearer ${getToken()}` }
        })
            .then(res => res.json())
            .then(res => {
                if (res.data) setAuthor(res.data);
            });
    };

    // Handler klik novel: redirect ke ReadPage
    const handleNovelClick = (storyId) => {
        navigate(`/read/${storyId}`);
    };

    // Handler klik reel
    const handleReelClick = (clickedStoryId) => {
        const reelIds = novels.map(novel => novel.storyId).join(',');
        navigate(`/dashboard?reels=${reelIds}&current=${clickedStoryId}`);
    };

    if (loading || !author) return <div>Loading author...</div>;

    return (
        <div className="min-h-screen bg-white">
            <Navbar />
            <div className="max-w-7xl mx-auto px-8 py-10">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
                    <AuthorProfile
                        author={author}
                        followed={author.followedByCurrentUser}
                        followerCount={author.followerCount}
                        onFollowToggle={handleFollowToggle}
                    />
                    {/* Novel Section */}
                    <div className="md:col-span-2 space-y-12">
                        <div className="bg-gray-50 p-6 rounded-md shadow-md">
                            <h2 className="text-3xl font-semibold mb-8">Novel</h2>
                            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
                                {novels.map((novel) => (
                                    <div
                                        key={novel.storyId}
                                        className="cursor-pointer flex flex-col items-center"
                                        onClick={() => handleNovelClick(novel.storyId)}
                                    >
                                        <img
                                            src={novel.coverUrl}
                                            alt={novel.title}
                                            className="w-36 h-48 object-cover rounded-md shadow-md mb-3"
                                        />
                                        <h3 className="font-semibold text-lg">{novel.title}</h3>
                                        <p className="text-gray-500 text-sm">
                                            {(novel.genres && novel.genres.join(', ')) || novel.description}
                                        </p>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Reels Section */}
                        <div className="bg-gray-50 p-6 rounded-md shadow-md">
                            <h2 className="text-3xl font-semibold mb-8">Reels</h2>
                            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
                                {novels.map((novel) => (
                                    <div
                                        key={novel.storyId}
                                        className="flex flex-col items-center cursor-pointer"
                                        onClick={() => handleReelClick(novel.storyId)}
                                    >
                                        <img
                                            src={novel.coverUrl}
                                            alt={novel.title}
                                            className="w-36 h-48 object-cover rounded-md shadow-md mb-3"
                                        />
                                        <h3 className="font-semibold text-lg">{novel.title}</h3>
                                        <div className="flex gap-4 text-gray-500 text-sm mt-1">
                                            <div className="flex items-center gap-1">
                                                <FaEye /> <span>{novel.reactions?.likeCount ?? 0} likes</span>
                                            </div>
                                            <div className="flex items-center gap-1">
                                                <FaHeart /> <span>{novel.reactions?.bookmarkCount ?? 0} bookmarks</span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
