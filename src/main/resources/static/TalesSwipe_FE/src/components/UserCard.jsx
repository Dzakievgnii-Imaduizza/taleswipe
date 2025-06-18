export default function UserCard() {
    return (
        <div className="bg-white rounded-xl shadow p-6 text-center w-64">
            <div className="w-20 h-20 bg-indigo-500 text-white text-4xl rounded-full mx-auto mb-4 flex items-center justify-center">A</div>
            <h2 className="text-lg font-semibold">User</h2>
            <p className="text-sm">12.3K Followers</p>
            <p className="text-sm mb-4">350K Like</p>
            <button className="bg-indigo-500 text-white px-4 py-2 rounded">Follow</button>
        </div>
    );
}