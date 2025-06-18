import React, { useState, useEffect, useMemo } from 'react';
import { useSwipeable } from 'react-swipeable';
import { useLocation, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import BookInfo from '../components/BookInfo';
import StoryPage from '../components/StoryPage';
import Author from '../components/Author';
import ReactionButtons from '../components/ReactionButtons';
import CommentSection from '../components/CommentSection';

const getToken = () => localStorage.getItem('token');

function useQuery() {
    return new URLSearchParams(useLocation().search);
}

export default function Dashboard() {
    const query = useQuery();
    const navigate = useNavigate();

    const reelsParam = query.get('reels');
    const reelsList = useMemo(() => (reelsParam ? reelsParam.split(',') : null), [reelsParam]);

    const [novels, setNovels] = useState([]);
    const [activeIndex, setActiveIndex] = useState(0);
    const [novelDirection, setNovelDirection] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [swipeCount, setSwipeCount] = useState(0);
    const [offset, setOffset] = useState(0);
    const [isFetching, setIsFetching] = useState(false);
    const [hasMore, setHasMore] = useState(true);

    const [showReportModal, setShowReportModal] = useState(false);
    const [reportType, setReportType] = useState(null);
    const [reportReason, setReportReason] = useState('');

    // Reset feed saat reelsList berubah
    useEffect(() => {
        setNovels([]);
        setActiveIndex(0);
        setSwipeCount(0);
        setOffset(0);
        setHasMore(true);
    }, [reelsList]);

    // Untuk refresh data author (followers/followed)
    const fetchActiveNovel = async () => {
        const storyId = novels[activeIndex]?.storyId;
        if (!storyId) return;
        try {
            const res = await fetch(`http://localhost:8080/api/stories/${storyId}`, {
                headers: { 'Authorization': `Bearer ${getToken()}` }
            });
            const data = await res.json();
            setNovels(prev => {
                const copy = [...prev];
                copy[activeIndex] = data.data || data;
                return copy;
            });
        } catch (err) {
            console.error('FETCH ERROR:', err);
        }
    };

    useEffect(() => {
        if (reelsList && reelsList.length > 0) {
            Promise.all(
                reelsList.map(storyId =>
                    fetch(`http://localhost:8080/api/stories/${storyId}`, {
                        headers: { 'Authorization': `Bearer ${getToken()}` }
                    })
                        .then(res => res.json())
                        .then(res => res.data || res)
                        .catch(() => null)
                )
            ).then(results => {
                setNovels(results.filter(Boolean));
            });
        } else if (!reelsList) {
            fetchStories(0, 10);
        }
        // eslint-disable-next-line
    }, [reelsList]);

    const fetchStories = async (startOffset, limit) => {
        setIsFetching(true);
        try {
            const res = await fetch(`http://localhost:8080/api/feed?offset=${startOffset}&limit=${limit}`, {
                headers: { 'Authorization': `Bearer ${getToken()}` }
            });
            const data = await res.json();
            if (data && data.length > 0) {
                setNovels(prev => [...prev, ...data]);
                setOffset(startOffset + limit);
                setHasMore(data.length === limit);
            } else {
                setHasMore(false);
            }
        } catch (err) {
            console.error('Gagal fetch:', err);
        }
        setIsFetching(false);
    };

    // Swipe logic
    const swipeHandlers = useSwipeable({
        onSwipedUp: () => {
            if (activeIndex < novels.length - 1) {
                setNovelDirection('up');
                setActiveIndex(i => {
                    const newIndex = i + 1;
                    if (!reelsList) {
                        const newSwipeCount = swipeCount + 1;
                        setSwipeCount(newSwipeCount);
                        if (newSwipeCount % 10 === 0 && hasMore && !isFetching) {
                            fetchStories(offset, 10);
                        }
                    }
                    return newIndex;
                });
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

    if (!novels.length) return <div>Loading novels...</div>;
    const activeNovel = novels[activeIndex];
    if (!activeNovel) return <div>Loading novel...</div>;

    const handleReadClick = () => {
        navigate(`/read/${activeNovel.storyId}`);
    };
    const handleLimitReached = () => {
        setShowModal(true);
    };

    // Report modal logic
    const openReportModal = (type) => {
        setReportType(type);
        setShowReportModal(true);
    };
    const handleReportSubmit = () => {
        // Kirim ke backend jika ada API report
        setReportReason('');
        setShowReportModal(false);
    };

    // ---- UI ----
    return (
        <div className="min-h-screen bg-white flex flex-col">
            <Navbar />
            <main className="flex flex-1 gap-6 p-6 items-stretch" {...swipeHandlers}>
                <div key={activeIndex} className={`flex flex-1 gap-6 transition-all duration-500 ease-in-out
                    ${novelDirection === 'up' ? 'animate-slide-up' : novelDirection === 'down' ? 'animate-slide-down' : ''}`}>
                    <div className="w-1/4 h-full">
                        <BookInfo
                            title={activeNovel.title}
                            cover={activeNovel.coverUrl}
                            description={activeNovel.description}
                            onReadClick={handleReadClick}
                            onReport={() => openReportModal('novel')}
                        />
                    </div>
                    <div className="flex-1 flex justify-center items-center h-full">
                        <StoryPage
                            pages={activeNovel.pages}
                            maxSlides={4}
                            onLimitReached={handleLimitReached}
                        />
                    </div>
                    <div className="w-1/4 space-y-4 h-full">
                        {/* Author (jika perlu tampilkan following count, silakan di Author component saja) */}
                        <Author
                            {...activeNovel.author}
                            onReport={() => openReportModal('author')}
                            onFollowChange={fetchActiveNovel}
                        // onMyFollowingChange prop dihapus jika tidak perlu
                        />

                        <ReactionButtons
                            novelId={activeNovel.storyId}
                            likes={activeNovel.reactions?.likeCount || 0}
                            bookmarks={activeNovel.reactions?.bookmarkCount || 0}
                            likedByCurrentUser={activeNovel.likedByCurrentUser}
                            bookmarkedByCurrentUser={activeNovel.bookmarkedByCurrentUser}
                            onBookmarkToggled={fetchActiveNovel}
                            onLikeToggled={fetchActiveNovel}
                        />

                        <CommentSection
                            novelId={activeNovel.storyId}
                            initialComments={activeNovel.comments}
                        />
                    </div>
                </div>
                {!reelsList && isFetching && (
                    <div className="absolute bottom-0 left-0 right-0 flex justify-center p-2 z-20">
                        <span className="bg-white rounded px-4 py-2 shadow text-indigo-600">Loading more stories...</span>
                    </div>
                )}
            </main>
            {/* Modal lanjut baca */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
                    <div className="bg-white p-6 rounded shadow-lg text-center w-96">
                        <h2 className="text-xl font-semibold mb-4">Lanjutkan Baca</h2>
                        <p className="mb-4">Untuk membaca lebih lanjut, silakan buka halaman lengkap novel.</p>
                        <div className="flex justify-center gap-4">
                            <button onClick={() => setShowModal(false)} className="px-4 py-2 bg-gray-300 rounded">Tutup</button>
                            <button onClick={handleReadClick} className="px-4 py-2 bg-indigo-600 text-white rounded">Lanjut Baca</button>
                        </div>
                    </div>
                </div>
            )}
            {/* Modal report */}
            {showReportModal && (
                <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
                    <div className="bg-white p-6 rounded shadow-lg text-center w-96">
                        <h2 className="text-xl font-semibold mb-4">Report {reportType === 'novel' ? 'Novel' : 'Author'}</h2>
                        <textarea
                            className="w-full h-24 p-2 border border-gray-300 rounded mb-4"
                            placeholder="Tuliskan alasan report Anda..."
                            value={reportReason}
                            onChange={e => setReportReason(e.target.value)}
                        />
                        <div className="flex justify-center gap-4">
                            <button
                                onClick={() => setShowReportModal(false)}
                                className="px-4 py-2 bg-gray-300 rounded"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleReportSubmit}
                                disabled={reportReason.trim() === ''}
                                className="px-4 py-2 bg-red-600 text-white rounded disabled:opacity-50"
                            >
                                Submit Report
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
