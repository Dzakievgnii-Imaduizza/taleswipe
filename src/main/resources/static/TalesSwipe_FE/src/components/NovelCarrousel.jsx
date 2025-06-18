export default function NovelCarousel({ admin }) {
    return (
        <div className="bg-white rounded-xl shadow p-6 w-full max-w-3xl">
            <h2 className="text-xl font-bold mb-4 text-center">Novel</h2>
            <div className="flex items-center gap-4 overflow-x-auto">
                {[...Array(3)].map((_, i) => (
                    <div key={i} className="w-40 shrink-0">
                        <img src="/novel.jpg" className="rounded" />
                        <p className="font-semibold text-sm mt-2">Kala Itu Langit Biru</p>
                        <p className="text-xs text-gray-500">Romance, Slice of Life</p>
                    </div>
                ))}
            </div>
            {admin && (
                <div className="flex justify-center gap-4 mt-4">
                    <button className="bg-indigo-500 text-white px-4 py-2 rounded">Edit Novel</button>
                    <button className="bg-indigo-500 text-white px-4 py-2 rounded">Tambah Novel</button>
                    <button className="bg-indigo-500 text-white px-4 py-2 rounded">Hapus Novel</button>
                </div>
            )}
        </div>
    );
}