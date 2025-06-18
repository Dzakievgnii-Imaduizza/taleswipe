export default function BookInfo({ title, cover, description, onReadClick }) {
    return (
        <div className="relative bg-white p-6 rounded-lg shadow flex flex-col items-center text-center h-[800px] w-full max-w-sm justify-between">


            <img src={`http://localhost:8080${cover}`} alt="Book cover" className="w-100 h-116 object-cover rounded" />
            <div className="space-y-2 mt-4">
                <h2 className="text-lg font-bold">{title}</h2>
                <p className="text-sm text-gray-600 leading-relaxed">{description}</p>
            </div>
            <button
                onClick={onReadClick}
                className="bg-indigo-500 hover:bg-indigo-600 text-white py-2 rounded shadow w-full"
            >
                Lanjut Baca
            </button>
        </div>
    );
}

const ReportIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" className="w-5 h-5 mx-auto">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.29 3.86L1.82 18a1 1 0 00.86 1.5h18.64a1 1 0 00.86-1.5L13.71 3.86a1 1 0 00-1.72 0zM12 9v4m0 4h.01" />
    </svg>
);
