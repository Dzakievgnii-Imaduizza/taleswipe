import Navbar from '../components/Navbar';
import UserCard from '../components/UserCard';
import NovelCarousel from '../components/NovelCarrousel';

export default function UserPage() {
    return (
        <div>
            <Navbar />
            <div className="flex gap-6 p-8">
                <UserCard />
                <NovelCarousel admin />
            </div>
        </div>
    );
}