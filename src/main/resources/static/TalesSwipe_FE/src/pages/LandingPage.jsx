import React, { useState, useEffect } from 'react';
import { useSwipeable } from 'react-swipeable';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import BookInfo from '../components/BookInfo';
import StoryPage from '../components/StoryPage';
import Author from '../components/Author';
import ReactionButtons from '../components/ReactionButtons';
import CommentSection from '../components/CommentSection';

export default function LandingPage() {
    const [novels, setNovels] = useState([]);
    const [activeIndex, setActiveIndex] = useState(0);
    const [novelDirection, setNovelDirection] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetch('/novelData.json')
            .then(res => res.json())
            .then(data => setNovels(data.novels))
            .catch(err => console.error(err));
    }, []);

    const isAuthenticated = false; // simulasi login
    const MAX_FREE_SCROLL = 1; // maksimal swipe gratis

    const swipeHandlers = useSwipeable({
        onSwipedUp: () => {
            if (activeIndex < novels.length - 1) {
                if (!isAuthenticated && activeIndex >= MAX_FREE_SCROLL) {
                    navigate('/signin');
                } else {
                    setNovelDirection('up');
                    setActiveIndex(i => i + 1);
                }
            }
        },
        onSwipedDown: () => {
            if (activeIndex > 0) {
                setNovelDirection('down');
                setActiveIndex(i => i - 1);
            }
        },
        preventScrollOnSwipe: true,
        trackTouch: true,
        trackMouse: true
    });

    if (novels.length === 0) return <div className="text-center mt-10">Loading...</div>;

    const activeNovel = novels[activeIndex];

    // Fungsi redirect kalau belum login
    const handleProtectedAction = () => {
        if (!isAuthenticated) navigate('/signin');
    };

    return (
        <div className="min-h-screen bg-white flex flex-col">
            <Navbar />
            <main className="flex flex-1 gap-6 p-6 items-stretch" {...swipeHandlers}>
                {/* LEFT */}
                <div className="w-1/4 h-full">
                    <div className="h-full flex flex-col justify-between">
                        <BookInfo
                            title={activeNovel.title}
                            cover={activeNovel.cover}
                            description={activeNovel.description}
                            onReadClick={handleProtectedAction}
                        />
                    </div>
                </div>

                {/* CENTER */}
                <div className="flex-1 flex justify-center items-center h-full">
                    <div
                        key={activeIndex}
                        className={`w-full h-full flex items-center justify-center transition-all duration-500 ease-in-out 
            ${novelDirection === 'up' ? 'animate-slide-up' : novelDirection === 'down' ? 'animate-slide-down' : ''}`}
                    >
                        <StoryPage pages={activeNovel.pages} />
                    </div>
                </div>

                {/* RIGHT */}
                <div className="w-1/4 space-y-4 h-full">
                    <Author
                        name={activeNovel.author.name}
                        avatar={activeNovel.author.avatar}
                        bio={activeNovel.author.bio}
                        onFollowClick={handleProtectedAction}
                    />
                    <ReactionButtons
                        novelId={activeNovel.id}
                        likes={activeNovel.reactions.likes}
                        bookmarks={activeNovel.reactions.bookmarks}
                        onLike={handleProtectedAction}
                        onBookmark={handleProtectedAction}
                    />
                    <CommentSection
                        novelId={activeNovel.id}
                        initialComments={activeNovel.comments}
                        onComment={handleProtectedAction}
                    />
                </div>
            </main>
        </div>
    );
}
