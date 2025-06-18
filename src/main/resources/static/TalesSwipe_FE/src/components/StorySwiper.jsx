// src/components/StorySwiper.jsx
import { Swiper, SwiperSlide } from 'swiper/react';
import { Mousewheel } from 'swiper/modules';
import PageSwiper from './PageSwiper';

export default function StorySwiper({ stories }) {
    return (
        <Swiper
            direction="vertical"
            modules={[Mousewheel]}
            slidesPerView={1}
            mousewheel
            style={{ height: '100vh' }}
        >
            {stories.map((story, index) => (
                <SwiperSlide key={index}>
                    <div className="h-full flex flex-col">
                        <h2 className="text-center text-2xl font-bold my-4">{story.title}</h2>
                        <PageSwiper pages={story.pages} />
                    </div>
                </SwiperSlide>
            ))}
        </Swiper>
    );
}
