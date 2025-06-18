import React, { useState, useEffect } from 'react';
import {
  FaHeart,
  FaRegHeart,
  FaBookmark,
  FaRegBookmark,
  FaSpinner
} from 'react-icons/fa';

const dummyStory = {
  reactions: {
    likeCount: 0,
    bookmarkCount: 0,
  },
  likedByCurrentUser: false,
  bookmarkedByCurrentUser: false,
};

export default function ReactionButtons({ novelId }) {
  const [loading, setLoading] = useState(false);
  const [story, setStory] = useState(null);
  const token = localStorage.getItem('token');

  // Fetch story detail dari API
  const fetchStory = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/stories/${novelId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setStory(data);
      } else {
        // Kalau gagal fetch, pakai dummy
        setStory(dummyStory);
      }
    } catch (err) {
      console.error(err);
      setStory(dummyStory);
    }
  };

  useEffect(() => {
    fetchStory();
    // eslint-disable-next-line
  }, [novelId]);

  // Kalau story null (belum load), bisa pakai dummy juga
  const displayStory = story || dummyStory;

  // Ambil jumlah like & bookmark dari reactions:
  const likeCount = displayStory.reactions?.likeCount ?? 0;
  const bookmarkCount = displayStory.reactions?.bookmarkCount ?? 0;

  const toggleLike = async () => {
    setLoading(true);
    try {
      await fetch(
        `http://localhost:8080/api/stories/${novelId}/like`,
        {
          method: displayStory.likedByCurrentUser ? 'DELETE' : 'POST',
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      await fetchStory();
    } catch (err) {
      console.error(err);
    }
    setLoading(false);
  };

  const toggleBookmark = async () => {
    setLoading(true);
    try {
      await fetch(
        `http://localhost:8080/api/stories/${novelId}/bookmark`,
        {
          method: displayStory.bookmarkedByCurrentUser ? 'DELETE' : 'POST',
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      await fetchStory();
    } catch (err) {
      console.error(err);
    }
    setLoading(false);
  };

  return (
    <div className="flex items-center space-x-6 mt-4 gap-10">
      {/* Like Button */}
      <button
        onClick={toggleLike}
        disabled={loading}
        className={`flex items-center space-x-1 transition-transform duration-300 ${displayStory.likedByCurrentUser ? 'text-red-500 scale-110' : 'text-gray-500'}`}
      >
        {loading ? (
          <FaSpinner className="animate-spin" />
        ) : displayStory.likedByCurrentUser ? (
          <FaHeart />
        ) : (
          <FaRegHeart />
        )}
        <span>{likeCount}</span>
      </button>
      {/* Bookmark Button */}
      <button
        onClick={toggleBookmark}
        disabled={loading}
        className={`flex items-center space-x-1 transition-transform duration-300 ${displayStory.bookmarkedByCurrentUser ? 'text-yellow-500 scale-110' : 'text-gray-500'}`}
      >
        {loading ? (
          <FaSpinner className="animate-spin" />
        ) : displayStory.bookmarkedByCurrentUser ? (
          <FaBookmark />
        ) : (
          <FaRegBookmark />
        )}
        <span>{bookmarkCount}</span>
      </button>
    </div>
  );
}
