import { Routes, Route } from 'react-router-dom'
import SignIn from './pages/SignIn'
import SignUp from './pages/SignUp'
import Dashboard from './pages/Dashboard'
import AuthorPage from './pages/AuthorPage';
import UserPage from './pages/UserPage';
import BookmarkPage from './pages/BookmarkPage';
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import LandingPage from './pages/LandingPage';
import GenrePreference from './pages/GenrePreference';
import Profile from './pages/ProfilPage';
import AddNovel from './pages/AddNovelPage';
import EditNovel from './pages/EditNovel';
import ReadPage from './pages/ReadPage';
import SearchPage from './pages/SearchPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/signup" element={<SignUp />} />
      <Route path="/genre-preference" element={<GenrePreference />} />
      <Route path="/signin" element={<SignIn />} />
      <Route path="/author/:authorName" element={<AuthorPage />} />
      <Route path="/user" element={<UserPage />} />
      <Route path="/bookmark" element={<BookmarkPage />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/addnovel" element={<AddNovel />} />
      <Route path="/edit-novel/:id" element={<EditNovel />} />
      <Route path="/read/:id" element={<ReadPage />} />
      <Route path="/search" element={<SearchPage />} />
    </Routes>
  )
}
