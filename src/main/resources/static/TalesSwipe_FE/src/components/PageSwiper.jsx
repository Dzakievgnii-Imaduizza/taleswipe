import { Swiper, SwiperSlide } from 'swiper/react';
import { Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/pagination';
import StoryPage from './StoryPage';

export default function PageSwiper({ pages }) {
    return (
        <Swiper
            modules={[Pagination]}
            pagination={{ clickable: true }}
            slidesPerView={1}
            className="w-full h-full"
            nested={true} // penting agar swipe horizontal tidak ganggu swipe vertical
        >
            {pages.map((page, idx) => (
                <SwiperSlide key={idx} className="h-full">
                    <StoryPage content={page} />
                </SwiperSlide>
            ))}
        </Swiper>
    );
}
