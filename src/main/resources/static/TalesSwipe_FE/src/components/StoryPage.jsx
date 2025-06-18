
import React, { useState, useEffect } from 'react';
import { useSwipeable } from 'react-swipeable';

export default function StoryPage({ pages, maxSlides, onLimitReached }) {
    const [currentPage, setCurrentPage] = useState(0);
    const [direction, setDirection] = useState('');

    useEffect(() => {
        setCurrentPage(0);
    }, [pages]);

    const isLimited = typeof maxSlides === 'number';

    const swipeHandlers = useSwipeable({
        onSwipedLeft: () => {
            if (currentPage < pages.length - 1) {
                if (isLimited && currentPage + 1 >= maxSlides) {
                    if (onLimitReached) onLimitReached();
                } else {
                    setDirection('left');
                    setCurrentPage((p) => p + 1);
                }
            }
        },
        onSwipedRight: () => {
            if (currentPage > 0) {
                setDirection('right');
                setCurrentPage((p) => p - 1);
            }
        },
        preventScrollOnSwipe: true,
        trackMouse: true,
    });

    return (
        <div
            {...swipeHandlers}
            className="h-200 w-150 flex items-center justify-center bg-neutral-100 shadow rounded p-10 text-center font-serif text-black overflow-hidden relative"
        >
            <div
                key={currentPage}
                className={`absolute transition-all duration-500 ease-in-out w-full
          ${direction === 'left' ? 'animate-slide-left' : direction === 'right' ? 'animate-slide-right' : ''}`}
            >
                <p className="text-lg leading-relaxed max-w-xl mx-auto">
                    {pages[currentPage]?.content}
                </p>
            </div>
        </div>
    );
}
