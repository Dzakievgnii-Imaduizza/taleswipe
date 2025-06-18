import { useEffect, useState } from "react";
import { FaRegThumbsUp, FaThumbsUp } from "react-icons/fa";

const defaultAvatar = "https://i.pravatar.cc/100?img=6";

export default function CommentSection({ novelId }) {
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");
    const [replyingTo, setReplyingTo] = useState(null);
    const [newReply, setNewReply] = useState({});
    const [loading, setLoading] = useState(false);

    // Fetch nested comments
    useEffect(() => {
        if (!novelId) return;
        setLoading(true);
        fetch(`http://localhost:8080/api/comments/story/${novelId}/nested`, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`,
            },
        })
            .then(res => {
                if (!res.ok) throw new Error("Gagal fetch komentar");
                return res.json();
            })
            .then(setComments)
            .catch(() => setComments([]))
            .finally(() => setLoading(false));
    }, [novelId]);

    // Helper: Refresh comments
    const refreshComments = () => {
        setLoading(true);
        fetch(`http://localhost:8080/api/comments/story/${novelId}/nested`, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`,
            },
        })
            .then(res => res.json())
            .then(setComments)
            .catch(() => setComments([]))
            .finally(() => setLoading(false));
    };

    // POST new comment
    const handleAddComment = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;
        setLoading(true);
        try {
            await fetch(`http://localhost:8080/api/comments`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                },
                body: JSON.stringify({
                    storyId: novelId,
                    commentText: newComment,
                    parentCommentId: null,
                }),
            });
            setNewComment("");
            refreshComments();
        } catch {
            // Optional: handle error
        }
    };

    // POST reply (bebas berapapun level)
    const handleReply = async (commentId) => {
        const replyText = (newReply[commentId] || "").trim();
        if (!replyText) return;
        setLoading(true);
        try {
            await fetch(`http://localhost:8080/api/comments`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                },
                body: JSON.stringify({
                    storyId: novelId,
                    commentText: replyText,
                    parentCommentId: commentId,
                }),
            });
            setNewReply(prev => ({ ...prev, [commentId]: "" }));
            setReplyingTo(null);
            refreshComments();
        } catch {
            // Optional: handle error
        }
    };

    // Like/unlike
    const handleLike = async (commentId, liked) => {
        try {
            await fetch(`http://localhost:8080/api/comments/${commentId}/like`, {
                method: liked ? "DELETE" : "POST",
                headers: { "Authorization": `Bearer ${localStorage.getItem("token")}` },
            });
            refreshComments();
        } catch (err) {
            console.error(err);
        }
    };

    // Toggle reply box
    const handleReplyClick = (id) => setReplyingTo(replyingTo === id ? null : id);

    // Toggle show replies


    /**
     * Utility: Flattens the comment tree into an array for rendering,
     * with custom props for @parent when > 2 level.
     */
    function flattenComments(comments, depth = 1, parent = null) {
        let result = [];
        comments.forEach(cmt => {
            // Compose displayed text: prepend @Parent if depth > 2
            let displayedText = cmt.commentText;
            if (depth > 2 && parent) {
                displayedText = `@${parent.username}: ${cmt.commentText}`;
            }
            // Render comment (with override for flat if depth > 2)
            result.push({
                ...cmt,
                depth: depth > 2 ? 2 : depth,
                flat: depth > 2,
                displayedText,
            });
            // Recurse for replies
            if (Array.isArray(cmt.replies) && cmt.replies.length > 0) {
                result = result.concat(
                    flattenComments(cmt.replies, depth + 1, cmt)
                );
            }
        });
        return result;
    }

    const flatComments = flattenComments(comments);

    // Main comment render function
    function renderComment(cmt) {
        const {
            commentId,
            userProfilePicture,
            username,
            displayName,
            displayedText,
            likeCount,
            likedByCurrentUser,
            depth, // true jika tampil sejajar (>= level 3)
        } = cmt;
        return (
            <div key={commentId} className={`mb-3 ${depth === 2 ? "ml-6" : ""}`}>
                <div className="flex gap-3 text-sm">
                    <img src={`http://localhost:8080${cmt.userProfilePicture}`} className="w-8 h-8 rounded-full" alt={username} />
                    <div className="flex-1">
                        <p>
                            <span className="font-semibold">{displayName || username}</span>{" "}
                            <span className="text-gray-700">{displayedText}</span>
                        </p>
                        <div className="flex items-center gap-4 text-xs text-gray-500 mt-1">
                            <span>{likeCount} like</span>
                            <button
                                className="text-blue-500 hover:underline"
                                onClick={() => handleReplyClick(commentId)}
                            >
                                Reply
                            </button>
                            <button
                                onClick={() => handleLike(commentId, likedByCurrentUser)}
                                className="ml-auto"
                            >
                                {likedByCurrentUser ? (
                                    <FaThumbsUp className="text-blue-600" />
                                ) : (
                                    <FaRegThumbsUp />
                                )}
                            </button>
                        </div>
                        {/* REPLY INPUT */}
                        {replyingTo === commentId && (
                            <div className="mt-2 flex gap-2">
                                <img
                                    src={`http://localhost:8080${userProfilePicture}`}
                                    className="w-6 h-6 rounded-full"
                                    alt="avatar"
                                />
                                <input
                                    className="flex-1 p-1 border rounded-full text-sm"
                                    placeholder="Tulis balasan..."
                                    value={newReply[commentId] || ""}
                                    onChange={e =>
                                        setNewReply(prev => ({
                                            ...prev,
                                            [commentId]: e.target.value,
                                        }))
                                    }
                                />
                                <button
                                    className="text-indigo-500"
                                    onClick={() => handleReply(commentId)}
                                >
                                    ➤
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        );
    }

    // Main JSX
    return (
        <div className="bg-white p-4 rounded shadow">
            <h3 className="font-semibold text-center text-sm mb-4">Komentar</h3>
            <div className="space-y-4 h-[420px] overflow-y-auto pr-3">
                {loading && <p className="text-center text-gray-500">Memuat komentar...</p>}
                {!loading && flatComments.length === 0 && (
                    <p className="text-center text-gray-500">Belum ada komentar.</p>
                )}
                {!loading && flatComments.map(cmt => renderComment(cmt))}
            </div>
            {/* COMMENT INPUT */}
            <form
                onSubmit={handleAddComment}
                className="mt-4 flex items-center gap-2 border-t pt-3"
            >
                <img
                    src={defaultAvatar}
                    alt="avatar"
                    className="w-8 h-8 rounded-full"
                />
                <input
                    className="flex-1 p-2 border rounded-full text-sm"
                    placeholder="Tulis komentar..."
                    value={newComment}
                    onChange={e => setNewComment(e.target.value)}
                />
                <button type="submit" className="text-indigo-500 text-lg">
                    ➤
                </button>
            </form>
        </div>
    );
}
