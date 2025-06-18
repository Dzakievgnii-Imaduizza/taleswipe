import React from 'react';

export default function AuthorProfile({ author, followed, followerCount, onFollowToggle }) {
    // Data dari backend (UserResponse) sudah mengandung profilePicture, fullname (name), followerCount, followedByCurrentUser
    // Kamu bisa sesuaikan sesuai preferensi style.

    // Placeholder statistik
    const likes = author.totalLikes || 0;       // Tambah ini di backend jika ingin support likes penulis
    const following = author.followingCount || 0; // Tambah ini di backend jika ingin support following penulis
    const views = author.totalViews || 0;         // Tambah ini di backend jika ingin support views penulis

    return (
        <div className="bg-white rounded-lg shadow p-8 w-80 mx-auto text-center h-125">
            {/* Gunakan foto profil dari API jika ada, fallback ke initial */}
            {author.profilePicture ? (
                <img
                    src={author.profilePicture}
                    alt={author.fullname || author.username}
                    className="w-24 h-24 rounded-full object-cover mx-auto mb-6 border-2 border-indigo-500"
                />
            ) : (
                <div className="w-24 h-24 rounded-full bg-indigo-600 text-white text-5xl flex items-center justify-center mx-auto mb-6 select-none">
                    {(author.fullname || author.username || "A")[0].toUpperCase()}
                </div>
            )}
            <h2 className="text-xl font-bold mb-2">{author.fullname || author.username}</h2>
            {/* Tambahkan email jika ingin */}
            <p className="text-gray-500 text-sm mb-4">{author.email}</p>
            {/* Tambahkan bio jika sudah ada di API */}
            {author.bio && <p className="text-gray-600 text-sm mb-6">{author.bio}</p>}

            <div className="grid grid-cols-2 gap-y-4 gap-x-12 mb-6 text-center">
                <div>
                    <p className="text-2xl font-semibold">{followerCount?.toLocaleString() ?? 0}</p>
                    <p className="text-sm text-gray-600">Followers</p>
                </div>
                <div>
                    <p className="text-2xl font-semibold">{likes.toLocaleString()}</p>
                    <p className="text-sm text-gray-600">Likes</p>
                </div>
                <div>
                    <p className="text-2xl font-semibold">{following.toLocaleString()}</p>
                    <p className="text-sm text-gray-600">Following</p>
                </div>
                <div>
                    <p className="text-2xl font-semibold">{views.toLocaleString()}</p>
                    <p className="text-sm text-gray-600">Views</p>
                </div>
            </div>
            <button
                onClick={onFollowToggle}
                className={`bg-indigo-600 text-white rounded px-6 py-2 w-full text-lg font-semibold hover:bg-indigo-700 transition`}
            >
                {followed ? 'Unfollow' : 'Follow'}
            </button>
        </div>
    );
}
